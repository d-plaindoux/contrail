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

package org.wolfgang.contrail.component.route;

import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * A <code>Multiplexer</code> is able to manage information using filters owned
 * buy each filtered upstream sourc component linked to the mutiplexer
 * component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class DemultiplexerDataHandler<U> implements UpStreamDataHandler<DataWithInformation<U>> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final DeMultiplexerComponent<U,?> upStreamMultiplexer;

	/**
	 * Constructor
	 * 
	 * @param upStreamMultiplexer
	 */
	public DemultiplexerDataHandler(DeMultiplexerComponent<U,?> upStreamMultiplexer) {
		super();
		this.upStreamMultiplexer = upStreamMultiplexer;
	}

	@Override
	public void handleData(DataWithInformation<U> data) throws DataHandlerException {
		for (FilteringDestinationComponent<U,?> source : upStreamMultiplexer.getSourceComponents()) {
			if (source.getDataInformationFilter().accept(data.getDataInformation())) {
				source.getUpStreamDataHandler().handleData(data);
			}
		}
	}

	@Override
	public void handleClose() throws DataHandlerCloseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLost() throws DataHandlerCloseException {
		// TODO Auto-generated method stub

	}

}
