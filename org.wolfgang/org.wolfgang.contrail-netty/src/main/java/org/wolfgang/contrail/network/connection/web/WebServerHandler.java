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

package org.wolfgang.contrail.network.connection.web;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.network.connection.web.handler.HTTPRequestHandler;
import org.wolfgang.contrail.network.connection.web.handler.HTTPRequestHandlerImpl;

/**
 * Handles handshakes and messages
 */
class WebServerHandler extends SimpleChannelUpstreamHandler {

	/**
	 * Request handler
	 */
	private final HTTPRequestHandler httpRequestHandler;

	/**
	 * Constructor
	 */
	public WebServerHandler(DataSenderFactory<String, DataReceiver<String>> factory) {		
		this.httpRequestHandler = new HTTPRequestHandlerImpl(factory, new WebServerPage());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			final HttpRequest httpRequest = (HttpRequest) msg;
			this.httpRequestHandler.handleHttpRequest(ctx, httpRequest);
		} else if (msg instanceof WebSocketFrame) {
			final WebSocketFrame webSocketFrame = (WebSocketFrame) msg;
			this.httpRequestHandler.handleWebSocketFrame(ctx, webSocketFrame);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}