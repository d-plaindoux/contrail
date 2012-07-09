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

package org.wolfgang.contrail.component.pipeline.transducer.coercion;

import java.util.Arrays;
import java.util.List;

import org.wolfgang.contrail.component.pipeline.DataTransducer;
import org.wolfgang.contrail.component.pipeline.DataTransducerException;

/**
 * <code>Decoder</code> is able to transform a payload based array to an object
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class Decoder<T> implements DataTransducer<Object, T> {

	/**
	 * Accepted types for the decoding
	 */
	private final Class<T> coercionType;

	/**
	 * Constructor
	 */
	Decoder(Class<T> coercionType) {
		super();
		this.coercionType = coercionType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> transform(Object source) throws DataTransducerException {
		if (source == null || coercionType.isAssignableFrom(source.getClass())) {
			return Arrays.asList(coercionType.cast(source));
		} else {
			throw new DataTransducerException("TODO - coercion problem");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}