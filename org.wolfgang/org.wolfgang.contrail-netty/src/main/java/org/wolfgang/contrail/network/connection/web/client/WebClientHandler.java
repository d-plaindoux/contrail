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

package org.wolfgang.contrail.network.connection.web.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.network.connection.web.handler.WebClientSocketHandler;

/**
 * Handles handshakes and messages
 */
class WebClientHandler extends SimpleChannelUpstreamHandler {

	private final Promise<Integer, Exception> connectionEstablished;
	private final WebSocketClientHandshaker handshaker;
	private final WebClientSocketHandler wsRequestHandler;
	private final Promise<SourceComponent<String, String>, Exception> sourceComponentPromise;

	public WebClientHandler(WebSocketClientHandshaker handshaker, WebClientSocketHandler wsRequestHandler, Promise<Integer, Exception> connectionEstablished,
			Promise<SourceComponent<String, String>, Exception> sourceComponentPromise) {
		this.handshaker = handshaker;
		this.wsRequestHandler = wsRequestHandler;
		this.connectionEstablished = connectionEstablished;
		this.sourceComponentPromise = sourceComponentPromise;
	}

	@Override
	public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
		try {
			final Object message = event.getMessage();

			if (!handshaker.isHandshakeComplete()) {
				try {
					handshaker.finishHandshake(context.getChannel(), (HttpResponse) message);
					wsRequestHandler.notifyHandShake(context, sourceComponentPromise);
					connectionEstablished.success(context.getChannel().getId());

					Logger.getAnonymousLogger().log(Level.INFO, "Accept client web socket [" + context.toString() + "]");
				} catch (Exception e) {
					connectionEstablished.failure(e);
				}
			} else if (message instanceof HttpResponse) {
				final HttpResponse response = (HttpResponse) event.getMessage();
				throw new Exception("Unexpected HttpResponse (status=" + response.getStatus() + ", content=" + response.getContent().toString(CharsetUtil.UTF_8) + ')');
			} else if (message instanceof WebSocketFrame) {
				final WebSocketFrame webSocketFrame = (WebSocketFrame) message;
				this.wsRequestHandler.handleWebSocketFrame(context, webSocketFrame);
			}
		} catch (Throwable t) {
			t.printStackTrace(); // TODO
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}