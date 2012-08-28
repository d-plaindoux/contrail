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

import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailTransducer;
import org.wolfgang.contrail.component.annotation.ContrailType;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerFactory;
import org.wolfgang.contrail.connection.ContextFactory;

/**
 * <code>PayLoadBasedSerializer</code> is in charge of transforming upstream
 * bytes to java object and vice-versa based on pay load. This class provides
 * dedicate encoder and decoder for such serialization based codec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailTransducer(name = "Coercion", upType = @ContrailType(in = Object.class, out = Object.class))
public final class CoercionTransducerFactory<T> implements TransducerFactory<Object, T> {

	/**
	 * Accepted types
	 */
	private final Class<T> coercionType;

	/**
	 * Constructor
	 * 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@ContrailConstructor()
	public CoercionTransducerFactory(@ContrailArgument("context") ContextFactory factory, @ContrailArgument("type") String type) throws ClassNotFoundException {
		this.coercionType = (Class<T>) factory.getClassLoader().loadClass(type);
	}

	/**
	 * Constructor
	 * 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public CoercionTransducerFactory(ContextFactory factory, String[] type) throws ClassNotFoundException {
		this.coercionType = (Class<T>) factory.getClassLoader().loadClass(type[0]);
	}

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
