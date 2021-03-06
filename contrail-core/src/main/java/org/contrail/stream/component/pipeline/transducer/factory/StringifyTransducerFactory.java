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

import org.contrail.stream.codec.payload.Bytes;
import org.contrail.stream.codec.stringify.Decoder;
import org.contrail.stream.codec.stringify.Encoder;
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
public final class StringifyTransducerFactory implements TransducerFactory<String, Bytes> {

	/**
	 * Constructor
	 */
	public StringifyTransducerFactory() {
	}

	@Override
	public DataTransducer<String, Bytes> getDecoder() {
		return new Decoder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.contrail.stream.component.pipeline.transducer.TransducerFactory
	 * #getEncoder()
	 */
	@Override
	public DataTransducer<Bytes, String> getEncoder() {
		return new Encoder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.contrail.stream.component.pipeline.transducer.TransducerFactory
	 * #createComponent()
	 */
	@Override
	public TransducerComponent<String, String, Bytes, Bytes> createComponent() {
		return new TransducerComponent<String, String, Bytes, Bytes>(getDecoder(), getEncoder());
	}
}
