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
import java.io.OutputStream;
import java.util.concurrent.Future;

import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * The <code>FileReceiverClient</code> provides a client implementation using
 * standard libraries like files. The connection requires a file specification
 * for the reception.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class OutputStreamClient implements Closeable {

	/**
	 * Data sender factory
	 */
	private final DataSenderFactory<byte[], byte[]> factory;

	/**
	 * Constructor
	 * 
	 * @param ecosystem
	 *            The factory used to create components
	 */
	public OutputStreamClient(DataSenderFactory<byte[], byte[]> factory) {
		super();
		this.factory = factory;
	}

	/**
	 * @param input
	 *            The input file (can be <code>null</code>)
	 * @param output
	 *            The output file (can be <code>null</code>)
	 * @return
	 * @throws IOException
	 * @throws CannotCreateDataSenderException
	 */
	public Future<Void> connect(final OutputStream outputStream) throws IOException, CannotCreateDataSenderException {

		final DataReceiver<byte[]> dataReceiver = new DataReceiver<byte[]>() {
			@Override
			public void receiveData(byte[] data) throws DataHandlerException {
				try {
					outputStream.write(data);
					outputStream.flush();
				} catch (IOException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void close() throws IOException {
				// Do nothing
			}
		};

		final DataSender<byte[]> dataSender = this.factory.create(dataReceiver);
		dataSender.close();

		final FutureResponse<Void> futureResponse = new FutureResponse<Void>();
		futureResponse.setValue(null);
		return futureResponse;
	}

	@Override
	public void close() throws IOException {
		// Nothing
	}
}
