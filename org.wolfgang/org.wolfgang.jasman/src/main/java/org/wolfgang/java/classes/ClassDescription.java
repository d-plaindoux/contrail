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

import java.io.PrintStream;

/**
 * <code>ClassDescription</code> is the main component describing a class.
 * 
 * @author Didier Plaindoux
 */
public interface ClassDescription {

	/**
	 * Method providing the access flag. Such flags can be interpreted using the
	 * {@link java.lang.reflect.Modifier} class.
	 * 
	 * @return the access flags
	 */
	int getAccessFlags();

	/**
	 * @return the major version
	 */
	int getMajorVersion();

	/**
	 * @return the minor version
	 */
	int getMinorVersion();

	/**
	 * @return the constant pool
	 */
	ConstantPool getConstantPool();

	/**
	 * @return the class name
	 */
	String getClassName();

	/**
	 * @return the superclass name
	 */
	String getSuperClassName();

	/**
	 * @return the interfaces name
	 */
	String[] getInterfacesName();

	/**
	 * @return the declared types
	 */
	String[] getDeclaredTypes();

	/**
	 * @param name
	 * @return
	 * @throws DefinitionNotFound
	 */
	ClassField findFieldByName(String name) throws DefinitionNotFound;

	/**
	 * @return
	 */
	ClassField[] getFields();

	/**
	 * @param name
	 * @return
	 * @throws DefinitionNotFound
	 */
	ClassMethod findMethodByName(String name) throws DefinitionNotFound;

	/**
	 * @return the methods
	 */
	ClassMethod[] getMethods();

	/**
	 * @return the attributes
	 */
	ClassAttribute[] getAttributes();

	// -----------------------------------------------------------------------------------------------------------------
	// Utilities corner
	// -----------------------------------------------------------------------------------------------------------------

	long getDecodingTime();

	void dump(PrintStream printStream);
}