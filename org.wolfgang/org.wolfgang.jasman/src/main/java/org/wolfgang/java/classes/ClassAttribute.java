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
 * <code>ClassAttribute</code>
 * 
 * @author Didier Plaindoux
 */
public interface ClassAttribute {

	/**
	 * @return
	 */
	String getName();

	/**
	 * @return
	 */
	String toExternal();

	/**
	 * <code>AbstractClassAttribute</code>
	 * 
	 * @author Didier Plaindoux
	 */
	static abstract class Abstract implements ClassAttribute {

		protected final ConstantPool pool;
		private final int name;

		protected Abstract(ConstantPool pool, int name) {
			super();
			this.pool = pool;
			this.name = name;
		}

		public String getName() {
			return pool.getAt(name).toExternal();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * <code>Generic</code>
	 * 
	 * @author Didier Plaindoux
	 */
	public static class Generic extends Abstract {

		private final byte[] value;

		public Generic(ConstantPool pool, int name, byte[] value) {
			super(pool, name);
			this.value = value;
		}

		public byte[] getValue() {
			return value;
		}

		@Override
		public String toExternal() {
			return "Attribute <" + getName() + ">[(" + value.length + ") ...]";
		}
	}

	/**
	 * <code>Code</code>
	 * 
	 * @author Didier Plaindoux
	 */
	public static class Code extends Abstract {

		private final byte[] value;

		public Code(ConstantPool pool, int name, byte[] value) {
			super(pool, name);
			this.value = value;
		}

		public byte[] getValue() {
			return value;
		}

		@Override
		public String toExternal() {
			return "Attribute <" + getName() + ">[(" + value.length + ") ...]";
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * <code>Signature</code>
	 * 
	 * @author Didier Plaindoux
	 */
	public static class Signature extends Abstract {

		private final int signature;

		public Signature(ConstantPool pool, int name, byte[] value) {
			super(pool, name);
			this.signature = ((value[0] & 0xFF) << 8) | (value[1] & 0xFF);
		}

		@Override
		public String toExternal() {
			try {
				return "Signature [" + pool.getAt(signature).toExternal() + "]";
			} catch (ArrayIndexOutOfBoundsException e) {
				return "<unlink> Signature [" + signature + "]";
			}
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * <code>SourceFile</code>
	 * 
	 * @author Didier Plaindoux
	 */
	public static class SourceFile extends Abstract {
		private final int sourcefile;

		public SourceFile(ConstantPool pool, int name, byte[] value) {
			super(pool, name);
			this.sourcefile = ((value[0] & 0xFF) << 8) | (value[1] & 0xFF);
		}

		@Override
		public String toExternal() {
			try {
				return "SourceFile [" + pool.getAt(sourcefile).toExternal() + "]";
			} catch (ArrayIndexOutOfBoundsException e) {
				return "<unlink> SourceFile [" + sourcefile + "]";
			}

		}
	}
}
