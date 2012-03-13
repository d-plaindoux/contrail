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

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.utils.Coercion;

/**
 * <code>DataInformationImpl</code> its a basic and standard implementation for
 * the {@link DataInformation} type.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class DataInformationImpl implements DataInformation {

	/**
	 * The attributes map
	 */
	private final Map<String, Object> attributes;

	{
		this.attributes = new HashMap<String, Object>();
	}

	/**
	 * Constructor
	 */
	DataInformationImpl() {
		super();
	}

	@Override
	public <E> E getValue(String key, Class<E> requiredType) throws DataInformationValueNotFoundException,
			DataInformationValueTypeException {
		final Object object = this.attributes.get(key);
		if (object == null) {
			throw new DataInformationValueNotFoundException();
		} else if (!Coercion.canCoerce(object, requiredType)) {
			throw new DataInformationValueTypeException();
		} else {
			return Coercion.coerce(object, requiredType);
		}
	}

	@Override
	public <E> void setValue(String key, E value) throws DataInformationValueAlreadyDefinedException {
		final Object object = this.attributes.get(key);
		if (object == null) {
			this.attributes.put(key, value);
		} else {
			throw new DataInformationValueAlreadyDefinedException();
		}
	}

}
