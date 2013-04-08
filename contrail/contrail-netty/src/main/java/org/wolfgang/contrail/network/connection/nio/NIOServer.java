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
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * <code>NIOServer</code> is able to manage client connection using NIO
 * libraries
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public abstract class NIOServer implements Closeable {

	private final AtomicReference<Channel> channelReference;

	public NIOServer() {
		this.channelReference = new AtomicReference<Channel>();
	}

	public void bind(int port, ChannelPipelineFactory factory) {
		// Configure the server.
		final NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		final ServerBootstrap serverBootstrap = new ServerBootstrap(channelFactory);

		serverBootstrap.setPipelineFactory(factory);

		final Channel bind = serverBootstrap.bind(new InetSocketAddress("0.0.0.0", port));

		channelReference.set(bind);
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