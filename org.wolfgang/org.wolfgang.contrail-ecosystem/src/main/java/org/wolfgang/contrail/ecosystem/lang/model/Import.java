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

package org.wolfgang.contrail.ecosystem.lang.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.wolfgang.common.message.MessagesProvider;

/**
 * <code>Import</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "import")
public class Import implements Validation {

	private String alias;
	private String element;

	/**
	 * Constructor
	 */
	public Import() {
		super();
	}

	/**
	 * Return the value of alias
	 * 
	 * @return the alias
	 */
	@XmlAttribute(name = "as")
	public String getAlias() {
		return alias;
	}

	/**
	 * Set the value of alias
	 * 
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Return the value of element
	 * 
	 * @return the element
	 */
	@XmlValue
	public String getElement() {
		return element;
	}

	/**
	 * Set the value of element
	 * 
	 * @param element
	 *            the element to set
	 */
	public void setElement(String element) {
		this.element = element;
	}

	@Override
	public void validate() throws ValidationException {
		if (element == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "endpoint.server.undefined").format());
		}
	}
}
