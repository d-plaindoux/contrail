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

package org.wolfgang.contrail.ecosystem.lang.code;

import java.util.Map;

import org.wolfgang.contrail.ecosystem.lang.model.Function;

/**
 * <code>ClosureValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClosureValue implements CodeValue {
	private final Map<String, CodeValue> environment;
	private final Function function;

	/**
	 * Constructor
	 * 
	 * @param function
	 * @param environement
	 */
	public ClosureValue(Function function, Map<String, CodeValue> environement) {
		super();
		this.function = function;
		this.environment = environement;
	}

	/**
	 * Return the value of environment
	 * 
	 * @return the environment
	 */
	public Map<String, CodeValue> getEnvironment() {
		return environment;
	}

	/**
	 * Return the value of function
	 * 
	 * @return the function
	 */
	public Function getFunction() {
		return function;
	}
}