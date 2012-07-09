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
 * <code>Encoder</code> is capable to transform objects to payload based byte
 * array.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class Encoder<T> implements DataTransducer<T, Object> {

	/**
	 * An array of accepted types
	 */
	@SuppressWarnings("unused")
	private final Class<T> coercionType;

	/**
	 * Constructor
	 */
	Encoder(Class<T> coercionType) {
		super();
		this.coercionType = coercionType;
	}

	@Override
	public List<Object> transform(T source) throws DataTransducerException {
		return Arrays.asList((Object) source);
	}

	@Override
	public List<Object> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}