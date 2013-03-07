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

package org.wolfgang.contrail.network.connection.web.handler;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowFactory;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.network.connection.exception.WebClientConnectionException;

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
			this.sendWebSocketFrame(context, new PongWebSocketFrame(frame.getBinaryData()));
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
				sendWebSocketFrame(context, new TextWebSocketFrame(data));
			}
		});
	}

	private void registerIncomingConnection(int identifier, DataFlow<String> emitter, Promise<SourceComponent<String, String>, Exception> sourceComponentPromise)
			throws CannotCreateComponentException, ComponentNotConnectedException {
		final InitialComponent<String, String> initialComponent = Components.initial(emitter);
		sourceComponentPromise.success(initialComponent);
		receivers.put(identifier, initialComponent);
	}

	private void sendWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame res) {
		// Send the response and close the connection if necessary.
		ctx.getChannel().write(res);
	}
}
