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

package org.wolfgang.contrail.component.network;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.event.NetworkEvent;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryNotFoundException;
import org.wolfgang.contrail.reference.ReferenceTableImpl;

/**
 * <code>NetworkRouterTable</code> is able to provide callback linked to single
 * reference.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkTable extends ReferenceTableImpl<NetworkTable.Entry> {

	/**
	 * <code>Entry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public interface Entry {
		/**
		 * @return
		 */
		DirectReference getReferenceToUse();

		/**
		 * @return
		 * @throws CannotCreateComponentException
		 */
		SourceComponent<NetworkEvent, NetworkEvent> create() throws CannotCreateComponentException;
	}

	/**
	 * Constructor
	 */
	public NetworkTable() {
		super();
	}

	public DirectReference getDirectTarget(DirectReference reference) {
		try {
			return this.retrieve(reference).getReferenceToUse();
		} catch (ReferenceEntryNotFoundException e) {
			// No specific reference entry ...
			return reference;
		}
	}
}
