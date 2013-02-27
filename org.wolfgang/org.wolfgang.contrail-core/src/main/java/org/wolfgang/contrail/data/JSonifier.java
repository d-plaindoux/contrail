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
import java.lang.reflect.Method;

import org.wolfgang.contrail.codec.object.Decoder;
import org.wolfgang.contrail.codec.object.Encoder;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;

public class JSonifier {

	public static String JSonName = "jN";
	public static String JSonValue = "jV";

	public static JSonifierWithoutNames nameAndType(String identifier, String typeName) {
		return new JSonifierWithoutNames(identifier, typeName);
	}

	public static class JSonifierWithoutNames {
		private final String identifier;
		private final String typeName;

		private JSonifierWithoutNames(String identifier, String typeName) {
			super();

			this.identifier = identifier;
			this.typeName = typeName;
		}

		public JSonifierWithoutTypes withKeys(String... names) {
			return new JSonifierWithoutTypes(identifier, typeName, names);
		}
	}

	public static class JSonifierWithoutTypes {
		private final String identifier;
		private final String typeName;
		private final String[] names;

		private JSonifierWithoutTypes(String identifier, String typeName, String[] names) {
			super();
			this.identifier = identifier;
			this.typeName = typeName;
			this.names = names;
		}

		public JSonifier withTypes(Class<?>... types) {
			return new JSonifier(identifier, typeName, names, types);
		}
	}

	private final String identifier;
	private final String[] names;
	private final Class<?>[] types;
	private final String typeName;

	private JSonifier(String identifier, String typeName, String[] names, Class<?>[] types) {
		super();
		assert names.length == types.length;

		this.identifier = identifier;
		this.typeName = typeName;
		this.names = names;
		this.types = types;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getTypeName() {
		return typeName;
	}

	private String getBeanName(String name) {
		final StringBuilder builder = new StringBuilder("get");
		builder.append(Character.toUpperCase(name.charAt(0))).append(name.substring(1));
		return builder.toString();
	}

	public ObjectRecord toStructure(Object object, Encoder encoder) throws DataTransducerException {
		try {
			final ObjectRecord parameters = new ObjectRecord();

			for (String name : names) {
				try {
					final String methodName = getBeanName(name);
					final Method method = object.getClass().getMethod(methodName);
					parameters.set(name, encoder.encode(method.invoke(object)));
				} catch (Exception e) {
					final Field field = object.getClass().getField(name);
					parameters.set(name, encoder.encode(field.get(object)));
				}
			}

			return new ObjectRecord().set(JSonName, identifier).set(JSonValue, parameters);
		} catch (DataTransducerException e) {
			throw e;
		} catch (Exception e) {
			throw new DataTransducerException(e);
		}
	}

	public Object toObject(ObjectRecord object, Decoder decoder) throws DataTransducerException {
		try {
			final Class<?> model = Class.forName(typeName);
			final ObjectRecord objectRecord = object.get(JSonValue, ObjectRecord.class);
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