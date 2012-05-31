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

package org.wolfgang.contrail.network.codec.serializer;

import org.wolfgang.contrail.component.transducer.DataTransducer;
import org.wolfgang.contrail.network.codec.CodecFactory;
import org.wolfgang.contrail.network.codec.payload.Bytes;

/**
 * <code>PayLoadBasedSerializer</code> is in charge of transforming upstream
 * bytes to java object and vice-versa based on pay load. This class provides
 * dedicate encoder and decoder for such serialization based codec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class SerializationTransducerFactory implements CodecFactory<Bytes, Object> {

	/**
	 * Accepted types
	 */
	private final Class<?>[] types;

	/**
	 * Constructor
	 */
	@SuppressWarnings("static-access")
	public SerializationTransducerFactory() {
		this.types = new Class[0];
	}

	/**
	 * Constructor
	 * 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("static-access")
	public SerializationTransducerFactory(String... types) throws ClassNotFoundException {
		this.types = new Class[types.length];
		for (int i = 0; i < types.length; i++) {
			this.types[i] = this.getClass().forName(types[i]);
		}
	}

	/**
	 * Constructor
	 */
	public SerializationTransducerFactory(Class<?>... acceptedTypes) {
		this.types = acceptedTypes;
		// Prevent useless object creation
	}

	/**
	 * Method providing payload based serialization decoder
	 * 
	 * @return a byte array to object data transformation
	 */
	public DataTransducer<Bytes, Object> getDecoder() {
		return new Decoder(types);
	}

	/**
	 * Method providing payload based serialization encoder
	 * 
	 * @return a object to byte array data transformation
	 */

	public DataTransducer<Object, Bytes> getEncoder() {
		return new Encoder(types);
	}
}
