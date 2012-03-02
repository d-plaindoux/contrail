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

package org.wolfgang.common.utils;

/**
 * <code>Option</code> is an implementation of a typical functional data type
 * providing result which can provide an optional value
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public abstract class Option<E> {

	/**
	 * <code>Kind</code> defines the kind of the option
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public enum Kind {
		/**
		 * An option with a value
		 */
		Some,
		/**
		 * An option without any value
		 */
		None
	}

	/**
	 * Main method used to create an option with a given value
	 * 
	 * @param value
	 *            The value
	 * @return an option
	 */
	public static <E> Option<E> some(E value) {
		return new SomeClass<E>(value);
	}

	/**
	 * Main method used to create an option with a given value
	 * 
	 * @param value
	 *            The value
	 * @return an option
	 */
	public static <E> Option<E> none() {
		return new NoneClass<E>();
	}

	/**
	 * @return the option kind
	 */
	public abstract Kind getKind();

	/**
	 * @return the value if defined
	 * @see Option#getKind()
	 */
	public abstract E getValue();

	/**
	 * <code>SomeClass</code> used for any option with a given value
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 * @param <E>
	 *            The value type
	 */
	private static class SomeClass<E> extends Option<E> {
		/**
		 * The embedded value
		 */
		private final E value;

		/**
		 * @param e
		 */
		public SomeClass(E e) {
			super();
			this.value = e;
		}

		@Override
		public Option.Kind getKind() {
			return Kind.Some;
		}

		@Override
		public E getValue() {
			return this.value;
		}
	}

	/**
	 * <code>NoneClass</code> used for empty option
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 * @param <E>
	 */
	private static class NoneClass<E> extends Option<E> {

		@Override
		public Option.Kind getKind() {
			return Kind.None;
		}

		@Override
		public E getValue() {
			throw new IllegalAccessError();
		}

	}
}
