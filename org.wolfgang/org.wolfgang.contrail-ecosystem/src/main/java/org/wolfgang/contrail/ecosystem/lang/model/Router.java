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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Router</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "router")
public class Router implements Expression, Validation {

	private String selfReference;
	private List<Case> cases;
	private Default defaultCase;

	{
		this.cases = new ArrayList<Case>();
	}

	/**
	 * Constructor
	 */
	public Router() {
		super();
	}

	/**
	 * Return the value of selfReference
	 * 
	 * @return the selfReference
	 */
	@XmlElement(name = "self")
	public String getSelf() {
		return selfReference;
	}

	/**
	 * Set the value of selfReference
	 * 
	 * @param selfReference
	 *            the selfReference to set
	 */
	public void setSelf(String selfReference) {
		this.selfReference = selfReference;
	}

	/**
	 * Return the value of cases
	 * 
	 * @return the cases
	 */
	@XmlElement(name = "case")
	public List<Case> getCases() {
		return cases;
	}

	/**
	 * Set the value of cases
	 * 
	 * @param cases
	 *            the cases to set
	 */
	public void add(Case aCase) {
		this.cases.add(aCase);
	}

	/**
	 * Return the value of defaultCase
	 * 
	 * @return the defaultCase
	 */
	@XmlElement(name = "default")
	public Default getDefaultCase() {
		return defaultCase;
	}

	/**
	 * Set the value of defaultCase
	 * 
	 * @param defaultCase
	 *            the defaultCase to set
	 */
	public void setDefaultCase(Default defaultCase) {
		this.defaultCase = defaultCase;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO
	}

	@Override
	public <T, E extends Exception> T visit(ExpressionVisitor<T, E> visitor) throws E {
		return null;
	}
}
