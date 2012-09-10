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

package org.wolfgang.contrail.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;

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
public abstract class AbstractClient implements Client {

	/**
	 * Active server sockets
	 */
	private final List<Pair<InputStream, OutputStream>> clients;

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ThreadPoolExecutor executor;

	{
		this.clients = Collections.synchronizedList(new ArrayList<Pair<InputStream, OutputStream>>());

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
	public AbstractClient() {
		super();
	}

	protected abstract Pair<InputStream, OutputStream> getClient(final URI uri) throws CannotCreateClientException;

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
	 * @throws CannotCreateDataFlowException
	 */
	@Override
	public Component connect(final URI uri) throws CannotCreateClientException {
		final Pair<InputStream, OutputStream> client = getClient(uri);

		this.clients.add(client);

		final DownStreamDataFlow<byte[]> dataReceiver = DataFlows.<byte[]> closable(new DownStreamDataFlow<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				try {
					client.getSecond().write(data);
				} catch (IOException e) {
					throw new DataFlowException(e);
				}
				try {
					client.getSecond().flush();
				} catch (IOException e) {
					// Ignore this flush exception ...
				}
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				try {
					client.getFirst().close();
					client.getSecond().close();
				} catch (IOException e) {
					throw new DataFlowCloseException(e);
				}
			}
		});

		final InitialComponent<byte[], byte[]> component = Components.initial(dataReceiver);

		final Callable<Void> reader = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				final byte[] buffer = new byte[1024 * 8];
				try {
					int len;
					while ((len = client.getFirst().read(buffer)) != -1) {
						component.getUpStreamDataFlow().handleData(Arrays.copyOf(buffer, len));
					}
				} catch (SocketException e) {
					// Nothings
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				} finally {
					clients.remove(client);
					component.closeUpStream();
				}

				return null;
			}
		};

		executor.submit(reader);

		return component;
	}

	@Override
	public void close() throws IOException {
		for (Pair<InputStream, OutputStream> client : this.clients) {
			try {
				client.getFirst().close();
				client.getSecond().close();
			} catch (IOException consume) {
				// Ignore
			}
		}

		executor.shutdownNow();
	}
}
