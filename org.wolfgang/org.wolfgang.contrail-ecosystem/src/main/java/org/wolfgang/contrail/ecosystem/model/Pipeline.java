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

package org.wolfgang.contrail.ecosystem.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Transducer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement
public class Pipeline implements Validation {

	private String name;
	private String factory;
	private List<String> parameters;

	{
		this.parameters = new ArrayList<String>();
	}

	/**
	 * Constructor
	 */
	public Pipeline() {
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
	 * Return the value of factory
	 * 
	 * @return the factory
	 */
	@XmlAttribute
	public String getFactory() {
		return factory;
	}

	/**
	 * Set the value of factory
	 * 
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(String factory) {
		this.factory = factory;
	}

	/**
	 * Return the value ofparameters
	 * 
	 * @return the parameters
	 */
	@XmlElement(name = "param")
	public List<String> getParameters() {
		return parameters;
	}

	/**
	 * Set the value of parameters
	 * 
	 * @param parameters
	 *            the parameters to set
	 */
	public void add(String parameter) {
		this.parameters.add(parameter);
	}

	@Override
	public void validate() throws ValidationException {
		// TODO Auto-generated method stub
		
	}
}
