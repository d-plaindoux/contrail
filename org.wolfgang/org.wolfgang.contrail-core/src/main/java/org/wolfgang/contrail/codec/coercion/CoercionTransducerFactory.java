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

package org.wolfgang.contrail.codec.coercion;

import org.wolfgang.contrail.codec.CodecFactory;
import org.wolfgang.contrail.component.pipeline.DataTransducer;

/**
 * <code>PayLoadBasedSerializer</code> is in charge of transforming upstream
 * bytes to java object and vice-versa based on pay load. This class provides
 * dedicate encoder and decoder for such serialization based codec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class CoercionTransducerFactory<T> implements CodecFactory<Object, T> {

	/**
	 * Accepted types
	 */
	private final Class<T> coercionType;

	/**
	 * Constructor
	 * 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public CoercionTransducerFactory(String... types) throws ClassNotFoundException {
		assert types.length == 1;
		this.coercionType = (Class<T>) this.getClass().forName(types[0]);
	}

	/**
	 * Constructor
	 */
	public CoercionTransducerFactory(Class<T> coercionType) {
		this.coercionType = coercionType;
		// Prevent useless object creation
	}

	/**
	 * Method providing payload based serialization decoder
	 * 
	 * @return a byte array to object data transformation
	 */
	public DataTransducer<Object, T> getDecoder() {
		return new Decoder<T>(this.coercionType);
	}

	/**
	 * Method providing payload based serialization encoder
	 * 
	 * @return a object to byte array data transformation
	 */

	public DataTransducer<T, Object> getEncoder() {
		return new Encoder<T>(this.coercionType);
	}
}
