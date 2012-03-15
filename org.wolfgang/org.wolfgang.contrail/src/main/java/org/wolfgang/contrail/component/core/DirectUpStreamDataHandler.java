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

package org.wolfgang.contrail.component.core;

import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * A <code>DirectUpStreamDataHandler</code> is able to propagate immediately a
 * data to the corresponding upstream destination component component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DirectUpStreamDataHandler<D> implements UpStreamDataHandler<D> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final UpStreamDestinationComponent<D> upStreamDestinationComponent;

	/**
	 * Constructor
	 * 
	 * @param upStreamMultiplexer
	 */
	public DirectUpStreamDataHandler(UpStreamDestinationComponent<D> upStreamDestinationComponent) {
		super();
		this.upStreamDestinationComponent = upStreamDestinationComponent;
	}

	@Override
	public void handleData(D data) throws DataHandlerException {
		this.upStreamDestinationComponent.getUpStreamDataHandler().handleData(data);
	}

	@Override
	public void handleClose() throws DataHandlerCloseException {
		this.upStreamDestinationComponent.closeDownStream();
	}

	@Override
	public void handleLost() throws DataHandlerCloseException {
		this.upStreamDestinationComponent.closeDownStream();
	}

}
