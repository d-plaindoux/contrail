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

package org.wolfgang.contrail.reference;

import java.util.HashMap;
import java.util.Map;

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
	private final Map<DirectReference, E> table;

	{
		this.table = new HashMap<DirectReference, E>();
	}

	/**
	 * Constructor
	 */
	protected ReferenceTableImpl() {
		// Nothing to do
	}

	@Override
	public void insert(E element, DirectReference mainReference, DirectReference... references) throws ReferenceEntryAlreadyExistException {
		if (table.containsKey(mainReference)) {
			throw new ReferenceEntryAlreadyExistException();
		} else {
			this.table.put(mainReference, element);
		}

		for (DirectReference reference : references) {
			if (table.containsKey(reference)) {
				// Ignore ?
			} else {
				this.table.put(reference, element);
			}
		}
	}

	@Override
	public boolean exist(DirectReference reference) {
		return this.table.containsKey(reference);
	}

	@Override
	public E retrieve(DirectReference reference) throws ReferenceEntryNotFoundException {
		if (table.containsKey(reference)) {
			return table.get(reference);
		} else {
			throw new ReferenceEntryNotFoundException();
		}
	}
}
