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

import java.util.List;

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
public class ByteArrayDestinationComponent implements UpStreamDestinationComponent<byte[]>, UpStreamDataHandler<byte[]> {

	private DownStreamDataHandler<byte[]> downStreamDataHandler;

	/**
	 * Constructor
	 */
	public ByteArrayDestinationComponent() {
		super();
	}

	/*
	 * @see org.wolfgang.contrail.component.UpStreamSourceComponent#
	 * getDownStreamDataChannel()
	 */
	@Override
	public UpStreamDataHandler<byte[]> getUpStreamDataHandler() {
		return this;
	}

	/*
	 * @see
	 * org.wolfgang.contrail.component.UpStreamSourceComponent#connect(org.wolfgang
	 * .contrail.component.UpStreamDestinationComponent)
	 */
	@Override
	public void connect(UpStreamSourceComponent<byte[]> handler) throws ComponentAlreadyConnected {
		if (this.downStreamDataHandler == null) {
			this.downStreamDataHandler = handler.getDownStreamDataHandler();
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
	public void disconnect(UpStreamSourceComponent<byte[]> handler) throws ComponentNotYetConnected {
		if (this.downStreamDataHandler == null) {
			throw new ComponentNotYetConnected();
		} else {
			this.downStreamDataHandler = null;
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
		// Send the received byte array to the down stream data handler (Loop)
		if (downStreamDataHandler == null) {
			throw new HandleDataException("downStreamHandler");
		} else {
			this.downStreamDataHandler.handleData(context, data);
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
}
