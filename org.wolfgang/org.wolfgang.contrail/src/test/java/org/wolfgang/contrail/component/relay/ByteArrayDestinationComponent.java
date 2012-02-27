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

import org.wolfgang.contrail.component.impl.AbstractUpStreamDestinationComponent;
import org.wolfgang.contrail.exception.ComponentNotYetConnected;
import org.wolfgang.contrail.handler.DataContext;
import org.wolfgang.contrail.handler.HandleDataException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>BytesSourceComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ByteArrayDestinationComponent extends AbstractUpStreamDestinationComponent<byte[]> implements
		UpStreamDataHandler<byte[]> {

	/**
	 * Constructor
	 */
	public ByteArrayDestinationComponent() {
		super();
		this.setUpStreamDataHandler(this);
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
		try {
			this.getDowntreamDataHandler().handleData(context, data);
		} catch (ComponentNotYetConnected e) {
			throw new HandleDataException();
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
