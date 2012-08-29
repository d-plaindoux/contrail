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

package org.wolfgang.contrail.component.bound;

import java.io.IOException;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>DataSender</code> is capable to send data to the component stream. This
 * is mainly linked to an initial upstream source component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DataInitialSender<E> implements DataSender<E> {

	private final InitialComponent<E, ?> component;

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	public DataInitialSender(InitialComponent<E, ?> component) {
		super();
		this.component = component;
	}

	/**
	 * Method called whether a data shall be performed
	 * 
	 * @param data
	 *            The data to be performed
	 * @throws DataHandlerException
	 *             thrown is the data can not be handled correctly
	 */
	public void sendData(E data) throws DataHandlerException {
		try {
			this.component.getUpStreamDataHandler().handleData(data);
		} catch (ComponentNotConnectedException e) {
			throw new DataHandlerException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			component.getUpStreamDataHandler().handleClose();
		} catch (ComponentNotConnectedException e) {
			throw new IOException(e);
		} catch (DataHandlerCloseException e) {
			throw new IOException(e);
		}
	}
}
