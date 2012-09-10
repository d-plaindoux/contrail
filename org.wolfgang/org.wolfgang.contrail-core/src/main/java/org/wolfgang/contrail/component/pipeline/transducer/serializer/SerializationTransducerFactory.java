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

package org.wolfgang.contrail.component.pipeline.transducer.serializer;

import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailTransducer;
import org.wolfgang.contrail.component.annotation.ContrailType;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.Bytes;

/**
 * <code>PayLoadBasedSerializer</code> is in charge of transforming upstream
 * bytes to java object and vice-versa based on pay load. This class provides
 * dedicate encoder and decoder for such serialization based codec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailTransducer(upType = @ContrailType(in = Bytes.class, out = Bytes.class), downType = @ContrailType(in = Object.class, out = Object.class))
public final class SerializationTransducerFactory implements TransducerFactory<Bytes, Object> {

	/**
	 * Accepted types
	 */
	private final Class<?>[] types;

	/**
	 * Constructor
	 */
	@ContrailConstructor
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

	@Override
	public DataTransducer<Bytes, Object> getDecoder() {
		return new Decoder(types);
	}

	@Override
	public DataTransducer<Object, Bytes> getEncoder() {
		return new Encoder(types);
	}

	@Override
	public TransducerComponent<Bytes, Bytes, Object, Object> createComponent() {
		return new TransducerComponent<Bytes, Bytes, Object, Object>(getDecoder(), getEncoder());
	}
}
