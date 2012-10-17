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

import java.io.IOException;

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
	 * @param visitor
	 * @return
	 */
	<E> E visit(ClassAttributeVisitor<E> visitor);

	/**
	 * <code>AbstractClassAttribute</code>
	 * 
	 * @author Didier Plaindoux
	 */
	abstract class Abstract implements ClassAttribute {

		private final int name;
		protected final ConstantPool pool;

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
	class Generic extends Abstract {

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

		@Override
		public <E> E visit(ClassAttributeVisitor<E> visitor) {
			return visitor.visit(this);
		}
	}

	/**
	 * <code>Code</code>
	 * 
	 * @author Didier Plaindoux
	 */
	class Code extends Abstract {

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
			return "Undecoded <" + getName() + ">[(" + value.length + ") ...]";
		}

		@Override
		public <E> E visit(ClassAttributeVisitor<E> visitor) {
			return visitor.visit(this);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * <code>Signature</code>
	 * 
	 * @author Didier Plaindoux
	 */
	class Signature extends Abstract {

		private final int signature;

		public Signature(ConstantPool pool, int name, int signature) {
			super(pool, name);
			this.signature = signature;
		}

		@Override
		public String toExternal() {
			try {
				return getName() + " [" + pool.getAt(signature).toExternal() + "]";
			} catch (ArrayIndexOutOfBoundsException e) {
				return "<unlink> Signature [" + signature + "]";
			}
		}

		public String getSignature() {
			return pool.getAt(signature).toExternal();
		}

		@Override
		public <E> E visit(ClassAttributeVisitor<E> visitor) {
			return visitor.visit(this);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * <code>SourceFile</code>
	 * 
	 * @author Didier Plaindoux
	 */
	class SourceFile extends Abstract {
		private final int sourcefile;

		public SourceFile(ConstantPool pool, int name, int sourceFile) {
			super(pool, name);
			this.sourcefile = sourceFile;
		}

		@Override
		public String toExternal() {
			try {
				return getName() + " [" + pool.getAt(sourcefile).toExternal() + "]";
			} catch (ArrayIndexOutOfBoundsException e) {
				return "<unlink> SourceFile [" + sourcefile + "]";
			}

		}

		@Override
		public <E> E visit(ClassAttributeVisitor<E> visitor) {
			return visitor.visit(this);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * <code>SourceFile</code>
	 * 
	 * @author Didier Plaindoux
	 */
	class VisibleAnnotations extends Abstract {

		private final Annotation[] annotations;

		public VisibleAnnotations(ConstantPool pool, int name, Annotation[] annotations) throws IOException {
			super(pool, name);
			this.annotations = annotations;
		}

		/**
		 * Return the value of annotations
		 * 
		 * @return the annotations
		 */
		public Annotation[] getAnnotations() {
			return annotations;
		}

		/**
		 * Searh for an annotation using the class name
		 * 
		 * @param className
		 *            The searched class name
		 * @return an annotation or <code>null</code>
		 */
		public Annotation searchByType(String className) {
			for (Annotation annotation : annotations) {
				if (annotation.getName().equals(className)) {
					return annotation;
				}
			}

			return null;
		}

		@Override
		public String toExternal() {
			try {
				final StringBuilder builder = new StringBuilder();
				builder.append(getName()).append(" [ ");
				for (Annotation annotation : annotations) {
					builder.append(annotation.toExternal()).append(' ');
				}
				builder.append("]");
				return builder.toString();
			} catch (ArrayIndexOutOfBoundsException e) {
				return "<unlink> RuntimeVisibleAnnotations [...]";
			}

		}

		@Override
		public <E> E visit(ClassAttributeVisitor<E> visitor) {
			return visitor.visit(this);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * <code>SourceFile</code>
	 * 
	 * @author Didier Plaindoux
	 */
	class VisibleParametersAnnotations extends Abstract {

		private final Annotation[][] parameteresAnnotations;

		public VisibleParametersAnnotations(ConstantPool pool, int name, Annotation[][] parameteresAnnotations) {
			super(pool, name);
			this.parameteresAnnotations = parameteresAnnotations;
		}

		/**
		 * Return the value o fannotations
		 * 
		 * @return the annotations
		 */
		public Annotation[][] getParametersAnnotations() {
			return parameteresAnnotations;
		}

		@Override
		public String toExternal() {
			try {
				final StringBuilder builder = new StringBuilder();
				builder.append(getName()).append(" [");
				for (Annotation[] annotations : parameteresAnnotations) {
					builder.append("[ ");
					for (Annotation annotation : annotations) {
						builder.append(annotation.toExternal()).append(' ');
					}
					builder.append("]");
				}
				builder.append("]");
				return builder.toString();
			} catch (ArrayIndexOutOfBoundsException e) {
				return "<unlink> RuntimeVisibleAnnotations [...]";
			}

		}

		@Override
		public <E> E visit(ClassAttributeVisitor<E> visitor) {
			return visitor.visit(this);
		}

		/**
		 * @param i
		 * @param canonicalName
		 * @return
		 */
		public Annotation searchByType(int i, String className) {
			for (Annotation annotation : parameteresAnnotations[i]) {
				if (annotation.getName().equals(className)) {
					return annotation;
				}
			}

			return null;
		}
	}
}
