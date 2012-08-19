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

package org.wolfgang.contrail.component.router;

import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.reference.DirectReference;

/**
 * A <code>StreamDataHandlerStation</code> is able to manage information using
 * filters owned buy each filtered upstream destination component linked to the
 * multiplexer component. The data is a network event in this case and the
 * management is done using a dedicated network router.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class SwitchDataHandlerStation extends AbstractDataHandlerStation {

	/**
	 * Constructor
	 * 
	 * @param component
	 * @param selfReference
	 * @param routerTable
	 */
	public SwitchDataHandlerStation(RouterComponent component, RouterSourceTable routerTable) {
		super(component, routerTable);
	}

	@Override
	protected DirectReference getNextTarget(Event data) {
		if (data.getReferenceToDestination().hasNext()) {
			data.getReferenceToDestination().removeCurrent();
		}
		return data.getReferenceToDestination().getCurrent();
	}

	@Override
	public String toString() {
		return "switch";
	}
}
