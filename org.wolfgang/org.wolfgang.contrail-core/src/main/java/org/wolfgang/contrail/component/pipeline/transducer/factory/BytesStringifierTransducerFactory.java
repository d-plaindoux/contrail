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

import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.codec.stringify.Decoder;
import org.wolfgang.contrail.codec.stringify.Encoder;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerFactory;

/**
 * <code>StringifyTransducerFactory</code> is in charge of transforming upstream
 * string to byte arrays and vice-versa based on pay load.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class BytesStringifierTransducerFactory implements TransducerFactory<String, Bytes> {

	/**
	 * Constructor
	 */
	public BytesStringifierTransducerFactory() {
		// Prevent useless object creation
	}

	@Override
	public DataTransducer<String, Bytes> getDecoder() {
		return new Decoder();
	}

	@Override
	public DataTransducer<Bytes, String> getEncoder() {
		return new Encoder();
	}

	@Override
	public TransducerComponent<String, String, Bytes, Bytes> createComponent() {
		return new TransducerComponent<String, String, Bytes, Bytes>(getDecoder(), getEncoder());
	}
}
