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
import org.wolfgang.contrail.component.annotation.ContrailClient;
import org.wolfgang.contrail.component.annotation.ContrailType;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.Worker;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

/**
 * The <code>ProcessClient</code> provides a client implementation using
 * standard libraries runtime process creation. This can be used to create a
 * connection between two framework using SSH for example.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailClient(scheme = "sh", type = @ContrailType(in = byte[].class, out = byte[].class))
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
	public Worker connect(URI uri, UpStreamDataFlowFactory<byte[], byte[]> factory) throws CannotCreateClientException {
		final Process client;
		try {
			// TODO for the SSH
			client = Runtime.getRuntime().exec(uri.getPath());
		} catch (IOException e) {
			throw new CannotCreateClientException(e);
		}

		final DownStreamDataFlow<byte[]> dataReceiver = new DownStreamDataFlow<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				try {
					client.getOutputStream().write(data);
					client.getOutputStream().flush();
				} catch (IOException e) {
					throw new DataFlowException(e);
				}
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				client.destroy();
			}

			@Override
			public void handleLost() throws DataFlowCloseException {
				handleClose();
			}
		};

		final UpStreamDataFlow<byte[]> dataSender;
		try {
			dataSender = factory.create(dataReceiver);
		} catch (CannotCreateDataFlowException e) {
			try {
				dataReceiver.handleClose();
			} catch (DataFlowCloseException consume) {
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
						dataSender.handleData(Arrays.copyOf(buffer, len));
					}
					return null;
				} catch (Exception e) {
					dataSender.handleClose();
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
