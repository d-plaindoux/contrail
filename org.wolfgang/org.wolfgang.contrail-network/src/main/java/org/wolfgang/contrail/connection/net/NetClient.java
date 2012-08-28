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

package org.wolfgang.contrail.connection.net;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wolfgang.common.concurrent.DelegatedFuture;
import org.wolfgang.contrail.component.annotation.ContrailClient;
import org.wolfgang.contrail.component.annotation.ContrailType;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.Worker;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * The <code>NetClient</code> provides a client implementation using standard
 * libraries like sockets and server sockets. The current implementation don't
 * use the new IO libraries and select mechanism. As a consequence this
 * implementation is not meant to be scalable as required for modern framework
 * like web portal. Nevertheless this can be enough for an optimized network
 * layer relaying on federation network links between components particularly on
 * presence of multiple hop network links.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailClient(scheme = "tcp", type = @ContrailType(in = byte[].class, out = byte[].class))
public class NetClient implements Client {

	/**
	 * Active server sockets
	 */
	private List<Socket> clients;

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ThreadPoolExecutor executor;

	{
		this.clients = new ArrayList<Socket>();

		final ThreadGroup group = new ThreadGroup("Network.Client");
		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(group, r, "Network.Connected.Client");
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
	public NetClient() {
		super();
	}

	/**
	 * Method called whether a client connection must be performed
	 * 
	 * @param address
	 *            The server internet address
	 * @param port
	 *            The server port
	 * @return
	 * @throws IOException
	 * @throws CannotBindToInitialComponentException
	 * @throws CannotCreateDataSenderException
	 */
	public Worker connect(final URI uri, final DataSenderFactory<byte[], byte[]> factory) throws CannotCreateClientException {
		final Socket client;

		try {
			client = new Socket(uri.getHost(), uri.getPort());
			this.clients.add(client);
		} catch (UnknownHostException e) {
			throw new CannotCreateClientException(e);
		} catch (IOException e) {
			throw new CannotCreateClientException(e);
		}

		final DataReceiver<byte[]> dataReceiver = new DataReceiver<byte[]>() {
			@Override
			public void receiveData(byte[] data) throws DataHandlerException {
				try {
					client.getOutputStream().write(data);
					client.getOutputStream().flush();
				} catch (IOException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void close() throws IOException {
				client.close();
			}
		};

		final DataSender<byte[]> dataSender;
		try {
			dataSender = factory.create(dataReceiver);
		} catch (CannotCreateDataSenderException e) {
			try {
				dataReceiver.close();
			} catch (IOException consume) {
				// Ignore
			}
			throw new CannotCreateClientException(e);
		}

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
				} finally {
					dataSender.close();
					client.close();
					clients.remove(client);
				}

				return null;
			}
		};

		final DelegatedFuture<Void> delegatedFuture = new DelegatedFuture<Void>(executor.submit(reader)) {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				try {
					client.close();
				} catch (IOException consume) {
					// Ignore
				}
				return super.cancel(mayInterruptIfRunning);
			}
		};

		return new Worker() {
			@Override
			public void shutdown() {
				delegatedFuture.cancel(true);
			}

			@Override
			public boolean isActive() {
				return !delegatedFuture.isCancelled() && !delegatedFuture.isDone();
			}
		};
	}

	@Override
	public void close() throws IOException {
		for (Socket client : this.clients) {
			try {
				client.close();
			} catch (IOException consume) {
				// Ignore
			}
		}

		executor.shutdownNow();
	}
}
