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

package org.wolfgang.contrail.codec.identity;

import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.codec.CodecFactory;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.DataTransducer;

/**
 * <code>SerializerTransducerFactory</code> is in charge of transforming
 * upstream bytes to java object and vice-versa based on pay load. This class
 * provides dedicate encoder and decoder for such serialization based CoDec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class IdentityTransducerFactory<A> implements CodecFactory<A, A> {

	private final Class<A> type;

	/**
	 * Constructor
	 */
	public IdentityTransducerFactory(Class<A> type) {
		this.type = type;
	}

	@Override
	public Pair<Class<A>, Class<A>> getTypes() {
		return Pair.create(type, type);
	}

	/**
	 * Method providing pay-load based decoder
	 * 
	 * @return a byte array to object data transformation
	 */
	public DataTransducer<A, A> getDecoder() {
		return new Identity<A>();
	}

	/**
	 * Method providing pay-load based decoder
	 * 
	 * @return a byte array to object data transformation
	 */
	public DataTransducer<A, A> getEncoder() {
		return new Identity<A>();
	}
}
