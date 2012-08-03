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

package org.wolfgang.contrail.ecosystem.model2;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.wolfgang.common.message.MessagesProvider;

/**
 * <code>Atom</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "atom")
public class Atom implements Expression, Validation {

	private String value;

	/**
	 * Constructor
	 */
	public Atom() {
		super();
	}

	/**
	 * Return the value of reference
	 * 
	 * @return the reference
	 */
	@XmlValue
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of reference
	 * 
	 * @param value
	 *            the reference to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void validate() throws ValidationException {
		if (value == null || value.trim().length() == 0) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "value.undefined").format());
		}
	}

	@Override
	public <T, E extends Exception> T visit(ExpressionVisitor<T, E> visitor) throws E {
		return visitor.visit(this);
	}
}
