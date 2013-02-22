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

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.wolfgang.contrail.contrail.ComponentSourceManager;
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
public final class WebClient extends NIOClient {

	private final WebClientSocketHandler wsRequestHandler;

	private WebClient(ComponentSourceManager factory) {
		super();

		this.wsRequestHandler = new WebClientSocketHandlerImpl(factory);
	}

	public Instance instance(URI uri) throws Exception {
		if (!"ws".equals(uri.getScheme())) {
			throw new IllegalArgumentException("Unsupported protocol: " + uri.getScheme());
		}

		return new Instance(uri);
	}

	public class Instance implements Callable<ChannelFuture>, Closeable {

		private final URI uri;
		private final AtomicReference<ChannelFuture> reference;

		Instance(URI uri) {
			super();
			this.uri = uri;
			this.reference = new AtomicReference<ChannelFuture>();
		}

		public Instance connect() throws Exception {
			this.call();
			return this;
		}

		@Override
		public ChannelFuture call() throws Exception {
			final Map<String, String> customHeaders = new HashMap<String, String>();
			final WebSocketClientHandshaker handshaker = new WebSocketClientHandshakerFactory().newHandshaker(uri, WebSocketVersion.V13, null, false, customHeaders);
			final ChannelPipelineFactory channelPipelineFactory = new WebClientPipelineFactory(handshaker, wsRequestHandler);
			final ChannelFuture channelFuture = WebClient.this.connect(uri.getHost(), uri.getPort(), channelPipelineFactory);

			handshaker.handshake(channelFuture.getChannel());

			this.reference.set(channelFuture);

			return this.reference.get();
		}

		@Override
		public void close() throws IOException {
			if (this.reference.get() != null) {
				this.reference.get().getChannel().close();
			}
		}
	}

	public static WebClient create(ComponentSourceManager componentSourceManager) {
		return new WebClient(componentSourceManager);
	}
}
