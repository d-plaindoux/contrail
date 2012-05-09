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

package org.wolfgang.contrail.data;

/**
 * A <code>DataInformation</code> provides basic information used for filtering
 * mechanism.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface DataInformation {

	/**
	 * Method called whether a given information must be retrieved using a given
	 * key with a required type.
	 * 
	 * @param key
	 *            The information key
	 * @param requiredType
	 *            The required type
	 * @return a value attached to the key with the required type
	 * @throws DataInformationValueNotFoundException
	 *             if the key has no explicit value
	 * @throws DataInformationValueTypeException
	 *             if the value type and the required type are not compatible
	 */
	<E> E getValue(String key, Class<E> requiredType) throws DataInformationValueNotFoundException,
			DataInformationValueTypeException;

	/**
	 * Method called whether a given information must be checked using a given
	 * key with a required type.
	 * 
	 * @param key
	 *            The information key
	 * @param requiredType
	 *            The required type
	 * @return true if the value attached to the key with the required type
	 *         exists
	 */
	<E> boolean hasValue(String key, Class<E> requiredType);

	/**
	 * Method called whether a given information must be stored using a given
	 * key and a given value.
	 * 
	 * @param key
	 *            The information key
	 * @param value
	 *            The value
	 * @throws DataInformationValueAlreadyDefinedException
	 *             if the key is already defined
	 */
	<E> void setValue(String key, E value) throws DataInformationValueAlreadyDefinedException;

}
