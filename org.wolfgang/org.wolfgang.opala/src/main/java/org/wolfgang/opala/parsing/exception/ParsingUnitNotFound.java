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

package org.wolfgang.opala.parsing.exception;

import org.wolfgang.opala.lexing.ILocation;

public class ParsingUnitNotFound extends Exception {
	private static final long serialVersionUID = -421616441655825652L;

	private ILocation location;

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public ParsingUnitNotFound(String message) {
		super(message);
	}

	/**
	 * Constructor
	 */
	public ParsingUnitNotFound() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ParsingUnitNotFound(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 * 
	 * @param arg0
	 */
	public ParsingUnitNotFound(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ILocation getLocation() {
		return location;
	}

	public ParsingUnitNotFound setLocation(ILocation location) {
		this.location = location;
		return this;
	}
}
