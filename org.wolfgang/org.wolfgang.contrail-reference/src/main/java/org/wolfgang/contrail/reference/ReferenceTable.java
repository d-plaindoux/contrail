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

/**
 * A <code>ReferenceTable</code> provides basic mechanisms used to retrieve
 * informations linked to a given reference
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface ReferenceTable<E> {

	/**
	 * Predicate checking a route entry existing for the direct reference
	 * 
	 * @param reference
	 *            The reference
	 * @return true if the reference has an entry; false otherwise
	 */
	boolean exist(DirectReference reference);

	/**
	 * Method called whether an entry must be retrieved using a given direct
	 * reference
	 * 
	 * @param reference
	 *            The reference to be used
	 * @return the corresponding entry (never <code>null</code>)
	 * @throws ReferenceEntryNotFoundException
	 */
	E retrieve(DirectReference reference) throws ReferenceEntryNotFoundException;

	/**
	 * Method called whether an entry must be added.
	 * 
	 * @param element
	 *            The element to be linked to the filter
	 * @param mainReference
	 *            The main direct reference
	 * @param references
	 *            Secondary references for indirect routes
	 * @throws ReferenceEntryAlreadyExistException
	 */
	void insert(E element, DirectReference mainReference, DirectReference... references)
			throws ReferenceEntryAlreadyExistException;
}
