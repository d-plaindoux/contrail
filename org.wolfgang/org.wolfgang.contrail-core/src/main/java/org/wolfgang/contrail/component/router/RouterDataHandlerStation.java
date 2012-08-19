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
public class RouterDataHandlerStation extends AbstractDataHandlerStation {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final DirectReference selfReference;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @param selfReference
	 * @param routerTable
	 */
	public RouterDataHandlerStation(RouterComponent component, DirectReference selfReference, RouterSourceTable routerTable) {
		super(component, routerTable);
		this.selfReference = selfReference;
	}

	@Override
	protected DirectReference getNextTarget(Event data) {
		// This data is handled by the current reference
		data.sentBy(this.selfReference);

		final DirectReference current = data.getReferenceToDestination().getCurrent();

		if (data.getReferenceToDestination().hasNext() && current.equals(this.selfReference)) {
			data.getReferenceToDestination().removeCurrent();
			return data.getReferenceToDestination().getCurrent();
		}

		return current;
	}

	@Override
	public String toString() {
		return selfReference.toString();
	}
}
