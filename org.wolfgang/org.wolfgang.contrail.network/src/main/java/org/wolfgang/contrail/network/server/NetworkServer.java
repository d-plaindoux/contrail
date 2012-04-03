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

package org.wolfgang.contrail.network.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.factory.ComponentFactory;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * The <code>NetworkServer</code> provides a server implementation using
 * standard libraries provides with Java like sockets and server sockets. The
 * current implementation don't use the new facilities and select mechanism. As
 * a consequence such code is not meant to be scalable as required for modern
 * framework like web portal. In addition this can be enough for an optimized
 * network layer relaying on federated network links between components
 * particularly on presence of multiple hop network links.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkServer implements Callable<Void>, Closeable {

	/**
	 * 
	 */
	private final ThreadPoolExecutor executor;

	/**
	 * The chosen inet address for the socket server
	 */
	private final InetAddress address;

	/**
	 * The chosen port number for the socket server
	 */
	private final int port;

	/**
	 * De-multiplexer component
	 */
	private final ComponentFactory factory;

	/**
	 * The underlying server socket
	 */
	private ServerSocket serverSocket;

	{
		final ThreadGroup GROUP = new ThreadGroup("Network.Server");
		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(GROUP, r, "Network.Client");
			}
		};
		final LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<Runnable>();
		this.executor = new ThreadPoolExecutor(256, 256, 30L, TimeUnit.SECONDS, linkedBlockingQueue, threadFactory);
		this.executor.allowCoreThreadTimeOut(true);
	}

	/**
	 * Constructor
	 * 
	 * @param address
	 * @param port
	 */
	public NetworkServer(InetAddress address, int port, ComponentFactory factory) {
		super();
		this.address = address;
		this.port = port;
		this.factory = factory;
	}

	@Override
	public Void call() throws Exception {
		if (serverSocket != null) {
			throw new IllegalAccessException();
		}

		serverSocket = new ServerSocket(port, 0, address);

		while (serverSocket.isBound()) {
			final Socket client = serverSocket.accept();

			final DataReceiver<byte[]> dataReceiver = new DataReceiver<byte[]>() {
				@Override
				public void receiveData(byte[] data) throws DataHandlerException {
					try {
						client.getOutputStream().write(data);
					} catch (IOException e) {
						throw new DataHandlerException(e);
					}
				}
			};

			final DataSender<byte[]> dataSender = factory.createInitial(dataReceiver, byte[].class, byte[].class);

			final Callable<Void> reader = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					final byte[] buffer = new byte[1024 * 8];
					int len = client.getInputStream().read(buffer);
					while (len != -1) {
						dataSender.sendData(Arrays.copyOf(buffer, len));
						len = client.getInputStream().read(buffer);
					}
					return null;
				}
			};

			executor.submit(reader);
		}

		return null;
	}

	@Override
	public void close() throws IOException {
		executor.shutdownNow();

		if (serverSocket != null) {
			serverSocket.close();
		}
	}
}
