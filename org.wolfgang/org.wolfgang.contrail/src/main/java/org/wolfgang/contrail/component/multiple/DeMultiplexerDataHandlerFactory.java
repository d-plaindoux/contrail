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

import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>DeMultiplexeDataHandlerFactory</code> is able to create a dedicated up
 * stream data handler using the parametric filtered source component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface DeMultiplexerDataHandlerFactory<D> {

	/**
	 * Methods called whether a dedicated multiplexer up-stream data handler
	 * must be created
	 * 
	 * @param filteredSource
	 *            The filtered source component
	 * @return an upstream data handler
	 */
	UpStreamDataHandler<DataWithInformation<D>> create(FilteredDestinationComponentSet<D> filteredDestination);

}
