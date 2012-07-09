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

package org.wolfgang.contrail.codec.payload;

import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.codec.CodecFactory;
import org.wolfgang.contrail.component.pipeline.DataTransducer;
import org.wolfgang.contrail.component.pipeline.TransducerComponent;

/**
 * <code>PayLoadTransducerFactory</code> is in charge of transforming upstream
 * byte array to consistent byte arrays and vice-versa based on pay load. This
 * class provides dedicate encoder and decoder for such serialization based
 * CoDec
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class PayLoadTransducerFactory implements CodecFactory<byte[], Bytes> {

	/**
	 * Constructor
	 */
	public PayLoadTransducerFactory() {
		// Prevent useless object creation
	}

	private Pair<Class<Bytes>, Class<Bytes>> getSourceTypes() {
		return Pair.create(Bytes.class, Bytes.class);
	}

	@Override
	public DataTransducer<byte[], Bytes> getDecoder() {
		return new Decoder();
	}

	@Override
	public DataTransducer<Bytes, byte[]> getEncoder() {
		return new Encoder();
	}

	@Override
	public TransducerComponent<byte[], byte[], Bytes, Bytes> getComponent() {
		return new TransducerComponent<byte[], byte[], Bytes, Bytes>(getSourceTypes(), getDecoder(), getEncoder());
	}
}
