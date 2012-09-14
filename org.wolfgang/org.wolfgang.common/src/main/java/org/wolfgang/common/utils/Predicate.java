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

package org.wolfgang.common.utils;

/**
 * <code>Predicate</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class Predicate {

	private Predicate() {
		// Prevent useless object construction
	}

	/**
	 * Predicate checking the equality between two objects with can be
	 * <code>null</code>
	 * 
	 * @param obj1
	 *            an object
	 * @param obj2
	 *            an object
	 * @return true if these objects are equal
	 */
	public static boolean isNullOfEquals(Object obj1, Object obj2) {
		if (obj1 == obj2) {
			return true;
		} else if (obj1 == null || obj2 == null) {
			return false;
		} else {
			return obj1.equals(obj2);
		}
	}

	/**
	 * Predicate checking the equality between two objects arrays with can be
	 * <code>null</code>
	 * 
	 * @param obj1
	 *            an objects array
	 * @param obj2
	 *            an objects array
	 * @return true if these arrays are equal
	 */
	public static boolean isNullOfEquals(Object[] obj1, Object[] obj2) {
		if (obj1 == obj2) {
			return true;
		} else if (obj1 == null || obj2 == null) {
			return false;
		} else if (obj1.length == obj2.length) {
			for (int i = 0; i < obj1.length; i++) {
				if (obj1[i].equals(obj2[i]) == false) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
