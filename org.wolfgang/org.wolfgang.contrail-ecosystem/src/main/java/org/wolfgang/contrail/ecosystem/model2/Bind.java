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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.wolfgang.common.lang.TypeUtils;
import org.wolfgang.common.message.MessagesProvider;

/**
 * <code>Entry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "binder")
public class Bind extends ContentExpressions implements Validation {

	private String name;
	private String typeIn;
	private String typeOut;

	/**
	 * Constructor
	 */
	public Bind() {
		super();
	}

	/**
	 * Return the value of name
	 * 
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the value of typeIn
	 * 
	 * @return the typeIn
	 */
	@XmlAttribute(name = "typein")
	public String getTypeIn() {
		return typeIn;
	}

	/**
	 * Set the value of typeIn
	 * 
	 * @param typeIn
	 *            the typeIn to set
	 */
	public void setTypeIn(String typeIn) {
		this.typeIn = typeIn;
	}

	/**
	 * Return the value of typeOut
	 * 
	 * @return the typeOut
	 */
	@XmlAttribute(name = "typeout")
	public String getTypeOut() {
		return typeOut;
	}

	/**
	 * Set the value of typeOut
	 * 
	 * @param typeOut
	 *            the typeOut to set
	 */
	public void setTypeOut(String typeOut) {
		this.typeOut = typeOut;
	}

	@Override
	public void validate() throws ValidationException {
		if (this.name == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "name.undefined").format());
		} else if (this.typeIn == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "typein.undefined").format(name));
		} else if (this.typeOut == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "typeout.undefined").format(name));
		} else {
			try {
				TypeUtils.getType(typeIn);
			} catch (ClassNotFoundException e) {
				throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "typein.unknown").format(name, typeIn), e);
			}
			try {
				TypeUtils.getType(typeOut);
			} catch (ClassNotFoundException e) {
				throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "typeout.unknown").format(name, typeOut), e);
			}
		}
	}
}
