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

package org.wolfgang.contrail.flow;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The <code>ClosableDataHandler</code> is a specific data handler managing the
 * stream status (open or close) and delegating operations when it's open.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
abstract class ClosableDataFlow<D> implements DataFlow<D> {

	private final AtomicBoolean closed;
	private final DataFlow<D> dataHandler;

	{
		this.closed = new AtomicBoolean(false);
	}

	/**
	 * Constructor
	 * 
	 * @param exceptionToSend
	 */
	protected ClosableDataFlow(DataFlow<D> dataHandler) {
		super();
		this.dataHandler = dataHandler;
	}

	@Override
	public void handleData(D data) throws DataFlowException {
		if (closed.get()) {
			throw new DataFlowCloseException();
		} else {
			dataHandler.handleData(data);
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		if (closed.getAndSet(true)) {
			throw new DataFlowCloseException();
		} else {
			dataHandler.handleClose();
		}
	}

	@Override
	public void handleLost() throws DataFlowCloseException {
		if (closed.getAndSet(true)) {
			throw new DataFlowCloseException();
		} else {
			dataHandler.handleLost();
		}
	}
}
