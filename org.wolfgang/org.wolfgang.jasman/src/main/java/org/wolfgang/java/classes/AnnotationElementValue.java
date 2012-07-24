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

package org.wolfgang.java.classes;

/**
 * <code>AnnotationElementValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface AnnotationElementValue {

	String toExternal();

	/**
	 * <code>ObjectValue</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class ObjectValue implements AnnotationElementValue {

		private final ConstantPool constantPool;
		private final int value;

		/**
		 * Constructor
		 * 
		 * @param constantPool
		 * @param value
		 */
		ObjectValue(ConstantPool constantPool, int value) {
			super();
			this.constantPool = constantPool;
			this.value = value;
		}

		/**
		 * Return the value of value
		 * 
		 * @return the value
		 */
		public String getValue() {
			return this.constantPool.getAt(value).toExternal();
		}

		@Override
		public String toExternal() {
			return getValue();
		}
	}

	/**
	 * <code>ClassValue</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class EnumValue implements AnnotationElementValue {

		private final ConstantPool constantPool;
		private final int type;
		private final int value;

		/**
		 * Constructor
		 * 
		 * @param constantPool
		 * @param type
		 */
		EnumValue(ConstantPool constantPool, int type, int value) {
			super();
			this.constantPool = constantPool;
			this.type = type;
			this.value = value;
		}

		/**
		 * Return the value of type
		 * 
		 * @return the value
		 */
		public String getType() {
			return TypeDecoder.getType(this.constantPool.getAt(type).toExternal());
		}

		/**
		 * Return the value of value
		 * 
		 * @return the value
		 */
		public String getValue() {
			return this.constantPool.getAt(value).toExternal();
		}

		@Override
		public String toExternal() {
			return getType() + "." + getValue();
		}

	}

	/**
	 * <code>ClassValue</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class ClassValue implements AnnotationElementValue {

		private final ConstantPool constantPool;
		private final int value;

		/**
		 * Constructor
		 * 
		 * @param constantPool
		 * @param value
		 */
		ClassValue(ConstantPool constantPool, int value) {
			super();
			this.constantPool = constantPool;
			this.value = value;
		}

		/**
		 * Return the value ofvalue
		 * 
		 * @return the value
		 */
		public String getValue() {
			return TypeDecoder.getType(this.constantPool.getAt(value).toExternal());
		}

		@Override
		public String toExternal() {
			return getValue();
		}
	}

	/**
	 * <code>ClassValue</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class AnnotationValue implements AnnotationElementValue {

		private final Annotation annotation;

		/**
		 * Constructor
		 * 
		 * @param constantPool
		 * @param value
		 */
		AnnotationValue(Annotation value) {
			super();
			this.annotation = value;
		}

		/**
		 * Return the value of value
		 * 
		 * @return the value
		 */
		public Annotation getValue() {
			return annotation;
		}

		@Override
		public String toExternal() {
			return annotation.toExternal();
		}
	}

	/**
	 * <code>ClassValue</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class ArrayValue implements AnnotationElementValue {
		private final AnnotationElementValue[] values;

		/**
		 * Constructor
		 * 
		 * @param annotation
		 */
		ArrayValue(AnnotationElementValue[] values) {
			super();
			this.values = values;
		}

		/**
		 * Return the value ofvalue
		 * 
		 * @return the value
		 */
		public AnnotationElementValue[] getValues() {
			return values;
		}

		@Override
		public String toExternal() {
			final StringBuilder builder = new StringBuilder();

			builder.append('{');
			for (int i = 0; i < values.length; i++) {
				if (i > 0) {
					builder.append(',');
				}
				builder.append(values[i].toExternal());
			}
			builder.append('}');

			return builder.toString();
		}
	}
}
