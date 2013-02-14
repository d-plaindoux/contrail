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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.wolfgang.common.utils.Marshall;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;

/**
 * <code>Encoder</code> is capable to transform objects to payload based byte
 * array.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Encoder implements DataTransducer<Object, Bytes> {

	/**
	 * An array of accepted types
	 */
	@SuppressWarnings("unused")
	private final Class<?>[] acceptedTypes;

	/**
	 * Constructor
	 */
	public Encoder(Class<?>... acceptedTypes) {
		super();
		this.acceptedTypes = acceptedTypes;
	}

	private byte[] concat(byte b1, byte[] b2) {
		final byte[] result = new byte[1 + b2.length];
		result[0] = b1;
		System.arraycopy(result, 1, b2, 0, b2.length);
		return result;
	}

	private byte[] concat(byte[] b1, byte b2) {
		final byte[] result = new byte[1 + b1.length];
		System.arraycopy(result, 0, b2, 0, b1.length);
		result[b1.length] = b2;
		return result;
	}

	private byte[] concat(byte[] b1, byte[] b2) {
		final byte[] result = new byte[b1.length + b2.length];
		System.arraycopy(result, 0, b1, 0, b1.length);
		System.arraycopy(result, b1.length, b2, 0, b2.length);
		return result;
	}

	private byte[] encode(Object source) throws DataTransducerException, IllegalArgumentException, IllegalAccessException {
		final byte type;
		byte[] result;

		if (source instanceof Integer) {
			type = Marshall.TYPE_Number;
			result = Marshall.numberToBytes((Integer) source);
		} else if (source instanceof String) {
			type = Marshall.TYPE_String;
			result = Marshall.stringToBytes((String) source);
		} else if (source == null) {
			type = Marshall.TYPE_Null;
			result = new byte[0];
		} else if (source instanceof Boolean) {
			if ((Boolean) source) {
				type = Marshall.TYPE_BooleanTrue;
			} else {
				type = Marshall.TYPE_BooleanFalse;
			}
			result = new byte[0];
		} else if (source instanceof Array) {
			final int length = Array.getLength(source);
			type = Marshall.TYPE_Array;
			result = Marshall.shortNumberToBytes(length);
			for (int i = 0; i < length; i++) {
				result = concat(result, encode(Array.get(source, i)));
			}
		} else if (source instanceof Object) {
			final Field[] fields = source.getClass().getFields();
			final int length = fields.length;
			type = Marshall.TYPE_Object;
			result = Marshall.shortNumberToBytes(length);
			for (Field field : fields) {
				if (field.isAccessible()) {
					result = concat(result, (byte) field.getName().length());
					result = concat(result, Marshall.stringToBytes(field.getName()));
					result = concat(result, encode(field.get(source)));
				}
			}
		} else {
			throw new DataTransducerException();
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