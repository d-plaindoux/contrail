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

import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.SourceComponent;

/**
 * <code>FilteredSourceComponentSet</code> provides basic mechanism filtering
 * multiple targets.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface FilteredSourceComponents<D> {

	/**
	 * Provide the existing filters
	 * 
	 * @return a map of filters
	 */
	Map<ComponentId, DataFilter<D>> getSourceFilters();

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an source component
	 * @throws ComponentNotConnectedException
	 */
	SourceComponent<?, D> getSourceComponent(ComponentId componentId) throws ComponentNotConnectedException;
}
