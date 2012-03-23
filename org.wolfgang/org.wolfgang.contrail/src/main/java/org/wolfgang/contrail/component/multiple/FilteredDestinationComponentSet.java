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

import java.util.Map;

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataWithInformation;

/**
 * <code>FilteredMultipleComponent</code> provides basic mechanism for filtered
 * multiple targets.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
interface FilteredDestinationComponentSet<U> {

	/**
	 * Method used to add a filter to a given destination. All destination
	 * without any filter are unreachable. A filter must be added if destination
	 * component must be used when data are managed.
	 * 
	 * @param componentId
	 *            The component identifier
	 * @param filter
	 *            The filter (can be <code>null</code>)
	 * @throws ComponentConnectedException
	 */
	void filterDestination(ComponentId componentId, DataInformationFilter filter) throws ComponentConnectedException;

	/**
	 * Provide the existing filters
	 * 
	 * @return a map of filters
	 */
	Map<ComponentId, DataInformationFilter> getDestinationFilters();

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an destination component
	 * @throws ComponentNotConnectedException
	 */
	DestinationComponent<DataWithInformation<U>, ?> getDestinationComponent(ComponentId componentId) throws ComponentNotConnectedException;
}
