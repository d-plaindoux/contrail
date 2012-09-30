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

/**
 * <code>ConstantValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ConstantValue implements CodeValue {
	private final Object value;

	/**
	 * Constructor
	 * 
	 * @param value
	 */
	public ConstantValue(Object value) {
		super();
		this.value = value;
	}

	/**
	 * Return the value of value
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public <T, E extends Exception> T visit(CodeValueVisitor<T, E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "\"" + value + "\"";
	}
}