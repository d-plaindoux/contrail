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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

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
public abstract class NIOClient {

	private final NioClientSocketChannelFactory channelFactory;

	public NIOClient() {
		this.channelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
	}

	protected ChannelFuture connect(String host, int port, ChannelPipelineFactory pipelineFactory) throws Exception {
		// Configure the client.
		final ClientBootstrap clientBootstrap = new ClientBootstrap(channelFactory);

		// Set up the event pipeline factory.
		clientBootstrap.setPipelineFactory(pipelineFactory);

		// Establish the connection.
		return clientBootstrap.connect(new InetSocketAddress(host, port));
	}
}
