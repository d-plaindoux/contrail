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

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.util.CharsetUtil;
import org.wolfgang.contrail.network.connection.web.WebServerPage;
import org.wolfgang.contrail.network.connection.web.resource.Resource;

/**
 * <code>HTTPRequestHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class HTTPRequestHandlerImpl implements HTTPRequestHander {

	private static final String WEBSOCKET = "/new.web.socket";

	private final WebServerPage serverPage;
	private WebSocketServerHandshaker handshaker;

	/**
	 * Constructor
	 */
	public HTTPRequestHandlerImpl(WebServerPage serverPage) {
		this.serverPage = serverPage;
	}

	/**
	 * Main method called whether an HTTP request is received
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@Override
	public void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
		// Allow only GET methods ?
		if (req.getMethod() != GET) {
			this.sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
		}

		// Map containing informations
		final Map<String, String> map = new HashMap<String, String>();
		map.put("self.id", String.valueOf(req.getHeader("From")));

		// Prepare the URI ?
		final String resourceURI;
		if (req.getUri().equals(WEBSOCKET)) {
			final String location = this.getWebSocketLocation(req);
			final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(location, null, false);
			this.handshaker = wsFactory.newHandshaker(req);
			if (this.handshaker == null) {
				wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
			} else {
				this.handshaker.handshake(ctx.getChannel(), req).addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);
			}
		} else {
			if (req.getUri().equals("/")) {
				resourceURI = "/index.html";
			} else {
				resourceURI = req.getUri();
			}

			final HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);

			try {
				final Resource resource = serverPage.getResource(resourceURI);

				if (resource.getFreeVariables().contains("web.socket.location")) {
					final String location = this.getWebSocketLocation(req);
					map.put("web.socket.location", location);
				}

				final ChannelBuffer content = resource.getContent(map);
				res.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
				setContentLength(res, content.readableBytes());

				res.setContent(content);
				this.sendHttpResponse(ctx, req, res);

			} catch (IOException e) {
				this.sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, NOT_FOUND));
			}
		}
	}

	@Override
	public void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			this.handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
		} else if (frame instanceof PingWebSocketFrame) {
			this.sendWebSocketFrame(ctx, new PongWebSocketFrame(frame.getBinaryData()));
		} else if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		} else {
			final String request = ((TextWebSocketFrame) frame).getText();
			// TODO replace this code ASAP
			this.sendWebSocketFrame(ctx, new TextWebSocketFrame(request.toUpperCase()));
		}
	}

	private String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET;
	}

	private void sendWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame res) {
		// Send the response and close the connection if necessary.
		ctx.getChannel().write(res);
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		final ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
