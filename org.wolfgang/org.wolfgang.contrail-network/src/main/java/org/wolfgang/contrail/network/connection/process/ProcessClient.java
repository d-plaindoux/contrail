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

package org.wolfgang.contrail.network.connection.process;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * The <code>ProcessClient</code> provides a client implementation using
 * standard libraries runtime process creation. This can be used to create a
 * connection between two framework using SSH for example.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ProcessClient implements Closeable {

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ThreadPoolExecutor executor;

	/**
	 * The data sender factory
	 */
	private final DataSenderFactory<byte[], DataReceiver<byte[]>> factory;

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
	public ProcessClient(DataSenderFactory<byte[], DataReceiver<byte[]>> factory) {
		super();
		this.factory = factory;
	}

	/**
	 * Method called whether a client connection must be performed
	 * 
	 * @param command
	 *            The command to be executed
	 * @throws IOException
	 * @throws CannotCreateDataSenderException
	 */
	public void connect(String[] command) throws IOException, CannotCreateDataSenderException {
		final Process client = Runtime.getRuntime().exec(command);

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

		final DataSender<byte[]> dataSender = this.factory.create(dataReceiver);

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
