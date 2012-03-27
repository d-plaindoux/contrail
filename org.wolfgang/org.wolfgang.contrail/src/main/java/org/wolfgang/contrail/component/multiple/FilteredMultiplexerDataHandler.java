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

package org.wolfgang.contrail.component.multiple;

import java.util.Map.Entry;

import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;

/**
 * A <code>FilteredMultiplexerDataHandler</code> is able to manage information
 * using filters owned buy each filtered upstream destination component linked
 * to the multiplexer component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class FilteredMultiplexerDataHandler<D> implements DownStreamDataHandler<DataWithInformation<D>> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final FilteredSourceComponents<D> filteredSourceComponentSet;

	/**
	 * Constructor
	 * 
	 * @param upStreamDeMultiplexer
	 */
	public FilteredMultiplexerDataHandler(FilteredSourceComponents<D> filteredSourceComponentSet) {
		super();
		this.filteredSourceComponentSet = filteredSourceComponentSet;
	}

	@Override
	public void handleData(DataWithInformation<D> data) throws DataHandlerException {
		boolean notHandled = true;

		for (Entry<ComponentId, DataInformationFilter> entry : filteredSourceComponentSet.getSourceFilters().entrySet()) {
			if (entry.getValue().accept(data.getDataInformation())) {
				try {
					filteredSourceComponentSet.getSourceComponent(entry.getKey()).getDownStreamDataHandler().handleData(data);
					notHandled = false;
				} catch (ComponentNotConnectedException consume) {
					// TODO
				}
			}
		}

		if (notHandled) {
			throw new DataHandlerException();
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
