/*
 * Copyrig
ht (C)2012 D. Plaindoux.
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

package org.wolfgang.contrail.connection.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.handler.DownStreamDataHandlerAdapter;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * The <code>FileClient</code> provides a client implementation using standard
 * libraries like files. The connection requires the input file specification
 * for the emission.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InputStreamClient implements Closeable {

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ThreadPoolExecutor executor;

	/**
	 * Data sender factory
	 */
	private final UpStreamDataHandlerFactory<byte[], byte[]> factory;

	{
		final ThreadGroup group = new ThreadGroup("File.Client");
		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(group, r, "File.Connected.Client");
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
	public InputStreamClient(UpStreamDataHandlerFactory<byte[], byte[]> factory) {
		super();
		this.factory = factory;
	}

	/**
	 * @param input
	 *            The input file (can be <code>null</code>)
	 * @return
	 * @throws IOException
	 * @throws CannotCreateDataHandlerException
	 */
	public Future<Void> connect(final InputStream inputStream) throws IOException, CannotCreateDataHandlerException {
		final UpStreamDataHandler<byte[]> dataSender = this.factory.create(new DownStreamDataHandlerAdapter<byte[]>());

		final Callable<Void> reader = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				final byte[] buffer = new byte[1024 * 8];
				try {
					int len = inputStream.read(buffer);
					while (len != -1) {
						dataSender.handleData(Arrays.copyOf(buffer, len));
						len = inputStream.read(buffer);
					}
					return null;
				} catch (Exception e) {
					throw e;
				}
			}
		};

		return executor.submit(reader);
	}

	@Override
	public void close() throws IOException {
		executor.shutdownNow();
	}
}
