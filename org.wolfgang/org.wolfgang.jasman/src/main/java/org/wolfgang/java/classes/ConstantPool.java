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
 * <code>ConstantPool</code>
 * 
 * @author Didier Plaindoux
 */
public interface ConstantPool extends Iterable<Constant> {

	/**
	 * Method used to retrieve a constant using its index. These index are given
	 * by other constant pool entries.
	 * 
	 * @param index
	 *            The index
	 * @return a constant (Never <code>null</code>)
	 */
	Constant getAt(int index);

}
