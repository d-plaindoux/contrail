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

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.UpStreamDataHandlerAdapter;

/**
 * <code>InitialUpStreamDataHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InitialUpStreamDataHandler<U> extends UpStreamDataHandlerAdapter<U> {

	private final InitialComponent<U, ?> component;

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	public InitialUpStreamDataHandler(InitialComponent<U, ?> component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(U data) throws DataHandlerException {
		super.handleData(data);
		try {
			this.component.getUpStreamDataHandler().handleData(data);
		} catch (ComponentNotConnectedException e) {
			throw new DataHandlerException(e);
		}

	}

	@Override
	public void handleClose() throws DataHandlerCloseException {
		super.handleClose();
		try {
			this.component.getUpStreamDataHandler().handleClose();
		} catch (ComponentNotConnectedException e) {
			throw new DataHandlerCloseException(e);
		}
	}

	@Override
	public void handleLost() throws DataHandlerCloseException {
		super.handleLost();
		try {
			this.component.getUpStreamDataHandler().handleLost();
		} catch (ComponentNotConnectedException e) {
			throw new DataHandlerCloseException(e);
		}
	}

}
