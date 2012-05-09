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

package org.wolfgang.contrail.network.codec.jaxb;

import org.wolfgang.contrail.component.transducer.DataTransducer;
import org.wolfgang.contrail.network.codec.payload.Bytes;

/**
 * <code>JAXBTransducerFactory</code> is in charge of transforming upstream
 * bytes to java object and vice-versa based on JAXB. This class provides
 * dedicate encoder and decoder for such serialization based codec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class JAXBTransducerFactory {

	/**
	 * Constructor
	 */
	private JAXBTransducerFactory() {
		// Prevent useless object creation
	}

	/**
	 * Method providing JAXB based decoder
	 * 
	 * @return a byte array to object data transformation
	 */
	public static DataTransducer<Bytes, Object> getDecoder(Class<?>[] types) {
		return new Decoder(types);
	}

	/**
	 * Method providing JAXB based encoder
	 * 
	 * @return a object to byte array data transformation
	 */

	public static DataTransducer<Object, Bytes> getEncoder(Class<?>[] types) {
		return new Encoder(types);
	}
}
