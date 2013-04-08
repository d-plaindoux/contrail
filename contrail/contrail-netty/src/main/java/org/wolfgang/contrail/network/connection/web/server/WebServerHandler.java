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

package org.wolfgang.contrail.network.connection.web.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.wolfgang.contrail.component.SourceComponentNotifier;
import org.wolfgang.contrail.network.connection.web.content.WebContentProvider;
import org.wolfgang.contrail.network.connection.web.handler.HTTPRequestHandler;
import org.wolfgang.contrail.network.connection.web.handler.HTTPRequestHandlerImpl;
import org.wolfgang.contrail.network.connection.web.handler.WebServerSocketHandler;
import org.wolfgang.contrail.network.connection.web.handler.WebServerSocketHandlerImpl;

/**
 * Handles handshakes and messages
 */
class WebServerHandler extends SimpleChannelUpstreamHandler {

	/**
	 * Request handler
	 */
	private final HTTPRequestHandler httpRequestHandler;
	private final WebServerSocketHandler wsRequestHandler;

	/**
	 * Constructor
	 */
	public WebServerHandler(SourceComponentNotifier componentSourceManager, WebContentProvider contentProvider) {
		this.wsRequestHandler = new WebServerSocketHandlerImpl(componentSourceManager);
		this.httpRequestHandler = new HTTPRequestHandlerImpl(wsRequestHandler, contentProvider);
	}

	@Override
	public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
		final Object message = event.getMessage();

		if (message instanceof HttpRequest) {
			final HttpRequest httpRequest = (HttpRequest) message;
			this.httpRequestHandler.handleHttpRequest(context, httpRequest);
		} else if (message instanceof WebSocketFrame) {
			final WebSocketFrame webSocketFrame = (WebSocketFrame) message;
			this.wsRequestHandler.handleWebSocketFrame(context, webSocketFrame);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent e) throws Exception {
		if (!this.wsRequestHandler.handleWebSocketError(context)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getCause().getMessage(), e);
		}
		context.getChannel().close();
	}
}