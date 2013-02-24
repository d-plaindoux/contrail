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

package org.wolfgang.contrail.component.pipeline.transducer.factory;

import java.util.Map;

import org.wolfgang.contrail.codec.object.Decoder;
import org.wolfgang.contrail.codec.object.Encoder;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerFactory;
import org.wolfgang.contrail.data.JSonifier;

/**
 * <code>ObjectTransducerFactory</code> is in charge of transforming upstream
 * object to java object and vice-versa. This class provides dedicate encoder
 * and decoder for basic object encoding/decoding.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ObjectTransducerFactory implements TransducerFactory<Object, Object> {

	private final Map<String, JSonifier> drivers;

	public ObjectTransducerFactory(Map<String, JSonifier> drivers) {
		this.drivers = drivers;
	}

	@Override
	public DataTransducer<Object, Object> getDecoder() {
		return new Decoder(drivers);
	}

	@Override
	public DataTransducer<Object, Object> getEncoder() {
		return new Encoder(drivers);
	}

	@Override
	public TransducerComponent<Object, Object, Object, Object> createComponent() {
		return new TransducerComponent<Object, Object, Object, Object>(getDecoder(), getEncoder());
	}
}
