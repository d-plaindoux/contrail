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

package org.contrail.stream.network.connection.web.handler;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.util.CharsetUtil;
import org.contrail.stream.network.connection.web.content.WebContentProvider;

/**
 * <code>HTTPRequestHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class HTTPRequestHandlerImpl implements HTTPRequestHandler {

	private static final String WEBSOCKET = "/websocket";
	
	private final WebServerSocketHandler wsRequestHandler;
	private final WebContentProvider serverPage;

	/**
	 * Constructor
	 * 
	 * @param wsRequestHandler
	 *            The web server socket handler
	 * @param serverPage
	 *            The server page
	 */
	public HTTPRequestHandlerImpl(WebServerSocketHandler wsRequestHandler, WebContentProvider serverPage) {
		this.wsRequestHandler = wsRequestHandler;
		this.serverPage = serverPage;
	}

	/**
	 * Main method called whether an HTTP request is received
	 * 
	 * @param request
	 *            The request
	 * @throws Exception
	 */
	@Override
	public void handleHttpRequest(ChannelHandlerContext context, HttpRequest request) throws Exception {
		if (request.getMethod() != GET) {
			this.sendHttpResponse(context, request, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
		} else if (request.getUri().startsWith(WEBSOCKET)) {
			this.initiateWebSocket(context, request);
		} else {
			final String resourceURI;

			if (request.getUri().equals("/")) {
				resourceURI = "/index.html";
			} else if (request.getUri().contains("?")) {
				// Do not use the search in this case (not directly used)
				resourceURI = request.getUri().substring(0, request.getUri().indexOf('?'));
			} else {
				resourceURI = request.getUri();
			}

			final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

			try {
				final byte[] resource = serverPage.getContent(resourceURI);
				final ChannelBuffer content = ChannelBuffers.copiedBuffer(resource);

				// TODO -- later ... response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
				setContentLength(response, resource.length);
				response.setContent(content);

				this.sendHttpResponse(context, request, response);
			} catch (IOException e) {
				this.sendHttpResponse(context, request, new DefaultHttpResponse(HTTP_1_1, NOT_FOUND));
			}
		}
	}

	//
	// Basic and internal construction mechanisms
	//

	private String getWebSocketLocation(HttpRequest request) {
		return "ws://" + request.getHeader(HttpHeaders.Names.HOST) + request.getUri();
	}

	//
	// Framework initialization
	//

	private void initiateWebSocket(ChannelHandlerContext context, HttpRequest req) throws Exception {
		final String location = this.getWebSocketLocation(req);
		final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(location, null, false);
		final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);

		if (handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(context.getChannel());
		} else {
			handshaker.handshake(context.getChannel(), req).addListener(wsRequestHandler.createHandShakeListener(handshaker, context));
		}
	}

	private void sendHttpResponse(ChannelHandlerContext context, HttpRequest req, HttpResponse response) {
		// Generate an error page if response status code is not OK (200).
		if (response.getStatus().getCode() != 200) {
			response.setContent(ChannelBuffers.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(response, response.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		final ChannelFuture f = context.getChannel().write(response);
		
		if (!isKeepAlive(req) || response.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
