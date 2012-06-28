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

import java.util.Arrays;
import java.util.Collection;

/**
 * <code>ReferenceFilter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ReferenceFilterFactory {

	/**
	 * <code>ReferenceFilterInCollection</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private static class ReferenceFilterInCollection implements ReferenceFilter {
		final Collection<DirectReference> collection;

		/**
		 * Constructor
		 * 
		 * @param collection
		 */
		private ReferenceFilterInCollection(Collection<DirectReference> collection) {
			super();
			this.collection = collection;
		}

		@Override
		public boolean accept(DirectReference directReference) {
			return collection.contains(directReference);
		}

		@Override
		public boolean subFilterOf(ReferenceFilter filter) {
			for (DirectReference reference : collection) {
				if (!filter.accept(reference)) {
					return false;
				}
			}

			return true;
		}
	}

	/**
	 * Constructor
	 */
	private ReferenceFilterFactory() {
		// Prevent useless object construction
	}

	/**
	 * Method building a reference filter able to accept a finite set of direct
	 * reference
	 * 
	 * @param references
	 *            The array of direct reference to be used by the filter
	 * @return a reference filter
	 */
	public static ReferenceFilter in(final DirectReference... references) {
		assert references.length > 0;
		return new ReferenceFilterInCollection(Arrays.asList(references));
	}
}
