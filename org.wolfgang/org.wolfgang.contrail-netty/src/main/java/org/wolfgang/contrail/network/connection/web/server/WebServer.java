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

import java.net.URI;

import org.jboss.netty.channel.Channel;
import org.wolfgang.contrail.contrail.ComponentSourceManager;
import org.wolfgang.contrail.network.connection.nio.NIOServer;

/**
 * <code>WebServer</code> is A HTTP server which serves HTTP requests and Web
 * Socket.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class WebServer extends NIOServer {

	private ComponentSourceManager factory;

	/**
	 * Constructor
	 * 
	 * @param port
	 */
	private WebServer(URI uri, ComponentSourceManager factory) {
		super(uri.getHost(), uri.getPort());

		if (!"http".equals(uri.getScheme())) {
			throw new IllegalArgumentException("Unsupported protocol: " + uri.getScheme());
		}

		this.factory = factory;
	}

	public WebServer bind() throws Exception {
		this.call();
		return this;
	}

	@Override
	public Channel call() throws Exception {
		return this.bind(new WebServerPipelineFactory(factory));
	}

	public static WebServer create(URI uri, ComponentSourceManager componentSourceManager) {
		return new WebServer(uri, componentSourceManager);
	}
}
