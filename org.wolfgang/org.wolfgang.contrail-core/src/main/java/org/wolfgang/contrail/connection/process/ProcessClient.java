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

package org.wolfgang.contrail.connection.process;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wolfgang.common.concurrent.DelegatedFuture;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.Worker;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * The <code>ProcessClient</code> provides a client implementation using
 * standard libraries runtime process creation. This can be used to create a
 * connection between two framework using SSH for example.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ProcessClient implements Client {

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ThreadPoolExecutor executor;

	{
		final ThreadGroup group = new ThreadGroup("Process.Client");
		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(group, r, "Process.Connected.Client");
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
	public ProcessClient() {
		super();
	}

	@Override
	public void close() throws IOException {
		executor.shutdownNow();
	}

	@Override
	public Worker connect(URI uri, DataSenderFactory<byte[], byte[]> factory) throws CannotCreateClientException {
		final Process client;
		try {
			// TODO for the SSH
			client = Runtime.getRuntime().exec(uri.getPath());
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
				client.destroy();
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
				try {
					final byte[] buffer = new byte[1024 * 8];
					int len;
					while ((len = client.getInputStream().read(buffer)) != -1) {
						dataSender.sendData(Arrays.copyOf(buffer, len));
					}
					return null;
				} catch (Exception e) {
					dataSender.close();
					throw e;
				}
			}
		};

		final DelegatedFuture<Void> delegatedFuture = new DelegatedFuture<Void>(executor.submit(reader));

		return new Worker() {
			@Override
			public void shutdown() {
				try {
					client.destroy();
				} finally {
					delegatedFuture.cancel(true);
				}
			}

			@Override
			public boolean isActive() {
				try {
					client.exitValue();
					return false;
				} catch (IllegalThreadStateException e) {
					return true;
				}
			}
		};
	}
}