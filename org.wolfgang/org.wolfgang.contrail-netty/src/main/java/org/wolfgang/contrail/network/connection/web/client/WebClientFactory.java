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

import java.net.URI;

import org.wolfgang.contrail.component.SourceComponentNotifier;
import org.wolfgang.contrail.network.connection.nio.NIOClient;
import org.wolfgang.contrail.network.connection.web.handler.WebClientSocketHandler;
import org.wolfgang.contrail.network.connection.web.handler.WebClientSocketHandlerImpl;

/**
 * <code>WebServer</code> is A HTTP server which serves HTTP requests and Web
 * Socket.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class WebClientFactory extends NIOClient {

	private final WebClientSocketHandler wsRequestHandler;

	private WebClientFactory(SourceComponentNotifier factory) {
		super();

		this.wsRequestHandler = new WebClientSocketHandlerImpl(factory);
	}

	WebClientSocketHandler getWsRequestHandler() {
		return wsRequestHandler;
	}

	public WebClient connect(URI uri) throws Exception {
		if (!"ws".equals(uri.getScheme())) {
			throw new IllegalArgumentException("Unsupported protocol: " + uri.getScheme());
		}

		return new WebClient(uri, this);
	}

	public static WebClientFactory create(SourceComponentNotifier componentSourceManager) {
		return new WebClientFactory(componentSourceManager);
	}
}
