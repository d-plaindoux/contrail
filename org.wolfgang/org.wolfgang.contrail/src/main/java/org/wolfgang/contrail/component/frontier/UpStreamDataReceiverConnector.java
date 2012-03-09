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

package org.wolfgang.contrail.component.frontier;

import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;

/**
 * <code>UpStreamDataReceiverConnector</code> is simple connector dedicated to
 * event management and handling coming from the component chain to an external
 * data receiver
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class UpStreamDataReceiverConnector<E> implements UpStreamDataHandler<E> {

	/**
	 * The data receiver 
	 */
	private final DataReceiver<E> receiver;
	
	/**
	 * Boolean reflecting the connector status
	 */
	private volatile boolean closed;

	{
		this.closed = false;
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The receiver
	 */
	public UpStreamDataReceiverConnector(DataReceiver<E> receiver) {
		this.receiver = receiver;
	}

	@Override
	public void handleData(E data) throws DataHandlerException {
		if (closed) {
			throw new UpStreamDataHandlerClosedException();
		} else {
			receiver.receiveData(data);
		}
	}

	@Override
	public void handleClose() {
		this.closed = true;
	}

	@Override
	public void handleLost() {
		this.closed = true;
	}

}
