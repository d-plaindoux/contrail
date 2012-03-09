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

package org.wolfgang.contrail.network.codec.serializer;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.wolfgang.common.utils.Marshall;
import org.wolfgang.contrail.component.transducer.DataTransducer;
import org.wolfgang.contrail.component.transducer.DataTransducerException;

/**
 * <code>Encoder</code> is capable to transform objects to byte array with a
 * prefix as a payload.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class Encoder implements DataTransducer<Object, byte[]> {

	/**
	 * Constructor
	 */
	Encoder() {
		super();
	}

	@Override
	public List<byte[]> transform(Object source) throws DataTransducerException {
		try {
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try {
				final byte[] bytes = Marshall.objectToBytes(source);
				stream.write(Marshall.intToBytes(bytes.length));
				stream.write(bytes);
			} finally {
				stream.close();
			}
			return Arrays.asList(stream.toByteArray());
		} catch (Exception e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<byte[]> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}