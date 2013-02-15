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

package org.wolfgang.contrail.codec.serialize;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.common.utils.Marshall;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;
import org.wolfgang.contrail.data.ObjectRecord;

/**
 * <code>Encoder</code> is capable to transform objects to payload based byte
 * array.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Encoder implements DataTransducer<Object, Bytes> {

	/**
	 * Constructor
	 */
	public Encoder() {
		super();
	}

	private byte[] concat(byte b1, byte[] b2) {
		final byte[] result = new byte[1 + b2.length];
		result[0] = b1;
		System.arraycopy(b2, 0, result, 1, b2.length);
		return result;
	}

	private byte[] concat(byte[] b1, byte[] b2) {
		final byte[] result = new byte[b1.length + b2.length];
		System.arraycopy(b1, 0, result, 0, b1.length);
		System.arraycopy(b2, 0, result, b1.length, b2.length);
		return result;
	}

	private byte[] encode(Object source) throws DataTransducerException, IllegalArgumentException, IllegalAccessException {
		final byte type;
		byte[] result;

		if (Coercion.canCoerce(source, Integer.class)) {
			final Integer intValue = Coercion.coerce(source, Integer.class);
			type = Marshall.TYPE_Number;
			result = Marshall.numberToBytes(intValue);
		} else if (Coercion.canCoerce(source, String.class)) {
			type = Marshall.TYPE_String;
			final String stringValue = Coercion.coerce(source, String.class);
			result = Marshall.shortNumberToBytes(stringValue.length());
			result = concat(result, Marshall.stringToBytes(stringValue));
		} else if (source == null) {
			type = Marshall.TYPE_Null;
			result = new byte[0];
		} else if (Coercion.canCoerce(source, Boolean.class)) {
			final boolean boolValue = Coercion.coerce(source, Boolean.class);
			type = boolValue ? Marshall.TYPE_BooleanTrue : Marshall.TYPE_BooleanFalse;
			result = new byte[0];
		} else if (source.getClass().isArray()) {
			final int length = Array.getLength(source);
			type = Marshall.TYPE_Array;
			result = Marshall.shortNumberToBytes(length);
			for (int i = 0; i < length; i++) {
				result = concat(result, encode(Array.get(source, i)));
			}
		} else if (Coercion.canCoerce(source, ObjectRecord.class)) {
			final ObjectRecord record = Coercion.coerce(source, ObjectRecord.class);
			final Set<String> fields = record.getNames();
			final int length = fields.size();
			type = Marshall.TYPE_Object;
			result = Marshall.shortNumberToBytes(length);
			for (String field : fields) {
				result = concat(result, Marshall.shortNumberToBytes(field.length()));
				result = concat(result, Marshall.stringToBytes(field));
				result = concat(result, encode(record.get(field)));
			}
		} else {
			throw new DataTransducerException("Serialization encoding not supported for " + source.getClass());
		}

		return concat(type, result);
	}

	@Override
	public List<Bytes> transform(Object source) throws DataTransducerException {
		try {
			return Arrays.asList(new Bytes(encode(source)));
		} catch (IllegalArgumentException e) {
			throw new DataTransducerException(e);
		} catch (IllegalAccessException e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<Bytes> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}