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

package org.wolfgang.contrail.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.wolfgang.contrail.codec.object.Decoder;
import org.wolfgang.contrail.codec.object.Encoder;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;

public class JSonifier {

	private String[] names;
	private Class<?>[] types;

	public JSonifier() {
		super();
	}

	public JSonifier withNames(String... names) {
		this.names = names;
		return this;
	}

	public JSonifier withTypes(Class<?>... types) {
		this.types = types;
		return this;
	}

	public ObjectRecord toStructure(Object object, Encoder encoder) throws DataTransducerException {
		try {
			final ObjectRecord parameters = new ObjectRecord();

			for (String name : names) {
				final Field field = object.getClass().getField(name);
				parameters.set(name, encoder.encode(field.get(object)));
			}

			return new ObjectRecord().set("jN", object.getClass().getName()).set("jV", parameters);
		} catch (DataTransducerException e) {
			throw e;
		} catch (Exception e) {
			throw new DataTransducerException(e);
		}
	}

	public Object toObject(ObjectRecord object, Decoder decoder) throws DataTransducerException {
		try {
			final Class<?> model = Class.forName(object.get("jN", String.class));
			final ObjectRecord objectRecord = object.get("jV", ObjectRecord.class);
			final Constructor<?> constructor = model.getConstructor(types);
			final Object[] parameters = new Object[types.length];
			for (int i = 0; i < types.length; i++) {
				parameters[i] = decoder.decode(objectRecord.get(names[i]));
			}
			return constructor.newInstance(parameters);
		} catch (DataTransducerException e) {
			throw e;
		} catch (Exception e) {
			throw new DataTransducerException(e);
		}
	}
}