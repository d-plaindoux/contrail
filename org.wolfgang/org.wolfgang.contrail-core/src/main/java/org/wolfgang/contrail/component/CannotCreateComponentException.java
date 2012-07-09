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

package org.wolfgang.contrail.component;

/**
 * <code>CannotCreateComponentException</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CannotCreateComponentException extends Exception {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = -1069734740818041861L;

	/**
	 * Constructor
	 */
	public CannotCreateComponentException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public CannotCreateComponentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * 
	 * @param arg0
	 */
	public CannotCreateComponentException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * 
	 * @param arg0
	 */
	public CannotCreateComponentException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
