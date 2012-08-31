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

package org.wolfgang.contrail.handler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <code>ClosableDataHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClosableDataHandler<D> implements DataHandler<D> {

	private final AtomicBoolean closed;

	private final DataHandler<D> dataHandler;
	private final DataHandlerCloseException exceptionToSend;

	{
		this.closed = new AtomicBoolean(false);
	}

	/**
	 * @param dataHandler
	 * @return
	 */
	public static <U> UpStreamDataHandler<U> create(UpStreamDataHandler<U> dataHandler) {
		return new ClosableUpStreamDataHandler<U>(dataHandler);
	}

	/**
	 * @param dataHandler
	 * @return
	 */
	public static <U> DownStreamDataHandler<U> create(DownStreamDataHandler<U> dataHandler) {
		return new ClosableDownStreamDataHandler<U>(dataHandler);
	}

	/**
	 * Constructor
	 * 
	 * @param exceptionToSend
	 */
	protected ClosableDataHandler(DataHandler<D> dataHandler, DataHandlerCloseException exceptionToSend) {
		super();
		this.dataHandler = dataHandler;
		this.exceptionToSend = exceptionToSend;
	}

	@Override
	public void handleData(D data) throws DataHandlerException {
		if (closed.get()) {
			throw exceptionToSend;
		} else {
			dataHandler.handleData(data);
		}
	}

	@Override
	public void handleClose() throws DataHandlerCloseException {
		if (closed.getAndSet(true)) {
			throw exceptionToSend;
		} else {
			dataHandler.handleClose();
		}
	}

	@Override
	public void handleLost() throws DataHandlerCloseException {
		if (closed.getAndSet(true)) {
			throw exceptionToSend;
		} else {
			dataHandler.handleLost();
		}
	}
}
