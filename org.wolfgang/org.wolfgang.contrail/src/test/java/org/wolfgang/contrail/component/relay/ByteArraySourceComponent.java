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

package org.wolfgang.contrail.component.relay;

import java.io.IOException;
import java.io.OutputStream;

import org.wolfgang.contrail.component.impl.AbstractUpStreamSourceComponent;
import org.wolfgang.contrail.exception.ComponentNotYetConnected;
import org.wolfgang.contrail.handler.DataContext;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.HandleDataException;

/**
 * <code>BytesSourceComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ByteArraySourceComponent extends AbstractUpStreamSourceComponent<byte[]> {

	/**
	 * <code>LocalDownStreamDataHandler</code> is the internal implementation of
	 * the down stream handler.
	 * 
	 * @author Didier Plaindoux
	 * @verision 1.0
	 */
	public class LocalDownStreamDataHandler implements DownStreamDataHandler<byte[]> {
		@Override
		public void handleData(DataContext context, byte[] data) throws HandleDataException {
			try {
				outputStream.write(data);
			} catch (IOException e) {
				throw new HandleDataException(e);
			}
		}

		@Override
		public void handleClose() {
			// Nothing
		}

		@Override
		public void handleLost() {
			// Nothing
		}
	}

	/**
	 * Output stream receiving outgoing data
	 */
	private final OutputStream outputStream;
	private final DownStreamDataHandler<byte[]> downStreamDataHandler;

	/**
	 * Constructor
	 */
	public ByteArraySourceComponent(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
		this.downStreamDataHandler = new LocalDownStreamDataHandler();
	}

	@Override
	public DownStreamDataHandler<byte[]> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

	/**
	 * Method called whether an external byte array is received
	 * 
	 * @param bytes
	 *            The byte array
	 * @throws HandleDataException
	 *             thrown if the data handling fails
	 */
	public void emitData(byte[] bytes) throws HandleDataException {
		try {
			this.getUpStreamDataHandler().handleData(null, bytes);
		} catch (ComponentNotYetConnected e) {
			throw new HandleDataException();
		}
	}
}
