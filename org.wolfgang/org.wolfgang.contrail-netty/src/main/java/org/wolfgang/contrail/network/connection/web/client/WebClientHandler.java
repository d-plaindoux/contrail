/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.wolfgang.contrail.network.connection.web.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.util.CharsetUtil;
import org.wolfgang.contrail.network.connection.web.handler.WebClientSocketHandler;

/**
 * Handles handshakes and messages
 */
class WebClientHandler extends SimpleChannelUpstreamHandler {

	private final WebSocketClientHandshaker handshaker;
	private final WebClientSocketHandler wsRequestHandler;

	public WebClientHandler(WebSocketClientHandshaker handshaker, WebClientSocketHandler wsRequestHandler) {
		this.handshaker = handshaker;
		this.wsRequestHandler = wsRequestHandler;
	}

	@Override
	public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
		try {
			final Object message = event.getMessage();

			if (!handshaker.isHandshakeComplete()) {
				handshaker.finishHandshake(context.getChannel(), (HttpResponse) message);
				wsRequestHandler.notifyHandShake(context);
			} else if (message instanceof HttpResponse) {
				final HttpResponse response = (HttpResponse) event.getMessage();
				throw new Exception("Unexpected HttpResponse (status=" + response.getStatus() + ", content=" + response.getContent().toString(CharsetUtil.UTF_8) + ')');
			} else if (message instanceof WebSocketFrame) {
				final WebSocketFrame webSocketFrame = (WebSocketFrame) message;
				this.wsRequestHandler.handleWebSocketFrame(context, webSocketFrame);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}