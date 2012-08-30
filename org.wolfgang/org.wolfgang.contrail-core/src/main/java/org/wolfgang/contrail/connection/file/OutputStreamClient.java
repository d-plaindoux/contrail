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
import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerAdapter;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

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
	private final UpStreamDataHandlerFactory<byte[], byte[]> factory;

	/**
	 * Constructor
	 * 
	 * @param ecosystem
	 *            The factory used to create components
	 */
	public OutputStreamClient(UpStreamDataHandlerFactory<byte[], byte[]> factory) {
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
	 * @throws CannotCreateDataHandlerException
	 * @throws DataHandlerCloseException
	 */
	public Future<Void> connect(final OutputStream outputStream) throws CannotCreateDataHandlerException, DataHandlerCloseException {
		final DownStreamDataHandler<byte[]> dataReceiver = new DownStreamDataHandlerAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataHandlerException {
				try {
					outputStream.write(data);
					outputStream.flush();
				} catch (IOException e) {
					throw new DataHandlerException(e);
				}
			}
		};

		final UpStreamDataHandler<byte[]> dataSender = this.factory.create(dataReceiver);
		dataSender.handleClose();

		final FutureResponse<Void> futureResponse = new FutureResponse<Void>();
		futureResponse.setValue(null);
		return futureResponse;
	}

	@Override
	public void close() throws IOException {
		// Nothing
	}
}
