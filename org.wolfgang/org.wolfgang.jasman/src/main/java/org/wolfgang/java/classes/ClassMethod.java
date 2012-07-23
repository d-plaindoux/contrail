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
 * <code>ClassMethod</code>
 * 
 * @author Didier Plaindoux
 */
public interface ClassMethod {

	/**
	 * Method providing the access flag. Such flags can be interpreted using the
	 * {@link java.lang.reflect.Modifier} class.
	 * 
	 * @return the access flags
	 */
	int getAccessFlag();

	/**
	 * @return the method name
	 */
	String getName();

	String getDescription();

	ClassAttribute[] getAttributes();

	ConstantPool getConstantPool();

	String toExternal();
}
