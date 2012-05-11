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

package org.wolfgang.contrail.network.codec.payload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.wolfgang.common.utils.Marshall;
import org.wolfgang.common.utils.Option;
import org.wolfgang.contrail.component.transducer.DataTransducer;
import org.wolfgang.contrail.component.transducer.DataTransducerException;

/**
 * <code>Decoder</code> is able to transform a byte stream to a payload based
 * array.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class Decoder implements DataTransducer<byte[], Bytes> {

	/**
	 * Static definition for integer length representation (32 bits)
	 */
	private static final int INT_LEN = 4;

	/**
	 * The empty buffer
	 */
	private static final byte[] EMPTY_BUFFER = new byte[0];

	/**
	 * The decoding buffer used for the object de-serialization
	 */
	private byte[] buffer;

	/**
	 * Constructor
	 */
	Decoder() {
		super();
		this.buffer = EMPTY_BUFFER;
	}

	/**
	 * Method in charge of object decoding if possible. The monadic result
	 * reflects the system capability to decode or not an object from the source
	 * 
	 * @return an optional object
	 * @throws IOException
	 *             Thrown if the decoding stage fails while it reads data from
	 *             the source
	 * @throws ClassNotFoundException
	 *             Thrown if the required class is not know
	 */
	private Option<Bytes> getNext() throws IOException, ClassNotFoundException {
		if (buffer.length < INT_LEN) {
			return Option.none();
		} else {
			// Read the coded object payload first
			final int payLoad = Marshall.bytesToInt(buffer);
			if (buffer.length - INT_LEN < payLoad) {
				return Option.none();
			} else {
				final byte[] array = Arrays.copyOfRange(buffer, INT_LEN, payLoad + INT_LEN);
				buffer = Arrays.copyOfRange(buffer, payLoad + INT_LEN, buffer.length);
				return Option.some(new Bytes(array));
			}
		}
	}

	@Override
	public List<Bytes> transform(byte[] source) throws DataTransducerException {
		try {
			// First concatenate remaining buffer with the new one
			final byte[] newBuffer = new byte[buffer.length + source.length];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			System.arraycopy(source, 0, newBuffer, buffer.length, source.length);
			buffer = newBuffer;

			// Try to decode objects
			final List<Bytes> objects = new ArrayList<Bytes>();
			for (Option<Bytes> option = getNext(); option.haveSome(); option = getNext()) {
				objects.add(option.getValue());
			}

			return objects;
		} catch (IOException e) {
			throw new DataTransducerException(e);
		} catch (ClassNotFoundException e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<Bytes> finish() throws DataTransducerException {
		if (buffer.length > 0) {
			throw new DataTransducerException();
		} else {
			return Arrays.asList();
		}
	}
}