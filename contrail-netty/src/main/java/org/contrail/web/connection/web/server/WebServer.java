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

package org.contrail.web.connection.web.server;

import java.util.concurrent.Callable;

import org.contrail.web.notifier.SourceComponentNotifier;
import org.contrail.web.connection.nio.NIOServer;
import org.contrail.web.connection.web.content.WebContentProvider;

/**
 * <code>WebServer</code> is A HTTP server which serves HTTP requests and Web
 * Socket.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class WebServer extends NIOServer implements Callable<Void> {

	private final SourceComponentNotifier factory;
	private final WebContentProvider contentProvider;

	private int port;

	/**
	 * Constructor
	 * 
	 * @param port
	 */
	private WebServer(SourceComponentNotifier factory, WebContentProvider contentProvider) {
		super();

		this.factory = factory;
		this.contentProvider = contentProvider;
	}

	public WebServer bind(int port) throws Exception {
		this.port = port;
		this.call();
		return this;
	}

	@Override
	public Void call() throws Exception {
		this.bind(this.port, new WebServerPipelineFactory(factory, contentProvider));
		return null;
	}

	public static WebServer create(SourceComponentNotifier componentSourceManager, WebContentProvider contentProvider) {
		return new WebServer(componentSourceManager, contentProvider);
	}
}
