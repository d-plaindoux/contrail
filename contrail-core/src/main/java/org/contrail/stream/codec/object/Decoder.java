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

package org.contrail.stream.codec.object;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.contrail.common.utils.Coercion;
import org.contrail.stream.component.pipeline.transducer.DataTransducer;
import org.contrail.stream.component.pipeline.transducer.DataTransducerException;
import org.contrail.stream.data.JSonifier;
import org.contrail.stream.data.ObjectRecord;

/**
 * <code>Decoder</code> is able to transform an object to a serializable object
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Decoder implements DataTransducer<Object, Object> {

	private Map<String, JSonifier> drivers;

	/**
	 * Constructor
	 */
	public Decoder(List<JSonifier> jSonifiers) {
		super();

		this.drivers = new HashMap<String, JSonifier>();
		
		for(JSonifier jSonifier : jSonifiers) {
			this.drivers.put(jSonifier.getIdentifier(), jSonifier);
		}
	}
	private Object[] decodeArray(Object source) throws DataTransducerException {
		final Object[] result = new Object[Array.getLength(source)];
		
		for (int i = 0; i < Array.getLength(source); i++) {
			result[i] = decode(Array.get(source, i));
		}
		return result;
	}

	private Object decodeObjectRecord(final ObjectRecord sourceRecord) throws DataTransducerException {
		final ObjectRecord result = new ObjectRecord();
		for (String name : sourceRecord.getNames()) {
			result.set(name, decode(sourceRecord.get(name)));
		}
		return result;
	}

	public Object decode(Object source) throws DataTransducerException {
		if (source == null) {
			return source;
		} else if (Coercion.canCoerce(source, ObjectRecord.class)) {
			final ObjectRecord sourceRecord = Coercion.coerce(source, ObjectRecord.class);
			if (sourceRecord.has("jN", String.class) && sourceRecord.has("jV", ObjectRecord.class)) {
				final String driverName = sourceRecord.get("jN", String.class);
				if (drivers.containsKey(driverName)) {
					return drivers.get(driverName).toObject(sourceRecord, this);
				} else {
					// Late binding
					return decodeObjectRecord(sourceRecord);
				}
			} else {
				return decodeObjectRecord(sourceRecord);
			}
		} else if (source.getClass().isArray()) {
			final Object[] result = decodeArray(source);
			return result;
		} else if (Coercion.canCoerce(source, Integer.class)) {
			return source;
		} else if (Coercion.canCoerce(source, Float.class)) {
			return source;
		} else if (Coercion.canCoerce(source, Double.class)) {
			return source;
		} else if (Coercion.canCoerce(source, Boolean.class)) {
			return source;
		} else if (Coercion.canCoerce(source, String.class)) {
			return source;
		} else {
			throw new DataTransducerException("Object decoding for " + source.getClass() + " is not supported");
		}
	}

	@Override
	public List<Object> transform(Object source) throws DataTransducerException {
		return Arrays.asList(decode(source));
	}

	@Override
	public List<Object> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}