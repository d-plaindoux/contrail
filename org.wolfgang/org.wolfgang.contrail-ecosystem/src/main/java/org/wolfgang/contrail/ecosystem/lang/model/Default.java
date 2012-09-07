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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Case</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "default")
public class Default implements Validation {

	private Function caseBody;

	/**
	 * Constructor
	 */
	public Default() {
		super();
	}

	/**
	 * Return the value of body
	 * 
	 * @return the body
	 */
	@XmlElement(name = "function")
	public Function getBody() {
		return caseBody;
	}

	/**
	 * Set the value of body
	 * 
	 * @param body
	 *            the body to set
	 */
	public void setBody(Function body) {
		this.caseBody = body;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO
	}
}
