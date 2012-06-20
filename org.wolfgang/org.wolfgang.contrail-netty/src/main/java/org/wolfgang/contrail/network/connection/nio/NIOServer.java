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

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * <code>NIOServer</code> is able to manage client connection using nio
 * libraries
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NIOServer implements Callable<Void>, Closeable {

	/**
	 * 
	 */
	private final String host;

	/**
	 * Port number for web based communication
	 */
	private final int port;
	
	/**
	 * 
	 */
	private final ChannelPipelineFactory pipeline;

	/**
	 * 
	 */
	private AtomicReference<Channel> channelReference;

	/**
	 * Constructor
	 * 
	 * @param port
	 *            The port number
	 */
	public NIOServer(String host, int port, ChannelPipelineFactory pipeline) {
		this.host = host;
		this.port = port;
		this.pipeline = pipeline;
		this.channelReference = new AtomicReference<Channel>();
	}

	/**
	 * Main method called whether a web server is required
	 */
	public Void call() {
		// Configure the server.
		final NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		final ServerBootstrap serverBootstrap = new ServerBootstrap(channelFactory);

		// Set up the event pipeline factory.
		serverBootstrap.setPipelineFactory(pipeline);

		// Bind and start to accept incoming connections.
		
		channelReference.set(serverBootstrap.bind(new InetSocketAddress(port)));

		return null;
	}

	@Override
	public void close() throws IOException {
		// Find the best way to close this server
		final Channel channel = channelReference.getAndSet(null);
		if (channel != null) {
			channel.close();
		}
	}
}