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
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.wolfgang.contrail.network.connection.web.WebServerPage;

/**
 * <code>HTTPRequestHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class HTTPRequestHandler {

	private final WebServerPage serverPage;

	/**
	 * Constructor
	 */
	public HTTPRequestHandler(WebServerPage serverPage) {
		this.serverPage = serverPage;
	}

	/**
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public HttpResponse handleHttpRequest(HttpRequest req) throws Exception {
		// Allow only GET methods.
		if (req.getMethod() != GET) {
			return new DefaultHttpResponse(HTTP_1_1, FORBIDDEN);
		}

		// Send the demo page and favicon.ico
		final String resource;
		if (req.getUri().equals("/")) {
			resource = "/index.html";
		} else {
			resource = req.getUri();
		}
		
		final HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
			final Map<String, String> map = new HashMap<String, String>();
			//map.put("web.socket.location", getWebSocketLocation(req));
			map.put("self.id",String.valueOf(req.getHeader("From")));
			
			try {
				final ChannelBuffer content = serverPage.getPage(resource, map);

				res.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
				setContentLength(res, content.readableBytes());
	
				res.setContent(content);
				return res;
			} catch (IOException e) {
				return new DefaultHttpResponse(HTTP_1_1, NOT_FOUND);
			}

		/*
		// Handshake
		final String location = getWebSocketLocation(req);
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(location, null, false);
		this.handshaker = wsFactory.newHandshaker(req);
		if (this.handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {
			this.handshaker.handshake(ctx.getChannel(), req).addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);
		}
		*/
	}
}
