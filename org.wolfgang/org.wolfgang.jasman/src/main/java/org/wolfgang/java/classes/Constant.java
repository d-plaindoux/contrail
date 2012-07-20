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
 * <code>Constant</code> provides all available constants in a binary java
 * class. It can be an UTF8, an UNUSED (internal only), an INTEGER etc.
 * 
 * @author Didier Plaindoux
 */
public interface Constant {

	/**
	 * Method providing an external representation to be used only as an
	 * informal information. This representation is not linked to any official
	 * representation.
	 * 
	 * @return a string
	 */
	String toExternal();

	/**
	 * <code>UTF8</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class UTF8 implements Constant {
		private final String value;

		/**
		 * Constructor
		 * 
		 * @param chars
		 */
		public UTF8(char[] chars) {
			super();
			this.value = String.valueOf(chars).intern();
		}

		/**
		 * @return the value
		 */
		public char[] getValue() {
			return value.toCharArray();
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public String toExternal() {
			return toString();
		}
	}

	/**
	 * <code>UNUSED</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class UNUSED implements Constant {

		@Override
		public String toExternal() {
			return "UNUSED";
		}
	}

	/**
	 * <code>INTEGER</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class INTEGER implements Constant {
		private final int value;

		/**
		 * Constructor
		 * 
		 * @param value
		 */
		public INTEGER(int value) {
			super();
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "INTEGER [" + value + "]";
		}

		@Override
		public String toExternal() {
			return String.valueOf(value);
		}
	}

	/**
	 * <code>LONG</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */

	public class LONG implements Constant {
		private final long value;

		/**
		 * Constructor
		 * 
		 * @param value
		 */
		public LONG(long value) {
			super();
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public long getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "LONG [" + value + "]";
		}

		@Override
		public String toExternal() {
			return String.valueOf(value);
		}
	}

	/**
	 * <code>FLOAT</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class FLOAT implements Constant {
		private final float value;

		/**
		 * Constructor
		 * 
		 * @param value
		 */
		public FLOAT(float value) {
			super();
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public float getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "FLOAT [" + value + "]";
		}

		@Override
		public String toExternal() {
			return String.valueOf(value);
		}
	}

	/**
	 * <code>DOUBLE</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class DOUBLE implements Constant {
		private final double value;

		/**
		 * Constructor
		 * 
		 * @param value
		 */
		public DOUBLE(double value) {
			super();
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "DOUBLE [" + value + "]";
		}

		@Override
		public String toExternal() {
			return String.valueOf(value);
		}
	}

	/**
	 * <code>STRING</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class STRING implements Constant {
		private final int entry;
		private final ConstantPool pool;

		/**
		 * Constructor
		 * 
		 * @param pool
		 * @param entry
		 */
		public STRING(ConstantPool pool, int entry) {
			super();
			this.pool = pool;
			this.entry = entry;
		}

		/**
		 * @return the value
		 */
		public UTF8 getValue() {
			return (UTF8) pool.getAt(entry);
		}

		@Override
		public String toString() {
			return "STRING [" + entry + "]";
		}

		@Override
		public String toExternal() {
			return getValue().toExternal();
		}
	}

	/**
	 * <code>CLASS</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class CLASS implements Constant {
		private final int name;
		private final ConstantPool pool;

		/**
		 * Constructor
		 * 
		 * @param pool
		 * @param name
		 */
		public CLASS(ConstantPool pool, int name) {
			super();
			this.pool = pool;
			this.name = name;
		}

		/**
		 * @return the value
		 */
		public UTF8 getName() {
			return (UTF8) pool.getAt(name);
		}

		@Override
		public String toString() {
			return "CLASS [" + name + "]";
		}

		@Override
		public String toExternal() {
			return getName().toExternal();
		}
	}

	/**
	 * <code>NAME_AND_TYPE</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class NAME_AND_TYPE implements Constant {
		private final int name;
		private final int type;
		private final ConstantPool pool;

		/**
		 * Constructor
		 * 
		 * @param pool
		 * @param name
		 * @param type
		 */
		public NAME_AND_TYPE(ConstantPool pool, int name, int type) {
			super();
			this.pool = pool;
			this.name = name;
			this.type = type;
		}

		/**
		 * @return the name
		 */
		public UTF8 getName() {
			return (UTF8) pool.getAt(name);
		}

		/**
		 * @return the type
		 */
		public UTF8 getType() {
			return (UTF8) pool.getAt(type);
		}

		@Override
		public String toString() {
			return "NAME_AND_TYPE [name=" + name + ", type=" + type + "]";
		}

		@Override
		public String toExternal() {
			return "NAME_AND_TYPE [name=" + getName().toExternal() + ", type=" + getType().toExternal() + "]";
		}
	}

	/**
	 * <code>Abstract_REF</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	class Abstract_REF implements Constant {
		private final int classReference;
		private final int nameAndType;
		private final ConstantPool pool;

		/**
		 * Constructor
		 * 
		 * @param pool
		 * @param classReference
		 * @param nameAndType
		 */
		Abstract_REF(ConstantPool pool, int classReference, int nameAndType) {
			super();
			this.pool = pool;
			this.classReference = classReference;
			this.nameAndType = nameAndType;
		}

		/**
		 * @return the classReference
		 */
		public CLASS getClassReference() {
			return (CLASS) pool.getAt(classReference);
		}

		/**
		 * @return the nameAndType
		 */
		public NAME_AND_TYPE getNameAndType() {
			return (NAME_AND_TYPE) pool.getAt(nameAndType);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + " [classReference=" + classReference + ", nameAndType=" + nameAndType + "]";
		}

		@Override
		public String toExternal() {
			return this.getClass().getSimpleName() + " [classReference=" + getClassReference().toExternal() + ", nameAndType=" + getNameAndType().toExternal() + "]";
		}
	}

	/**
	 * <code>FIELD_REF</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class FIELD_REF extends Abstract_REF {

		/**
		 * Constructor
		 * 
		 * @param pool
		 * @param classReference
		 * @param nameAndType
		 */
		public FIELD_REF(ConstantPool pool, int classReference, int nameAndType) {
			super(pool, classReference, nameAndType);
		}
	}

	/**
	 * <code>METHOD_REF</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class METHOD_REF extends Abstract_REF {

		/**
		 * Constructor
		 * 
		 * @param pool
		 * @param classReference
		 * @param nameAndType
		 */
		public METHOD_REF(ConstantPool pool, int classReference, int nameAndType) {
			super(pool, classReference, nameAndType);
		}
	}

	/**
	 * <code>INTERFACE_METHOD_REF</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class INTERFACE_METHOD_REF extends Abstract_REF {

		/**
		 * Constructor
		 * 
		 * @param pool
		 * @param classReference
		 * @param nameAndType
		 */
		public INTERFACE_METHOD_REF(ConstantPool pool, int classReference, int nameAndType) {
			super(pool, classReference, nameAndType);
		}
	}
}
