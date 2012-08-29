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
import org.jboss.netty.channel.Channels;
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
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.network.connection.web.WebServerPage;
import org.wolfgang.contrail.network.connection.web.resource.Resource;

/**
 * <code>HTTPRequestHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class HTTPRequestHandlerImpl implements HTTPRequestHandler {

	private static final String WEBSOCKET = "/web.socket";
	private static final DefaultHttpResponse DEFAULT_HTTP_RESPONSE = new DefaultHttpResponse(HTTP_1_1, OK);
	private static final DefaultHttpResponse NOT_FOUND_HTTP_RESPONSE = new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);
	private static final DefaultHttpResponse FORBIDDEN_HTTP_RESPONSE = new DefaultHttpResponse(HTTP_1_1, FORBIDDEN);

	/**
	 * 
	 */
	private final UpStreamDataHandlerFactory<String, String> factory;

	/**
	 * 
	 */
	private final WebServerPage serverPage;

	/**
	 * 
	 */
	private WebSocketServerHandshaker handshaker;

	/**
	 * 
	 */
	private UpStreamDataHandler<String> receiver;

	/**
	 * Constructor
	 * 
	 * @param ecosystem2
	 */
	public HTTPRequestHandlerImpl(UpStreamDataHandlerFactory<String, String> factory, WebServerPage serverPage) {
		this.factory = factory;
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
	public void handleHttpRequest(ChannelHandlerContext context, HttpRequest req) throws Exception {
		// Allow only GET methods ?
		if (req.getMethod() != GET) {
			this.sendHttpResponse(context, req, FORBIDDEN_HTTP_RESPONSE);
		}

		// Map containing informations
		final Map<String, String> map = new HashMap<String, String>();
		map.put("self.id", String.valueOf(req.getHeader("From")));

		// Prepare the URI ?
		final String resourceURI;
		if (req.getUri().equals(WEBSOCKET)) {
			this.initiateWebSocket(context, req);
		} else {
			if (req.getUri().equals("/")) {
				resourceURI = "/index.html";
			} else {
				resourceURI = req.getUri();
			}

			final HttpResponse res = DEFAULT_HTTP_RESPONSE;

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
				this.sendHttpResponse(context, req, res);

			} catch (IOException e) {
				this.sendHttpResponse(context, req, NOT_FOUND_HTTP_RESPONSE);
			}
		}
	}

	@Override
	public void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws DataHandlerException, DataHandlerCloseException {
		// Check for closing frame
		if (frame instanceof CloseWebSocketFrame) {
			this.handshaker.close(context.getChannel(), (CloseWebSocketFrame) frame);
			this.handshaker = null;
			this.receiver.handleClose();
		} else if (frame instanceof PingWebSocketFrame) {
			this.sendWebSocketFrame(context, new PongWebSocketFrame(frame.getBinaryData()));
		} else if (frame instanceof TextWebSocketFrame) {
			final String request = ((TextWebSocketFrame) frame).getText();
			this.receiver.handleData(request);
		} else {
			throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
		}
	}

	//
	// Basic and internal construction mechanisms
	//

	/**
	 * @param req
	 * @return
	 */
	private String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET;
	}

	/**
	 * @param context
	 * @return
	 */
	private DownStreamDataHandler<String> createReceiver(final ChannelHandlerContext context) {
		assert handshaker != null;

		return new DownStreamDataHandler<String>() {
			@Override
			public String toString() {
				return "Web Socket " + context.getName();
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				handshaker.close(context.getChannel(), new CloseWebSocketFrame());
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				handleClose();
			}

			@Override
			public void handleData(String data) throws DataHandlerException {
				sendWebSocketFrame(context, new TextWebSocketFrame(data));
			}
		};
	}

	/**
	 * @param context
	 * @return
	 */
	private ChannelFutureListener createListener(ChannelHandlerContext context) {
		final DownStreamDataHandler<String> emitter = createReceiver(context);

		return new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					Channels.fireExceptionCaught(future.getChannel(), future.getCause());
				} else {
					try {
						receiver = factory.create(emitter);
					} catch (Exception e) {
						Channels.fireExceptionCaught(future.getChannel(), e);
						throw e;
					}
				}
			}
		};
	}

	//
	// Framework initialization
	//

	/**
	 * @param context
	 * @param req
	 * @throws Exception
	 */
	private void initiateWebSocket(ChannelHandlerContext context, HttpRequest req) throws Exception {
		final String location = this.getWebSocketLocation(req);
		final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(location, null, false);
		this.handshaker = wsFactory.newHandshaker(req);
		if (this.handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(context.getChannel());
		} else {
			this.handshaker.handshake(context.getChannel(), req).addListener(this.createListener(context));
		}
	}

	//
	// Message emission facilities
	//
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
