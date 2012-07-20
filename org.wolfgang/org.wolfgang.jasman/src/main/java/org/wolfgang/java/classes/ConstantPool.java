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

import java.util.Iterator;

/**
 * <code>ConstantPool</code>
 * 
 * @author Didier Plaindoux
 */
public class ConstantPool implements Iterable<Constant> {

	private final Constant[] constants;

	/**
	 * Constructor
	 * 
	 * @param constants
	 *            Identifier constants.
	 */
	public ConstantPool(Constant[] constants) {
		super();
		assert constants != null;
		this.constants = constants;
	}

	/**
	 * Constant pool size
	 * 
	 * @return an integer
	 */
	private int size() {
		return this.constants.length;
	}

	/**
	 * Method used to retrieve a constant using its index. These index are given
	 * by other constant pool entries.
	 * 
	 * @param index
	 *            The index
	 * @return a constant (Never <code>null</code>)
	 */
	public Constant getAt(int index) {
		assert index > 0;
		assert index <= size();
		return this.constants[index - 1];
	}

	@Override
	public Iterator<Constant> iterator() {
		return new Iterator<Constant>() {
			private int current = 1;
			private int bound = size() + 1;

			@Override
			public boolean hasNext() {
				return current < bound;
			}

			@Override
			public Constant next() {
				return getAt(current++);
			}

			@Override
			public void remove() {
				throw new IllegalAccessError();
			}
		};
	}
}
