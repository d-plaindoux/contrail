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

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.SourceComponentNotifier;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowFactory;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.flow.exception.DataFlowException;

/**
 * <code>HTTPRequestHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class WebServerSocketHandlerImpl implements WebServerSocketHandler {

	/**
	 * The upstream handler
	 */
	private final SourceComponentNotifier componentSourceManager;

	/**
	 * The upstream data flow
	 */
	private Map<Integer, InitialComponent<String, String>> receivers;
	private Map<Integer, WebSocketServerHandshaker> handShakers;

	{
		this.receivers = new HashMap<Integer, InitialComponent<String, String>>();
		this.handShakers = new HashMap<Integer, WebSocketServerHandshaker>();
	}

	/**
	 * Constructor
	 * 
	 * @param factory2
	 *            The factory
	 * @param serverPage
	 *            The server page
	 */
	public WebServerSocketHandlerImpl(SourceComponentNotifier componentSourceManager) {
		this.componentSourceManager = componentSourceManager;
	}

	@Override
	public void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws Exception {
		final int identifier = context.getChannel().getId();

		if (!this.receivers.containsKey(identifier)) {
			throw new UnsupportedOperationException(String.format("Receiver not found for channel %d", identifier));
		} else if (frame instanceof CloseWebSocketFrame) {
			this.handShakers.remove(identifier).close(context.getChannel(), (CloseWebSocketFrame) frame);
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

	/**
	 * @param context
	 * @return
	 */
	private DataFlow<String> createReceiver(final WebSocketServerHandshaker handshaker, final ChannelHandlerContext context) {
		assert handshaker != null;

		return DataFlowFactory.<String> closable(new DataFlow<String>() {
			@Override
			public String toString() {
				return "Web Socket " + context.getName();
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				handshaker.close(context.getChannel(), new CloseWebSocketFrame());
			}

			@Override
			public void handleData(String data) throws DataFlowException {
				sendWebSocketFrame(context, new TextWebSocketFrame(data));
			}
		});
	}

	public ChannelFutureListener createHandShakeListener(WebSocketServerHandshaker handshaker, ChannelHandlerContext context) {
		final int identifier = context.getChannel().getId();
		final DataFlow<String> emitter = createReceiver(handshaker, context);

		handShakers.put(identifier, handshaker);

		return new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					Channels.fireExceptionCaught(future.getChannel(), future.getCause());
				} else {
					try {
						registerIncomingConnection(identifier, emitter);
					} catch (Exception e) {
						Channels.fireExceptionCaught(future.getChannel(), e);
						throw e;
					}
				}
			}
		};
	}

	private void registerIncomingConnection(int identifier, DataFlow<String> emitter) throws CannotCreateComponentException, ComponentNotConnectedException {
		final InitialComponent<String, String> initialComponent = Components.initial(emitter);
		componentSourceManager.accept(initialComponent);
		receivers.put(identifier, initialComponent);
	}

	private void sendWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame res) {
		// Send the response and close the connection if necessary.
		ctx.getChannel().write(res);
	}
}
