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

package org.wolfgang.contrail.network.connection.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 * <code>NIOClient</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NIOClient implements Callable<Void>, Closeable {

	private final String host;
	private final int port;
	private final ChannelPipelineFactory pipelineFactory;
	private final AtomicReference<ChannelFuture> channelReference;

	public NIOClient(String host, int port, ChannelPipelineFactory pipeline) {
		this.host = host;
		this.port = port;
		this.pipelineFactory = pipeline;
		this.channelReference = new AtomicReference<ChannelFuture>();
	}

	public Void call() {
		// Configure the server.
		final NioClientSocketChannelFactory channelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		final ClientBootstrap clientBootstrap = new ClientBootstrap(channelFactory);

		// Set up the event pipeline factory.
		clientBootstrap.setPipelineFactory(pipelineFactory);

		// Bind and start to accept incoming connections.

		channelReference.set(clientBootstrap.connect(new InetSocketAddress(host, port)));

		return null;
	}

	@Override
	public void close() throws IOException {
		// Find the best way to close this server
		final ChannelFuture channel = channelReference.getAndSet(null);
		if (channel != null) {
			channel.getChannel().close();
		}
	}

}
