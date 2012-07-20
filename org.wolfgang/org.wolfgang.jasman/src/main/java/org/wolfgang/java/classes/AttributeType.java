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
 * <code>AttributeType</code> is used to qualify items found in a class file
 * 
 * @author Didier Plaindoux
 */
public enum AttributeType {

	/**
	 * Signature type
	 */
	SIGNATURE,

	/**
	 * Source type
	 */
	SOURCEFILE,

	/**
	 * Code type
	 */
	CODE,

	/**
	 * Anything else
	 */
	GENERIC;

	/**
	 * Method used for internalization
	 * 
	 * @param name The attribute as a string
	 * @return an attribute type (Never <code>null</code>)
	 */
	public static AttributeType get(String name) {
		if (name.equals("Signature")) {
			return SIGNATURE;
		} else if (name.equals("SourceFile")) {
			return SOURCEFILE;
		} else if (name.equals("Code")) {
			return CODE;
		} else {
			return GENERIC;
		}
	}

}
