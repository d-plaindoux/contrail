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

package org.wolfgang.contrail.network.connection;

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
import org.wolfgang.contrail.ecosystem.CannotIntegrateInitialComponentException;
import org.wolfgang.contrail.ecosystem.CannotProvideInitialComponentException;
import org.wolfgang.contrail.ecosystem.ComponentEcosystem;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * The <code>NetworkClient</code> provides a client implementation using
 * standard libraries like sockets and server sockets. The current
 * implementation don't use the new IO libraries and select mechanism. As a
 * consequence this implementation is not meant to be scalable as required for
 * modern framework like web portal. Nevertheless this can be enough for an
 * optimized network layer relaying on federation network links between
 * components particularly on presence of multiple hop network links.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkClient implements Closeable {

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ThreadPoolExecutor executor;

	/**
	 * De-multiplexer component
	 */
	private final ComponentEcosystem ecosystem;

	{
		final ThreadGroup GROUP = new ThreadGroup("Network.Client");
		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(GROUP, r, "Network.Connected.Client");
			}
		};
		final LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<Runnable>();
		this.executor = new ThreadPoolExecutor(256, 256, 30L, TimeUnit.SECONDS, linkedBlockingQueue, threadFactory);
		this.executor.allowCoreThreadTimeOut(true);
	}

	/**
	 * Constructor
	 * 
	 * @param ecosystem
	 *            The factory used to create components
	 */
	public NetworkClient(ComponentEcosystem ecosystem) {
		super();
		this.ecosystem = ecosystem;
	}

	/**
	 * Method called whether a client connection must be performed
	 * 
	 * @param address
	 *            The server internet address
	 * @param port
	 *            The server port
	 * @throws IOException
	 * @throws CannotIntegrateInitialComponentException
	 * @throws CannotProvideInitialComponentException
	 */
	public void connect(InetAddress address, int port) throws IOException, CannotProvideInitialComponentException,
			CannotIntegrateInitialComponentException {
		final Socket client = new Socket(address, port);

		final DataReceiver<byte[]> dataReceiver = new DataReceiver<byte[]>() {
			@Override
			public void receiveData(byte[] data) throws DataHandlerException {
				try {
					client.getOutputStream().write(data);
				} catch (IOException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void close() throws IOException {
				client.close();
			}
		};

		final DataSender<byte[]> dataSender = ecosystem.bindToInitial(dataReceiver, byte[].class, byte[].class);

		final Callable<Void> reader = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				final byte[] buffer = new byte[1024 * 8];
				try {
					int len = client.getInputStream().read(buffer);
					while (len != -1) {
						dataSender.sendData(Arrays.copyOf(buffer, len));
						len = client.getInputStream().read(buffer);
					}
					return null;
				} catch (Exception e) {
					dataSender.close();
					throw e;
				}
			}
		};

		executor.submit(reader);
	}

	@Override
	public void close() throws IOException {
		executor.shutdownNow();
	}
}
