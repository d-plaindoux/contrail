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

package org.wolfgang.contrail.network.reference;

/**
 * <code>ReferenceFilter</code> is able to filter given direct reference. This
 * mechanism is linked to the {@link ReferenceTable}. In addition each filter
 * must also provides basic material for filter comparison used when new route
 * entry must be added.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface ReferenceFilter {

	/**
	 * Predicate used when the filter must be applied to a given direct
	 * reference
	 * 
	 * @param directReference
	 *            The reference to filter
	 * @return true if the filter accept the reference; false otherwise
	 */
	boolean accept(DirectReference directReference);

	/**
	 * Predicate used when the filter must be compared with a given one.
	 * reference
	 * 
	 * @param filter
	 *            The filter to compare
	 * @return true if the filter subsumes the current one; false otherwise
	 */
	boolean subFilterOf(ReferenceFilter filter);

}
