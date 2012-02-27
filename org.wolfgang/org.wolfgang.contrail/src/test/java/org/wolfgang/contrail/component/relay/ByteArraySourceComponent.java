/*
 * WolfGang Copyright (C)2012 D. Plaindoux.
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

import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.exception.ComponentAlreadyConnected;
import org.wolfgang.contrail.exception.ComponentNotYetConnected;
import org.wolfgang.contrail.handler.DataContext;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.HandleDataException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>BytesSourceComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ByteArraySourceComponent implements UpStreamSourceComponent<byte[]>, DownStreamDataHandler<byte[]> {

	/**
	 * 
	 */
	private final OutputStream outputStream;
	
	/**
	 * 
	 */
	private UpStreamDataHandler<byte[]> upStreamDataHandler;

	/**
	 * Constructor
	 */
	public ByteArraySourceComponent(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}

	/*
	 * @see org.wolfgang.contrail.component.UpStreamSourceComponent#
	 * getDownStreamDataChannel()
	 */
	@Override
	public DownStreamDataHandler<byte[]> getDownStreamDataHandler() {
		return this;
	}

	/*
	 * @see
	 * org.wolfgang.contrail.component.UpStreamSourceComponent#connect(org.wolfgang
	 * .contrail.component.UpStreamDestinationComponent)
	 */
	@Override
	public void connect(UpStreamDestinationComponent<byte[]> handler) throws ComponentAlreadyConnected {
		if (this.upStreamDataHandler == null) {
			this.upStreamDataHandler = handler.getUpStreamDataHandler();
		} else {
			throw new ComponentAlreadyConnected();
		}
	}

	/*
	 * @see
	 * org.wolfgang.contrail.component.UpStreamSourceComponent#disconnect(org
	 * .wolfgang.contrail.component.UpStreamDestinationComponent)
	 */
	@Override
	public void disconnect(UpStreamDestinationComponent<byte[]> handler) throws ComponentNotYetConnected {
		if (this.upStreamDataHandler == null) {
			throw new ComponentNotYetConnected();
		} else {
			this.upStreamDataHandler = null;
		}
	}

	// DownStreamDataHandler implementation

	/*
	 * @see
	 * org.wolfgang.contrail.handler.DataHandler#handleData(org.wolfgang.contrail
	 * .data.DataContext, java.lang.Object)
	 */
	@Override
	public void handleData(DataContext context, byte[] data) throws HandleDataException {
		try {
			this.outputStream.write(data);
		} catch (IOException e) {
			throw new HandleDataException(e);
		}
	}

	/*
	 * @see org.wolfgang.contrail.handler.DataHandler#handleClose()
	 */
	@Override
	public void handleClose() {
		// Nothing
	}

	/*
	 * @see org.wolfgang.contrail.handler.DataHandler#handleLost()
	 */
	@Override
	public void handleLost() {
		// Nothing
	}

	/**
	 * Method called whether an external byte array is received
	 * 
	 * @param bytes The byte array
	 * @throws HandleDataException thrown if the data handling fails
	 */

	public void emitData(byte[] bytes) throws HandleDataException {
		if (this.upStreamDataHandler == null) {
			throw new HandleDataException();
		} else {
			this.upStreamDataHandler.handleData(null, bytes);
		}
	}
}
