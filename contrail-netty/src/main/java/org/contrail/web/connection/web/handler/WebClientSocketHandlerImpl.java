/*
 * Copyright (C)2012 D. Plaindoux.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.contrail.web.connection.web.handler;

import java.util.HashMap;
import java.util.Map;

import org.contrail.web.connection.exception.WebClientConnectionException;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.contrail.common.concurrent.Promise;
import org.contrail.stream.component.CannotCreateComponentException;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.Components;
import org.contrail.stream.component.SourceComponent;
import org.contrail.stream.component.bound.InitialComponent;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.DataFlowFactory;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.flow.exception.DataFlowException;

/**
 * <code>HTTPRequestHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class WebClientSocketHandlerImpl implements WebClientSocketHandler {

	/**
	 * The upstream data flow
	 */
	private Map<Integer, InitialComponent<String, String>> receivers;

	{
		this.receivers = new HashMap<Integer, InitialComponent<String, String>>();
	}

	/**
	 * Constructor
	 * 
	 * @param factory2
	 *            The factory
	 * @param serverPage
	 *            The server page
	 */
	public WebClientSocketHandlerImpl() {
	}

	@Override
	public void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws Exception {
		final int identifier = context.getChannel().getId();

		if (!this.receivers.containsKey(identifier)) {
			throw new UnsupportedOperationException(String.format("Receiver not found for channel %d", identifier));
		} else if (frame instanceof CloseWebSocketFrame) {
			this.receivers.remove(identifier).getUpStreamDataFlow().handleClose();
		} else if (frame instanceof PingWebSocketFrame) {
			try {
				this.sendWebSocketFrame(context, new PongWebSocketFrame(frame.getBinaryData()));
			} catch (Throwable e) {
				throw new WebClientConnectionException(e);
			}
		} else if (frame instanceof TextWebSocketFrame) {
			final String request = ((TextWebSocketFrame) frame).getText();
			this.receivers.get(identifier).getUpStreamDataFlow().handleData(request);
		} else {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}
	}

	@Override
	public void notifyHandShake(ChannelHandlerContext context, Promise<SourceComponent<String, String>, Exception> sourceComponentPromise) throws WebClientConnectionException {
		final int identifier = context.getChannel().getId();
		final DataFlow<String> emitter = createReceiver(context);
		try {
			this.registerIncomingConnection(identifier, emitter, sourceComponentPromise);
		} catch (Exception e) {
			throw new WebClientConnectionException(e);
		}
	}

	private DataFlow<String> createReceiver(final ChannelHandlerContext context) {
		return DataFlowFactory.<String> closable(new DataFlow<String>() {
			@Override
			public String toString() {
				return "Web Socket " + context.getName();
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				// TODO -- handshaker.close(context.getChannel(), new CloseWebSocketFrame());
			}

			@Override
			public void handleData(String data) throws DataFlowException {
				try {
					sendWebSocketFrame(context, new TextWebSocketFrame(data));
				} catch (DataFlowException e) {
					throw e;
				} catch (Throwable e) {
					throw new DataFlowException(e);
				}
			}
		});
	}

	private void registerIncomingConnection(int identifier, DataFlow<String> emitter, Promise<SourceComponent<String, String>, Exception> sourceComponentPromise)
			throws CannotCreateComponentException, ComponentNotConnectedException {
		final InitialComponent<String, String> initialComponent = Components.initial(emitter);
		sourceComponentPromise.success(initialComponent);
		receivers.put(identifier, initialComponent);
	}

	private void sendWebSocketFrame(ChannelHandlerContext context, WebSocketFrame res) throws Throwable {
		final int identifier = context.getChannel().getId();
		try {
			context.getChannel().write(res);
		} catch (Throwable e) {
			if (this.receivers.containsKey(identifier)) {
				this.receivers.remove(identifier).closeUpStream();
			}
			throw e;
		}
	}
}
