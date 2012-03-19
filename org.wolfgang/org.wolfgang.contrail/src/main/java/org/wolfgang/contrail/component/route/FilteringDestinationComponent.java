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

import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataWithInformation;

/**
 * A <code>FilteringUpStreamDestinationComponent</code> is an upstream
 * destination component capable dedicated to data with information and capable
 * to filter such data.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface FilteringDestinationComponent<U,D> extends DestinationComponent<DataWithInformation<U>,D> {

	/**
	 * Provides the filter associated to the source component
	 * 
	 * @return a data information filter (never <code>null</code>)
	 */
	DataInformationFilter getDataInformationFilter();

}
