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

package org.contrail.stream.codec.payload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.contrail.common.utils.Marshall;
import org.contrail.stream.component.pipeline.transducer.DataTransducer;
import org.contrail.stream.component.pipeline.transducer.DataTransducerException;

/**
 * <code>Encoder</code> is capable to transform a byte array to another one with
 * a prefix as a payload.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Encoder implements DataTransducer<Bytes, byte[]> {

	/**
	 * Constructor
	 */
	public Encoder() {
		super();
	}

	@Override
	public List<byte[]> transform(Bytes source) throws DataTransducerException {
		try {
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try {
				final byte[] bytes = source.getContent();
				stream.write(Marshall.numberToBytes(bytes.length));
				stream.write(bytes);
			} finally {
				stream.close();
			}
			return Arrays.asList(stream.toByteArray());
		} catch (IOException e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<byte[]> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}
