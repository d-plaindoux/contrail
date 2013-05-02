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

package org.contrail.stream.codec.serialize;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.contrail.common.utils.Marshall;
import org.contrail.common.utils.Pair;
import org.contrail.stream.codec.payload.Bytes;
import org.contrail.stream.component.pipeline.transducer.DataTransducer;
import org.contrail.stream.component.pipeline.transducer.DataTransducerException;
import org.contrail.stream.data.ObjectRecord;

/**
 * <code>Decoder</code> is able to transform a payload based array to an object
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Decoder implements DataTransducer<Bytes, Object> {

	/**
	 * Constructor
	 */
	public Decoder() {
		super();
	}

	private Pair<Integer, Object> decode(byte[] array, int offset) throws IOException, DataTransducerException {
		int length;
		int size;

		final Object result;

		switch (array[offset]) {
		case Marshall.TYPE_String:
			length = Marshall.bytesToShortNumberWithOffset(array, offset + 1);
			size = 1 + Marshall.SIZE_ShortNumber;
			result = Marshall.bytesToStringWithOffset(array, offset + size, length);
			size += length * Marshall.SIZE_Character;
			break;

		case Marshall.TYPE_Number:
			result = Marshall.bytesToNumberWithOffset(array, offset + 1);
			size = 1 + Marshall.SIZE_Number;
			break;

		case Marshall.TYPE_Float:
			result = Marshall.bytesToFloatWithOffset(array, offset + 1);
			size = 1 + Marshall.SIZE_Number;
			break;

		case Marshall.TYPE_Undefined:
			result = null;
			size = 1;
			break;

		case Marshall.TYPE_Null:
			result = null;
			size = 1;
			break;

		case Marshall.TYPE_BooleanTrue:
			result = true;
			size = 1;
			break;

		case Marshall.TYPE_BooleanFalse:
			result = false;
			size = 1;
			break;

		case Marshall.TYPE_Array:
			length = Marshall.bytesToShortNumberWithOffset(array, offset + 1);
			size = 1 + Marshall.SIZE_ShortNumber;
			result = new Object[length];
			for (int i = 0; i < length; i += 1) {
				final Pair<Integer, Object> decoded = this.decode(array, offset + size);
				size += decoded.getFirst();
				((Object[]) result)[i] = decoded.getSecond();
			}
			break;

		case Marshall.TYPE_Object:
			result = new ObjectRecord();
			length = Marshall.bytesToShortNumberWithOffset(array, offset + 1);
			size = 1 + Marshall.SIZE_ShortNumber;
			for (int i = 0; i < length; i += 1) {
				final int name_length = Marshall.bytesToShortNumberWithOffset(array, offset + size);
				size += Marshall.SIZE_ShortNumber;
				final String key = Marshall.bytesToStringWithOffset(array, offset + size, name_length);
				size += name_length * Marshall.SIZE_Character;
				final Pair<Integer, Object> decoded = this.decode(array, offset + size);
				size += decoded.getFirst();
				((ObjectRecord) result).set(key, decoded.getSecond());
			}
			break;

		default:
			throw new DataTransducerException("L.data.not.deserializable");
		}

		return new Pair<Integer, Object>(size, result);
	}

	@Override
	public List<Object> transform(Bytes source) throws DataTransducerException {
		try {
			return Arrays.asList(decode(source.getContent(), 0).getSecond());
		} catch (IOException e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<Object> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}