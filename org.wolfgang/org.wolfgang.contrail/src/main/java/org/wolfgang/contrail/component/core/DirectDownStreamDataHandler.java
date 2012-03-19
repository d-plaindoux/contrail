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

import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;

/**
 * A <code>DirectDownStreamDataHandler</code> is able to propagate immediately a
 * data to the corresponding upstream source component component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DirectDownStreamDataHandler<D> implements DownStreamDataHandler<D> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final SourceComponent<?, D> upStreamSourceComponent;

	/**
	 * Constructor
	 * 
	 * @param upStreamMultiplexer
	 */
	public DirectDownStreamDataHandler(SourceComponent<?, D> upStreamSourceComponent) {
		super();
		this.upStreamSourceComponent = upStreamSourceComponent;
	}

	@Override
	public void handleData(D data) throws DataHandlerException {
		this.upStreamSourceComponent.getDownStreamDataHandler().handleData(data);
	}

	@Override
	public void handleClose() throws DataHandlerCloseException {
		this.upStreamSourceComponent.closeDownStream();
	}

	@Override
	public void handleLost() throws DataHandlerCloseException {
		this.upStreamSourceComponent.closeDownStream();
	}

}
