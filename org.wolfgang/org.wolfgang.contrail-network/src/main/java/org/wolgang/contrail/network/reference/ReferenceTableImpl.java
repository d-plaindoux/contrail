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

package org.wolgang.contrail.network.reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <code>ReferenceTableImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ReferenceTableImpl<E> implements ReferenceTable<E> {

	/**
	 * The internal map
	 */
	private final Map<ReferenceFilter, E> table;

	{
		this.table = new HashMap<ReferenceFilter, E>();
	}

	/**
	 * Constructor
	 */
	protected ReferenceTableImpl() {
		// Nothing to do
	}

	@Override
	public void insert(ReferenceFilter referenceFilter, E element) throws ReferenceEntryAlreadyExistException {
		for (ReferenceFilter filter : table.keySet()) {
			if (referenceFilter.subFilterOf(filter)) {
				throw new ReferenceEntryAlreadyExistException();
			}
		}

		this.table.put(referenceFilter, element);
	}

	@Override
	public E retrieve(DirectReference reference) throws ReferenceEntryNotFoundException {
		for (Entry<ReferenceFilter, E> entry : table.entrySet()) {
			if (entry.getKey().accept(reference)) {
				return entry.getValue();
			}
		}
		throw new ReferenceEntryNotFoundException();
	}
}
