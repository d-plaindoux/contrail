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

package org.contrail.stream.component.pipeline.transducer.factory;

import org.contrail.stream.codec.coercion.Decoder;
import org.contrail.stream.codec.coercion.Encoder;
import org.contrail.stream.component.pipeline.transducer.DataTransducer;
import org.contrail.stream.component.pipeline.transducer.TransducerComponent;
import org.contrail.stream.component.pipeline.transducer.TransducerFactory;

/**
 * <code>PayLoadBasedSerializer</code> is in charge of transforming upstream
 * bytes to java object and vice-versa based on pay load. This class provides
 * dedicate encoder and decoder for such serialization based codec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class CoercionTransducerFactory<T> implements TransducerFactory<Object, T> {

	/**
	 * Accepted types
	 */
	private final Class<T> coercionType;

	/**
	 * Constructor
	 */
	public CoercionTransducerFactory(Class<T> coercionType) {
		this.coercionType = coercionType;
		// Prevent useless object creation
	}

	@Override
	public DataTransducer<Object, T> getDecoder() {
		return new Decoder<T>(this.coercionType);
	}

	@Override
	public DataTransducer<T, Object> getEncoder() {
		return new Encoder<T>(this.coercionType);
	}

	@Override
	public TransducerComponent<Object, Object, T, T> createComponent() {
		return new TransducerComponent<Object, Object, T, T>(getDecoder(), getEncoder());
	}
}
