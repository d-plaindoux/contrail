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

package org.wolfgang.contrail.ecosystem.lang.delta.converter;

/**
 * <code>ConversionException</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ConversionException extends Exception {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = 928837609343463110L;

	/**
	 * Constructor
	 * 
	 * @param arg0
	 * @param arg1
	 */
	ConversionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor
	 * 
	 * @param arg0
	 */
	public ConversionException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor
	 * 
	 * @param arg0
	 */
	public ConversionException(Throwable arg0) {
		super(arg0);
	}
}
