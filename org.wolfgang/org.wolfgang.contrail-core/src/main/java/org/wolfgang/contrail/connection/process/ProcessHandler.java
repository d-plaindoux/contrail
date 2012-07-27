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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.wolfgang.common.concurrent.DelegatedFuture;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.Server;
import org.wolfgang.contrail.connection.Worker;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * The <code>ProcessHandler</code> provides a client process handler
 * implementation using standard libraries runtime process creation. This can be
 * used to create a connection between two framework using SSH for example.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ProcessHandler implements Server {

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ExecutorService executor;

	/**
	 * The data sender factory
	 */
	private final DataSenderFactory<byte[], byte[]> factory;

	{
		executor = Executors.newSingleThreadExecutor();
	}

	/**
	 * Constructor
	 * 
	 * @param ecosystem
	 *            The factory used to create components
	 */
	public ProcessHandler(DataSenderFactory<byte[], byte[]> factory) {
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
	public void connect() throws IOException, CannotCreateDataSenderException {

		final InputStream input = System.in;
		final OutputStream output = System.out;

		// TODO -- System.setIn(?);
		// TODO -- System.setOut(?);

		final DataReceiver<byte[]> dataReceiver = new DataReceiver<byte[]>() {
			@Override
			public void receiveData(byte[] data) throws DataHandlerException {
				try {
					output.write(data);
					output.flush();
				} catch (IOException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void close() throws IOException {
				System.exit(0); // End of the process
			}
		};

		final DataSender<byte[]> dataSender = this.factory.create(dataReceiver);

		final Callable<Void> reader = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					final byte[] buffer = new byte[1024 * 8];
					int len;
					while ((len = input.read(buffer)) != -1) {
						dataSender.sendData(Arrays.copyOf(buffer, len));
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

	@Override
	public Worker bind(URI uri, DataSenderFactory<byte[], byte[]> factory) throws CannotCreateServerException {

		final InputStream input = System.in;
		final OutputStream output = System.out;

		// TODO -- System.setIn(?);
		// TODO -- System.setOut(?);

		final DataReceiver<byte[]> dataReceiver = new DataReceiver<byte[]>() {
			@Override
			public void receiveData(byte[] data) throws DataHandlerException {
				try {
					output.write(data);
					output.flush();
				} catch (IOException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void close() throws IOException {
				System.exit(0); // End of the process
			}
		};

		final DataSender<byte[]> dataSender;
		try {
			dataSender = factory.create(dataReceiver);
		} catch (CannotCreateDataSenderException e) {
			throw new CannotCreateServerException(e);
		}

		final Callable<Void> reader = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					final byte[] buffer = new byte[1024 * 8];
					int len;
					while ((len = input.read(buffer)) != -1) {
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
				delegatedFuture.cancel(true);
				System.exit(0);
			}

			@Override
			public boolean isActive() {
				return true;
			}
		};
	}
}
