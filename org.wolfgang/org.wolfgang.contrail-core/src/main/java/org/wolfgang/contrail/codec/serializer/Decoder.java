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

package org.wolfgang.contrail.codec.serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.wolfgang.common.utils.Marshall;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.DataTransducer;
import org.wolfgang.contrail.component.pipeline.DataTransducerException;

/**
 * <code>Decoder</code> is able to transform a payload based array to an object
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class Decoder implements DataTransducer<Bytes, Object> {

	/**
	 * Accepted types for the decoding
	 */
	@SuppressWarnings("unused")
	private final Class<?>[] acceptedTypes;

	/**
	 * Constructor
	 */
	Decoder(Class<?>... acceptedTypes) {
		super();
		this.acceptedTypes = acceptedTypes;
	}

	@Override
	public List<Object> transform(Bytes source) throws DataTransducerException {
		try {
			return Arrays.asList(Marshall.bytesToObject(source.getContent()));
		} catch (IOException e) {
			throw new DataTransducerException(e);
		} catch (ClassNotFoundException e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<Object> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}