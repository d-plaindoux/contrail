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
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

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
	private final UpStreamDataFlowFactory<byte[], byte[]> factory;

	/**
	 * Constructor
	 * 
	 * @param ecosystem
	 *            The factory used to create components
	 */
	public OutputStreamClient(UpStreamDataFlowFactory<byte[], byte[]> factory) {
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
	 * @throws CannotCreateDataFlowException
	 * @throws DataFlowCloseException
	 */
	public Future<Void> connect(final OutputStream outputStream) throws CannotCreateDataFlowException, DataFlowCloseException {
		final DownStreamDataFlow<byte[]> dataReceiver = new DownStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				try {
					outputStream.write(data);
					outputStream.flush();
				} catch (IOException e) {
					throw new DataFlowException(e);
				}
			}
		};

		final UpStreamDataFlow<byte[]> dataSender = this.factory.create(dataReceiver);
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
