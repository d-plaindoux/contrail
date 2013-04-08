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
 * <code>Coercion</code> is a simple module providing coercion mechanisms like a
 * predicate checking the coercion validity and the operation.
 * 
 * @author Didier Plaindoux
 */
public final class Coercion {

	/**
	 * Constructor
	 */
	private Coercion() {
		// Prevent useless creation
	}

	/**
	 * @param object
	 * @param type
	 * @return
	 */
	private static boolean canCoerceToType(Object object, Class<?> type) {
		return object != null && type != null && type.isAssignableFrom(object.getClass());
	}

	/**
	 * Method called whether a given object coercion must be checked
	 * 
	 * @param object
	 *            The object
	 * @param type
	 *            The required type
	 * @return true if the object can be coerced to the given type
	 */
	public static boolean canCoerce(Object object, Class<?> primary, Class<?>... types) {
		boolean coerce = canCoerceToType(object, primary);

		for (Class<?> type : types) {
			coerce = coerce && canCoerceToType(object, type);
		}

		return coerce;
	}

	/**
	 * Method called whether a given object coercion must be done
	 * 
	 * @param object
	 *            The object
	 * @param type
	 *            The required type
	 * @return the object with the required type
	 * @throws ClassCastException
	 *             thrown if the object can not be coerced
	 */
	public static <T> T coerce(Object object, Class<T> type) {
		assert object != null && type != null;
		return type.cast(object);
	}
}
