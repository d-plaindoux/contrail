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
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Function</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "function")
public class Function extends ContentExpressions implements Expression, Validation {

	/**
	 * Parameters
	 */
	private List<String> parameters;

	{
		this.parameters = new ArrayList<String>();
	}

	/**
	 * Constructor
	 */
	public Function() {
		super();
	}

	/**
	 * Return the value of parameters
	 * 
	 * @return the parameters
	 */
	@XmlElement(name = "var")
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
		if (parameter == null) {
			// Generate name "a la Perl" :)
			this.parameters.add("_" + this.parameters.size());
		} else {
			this.parameters.add(parameter.trim());
		}
	}

	/**
	 * Return the value of parameters
	 * 
	 * @return the parameters
	 */
	public String[] getParameters(String[] names, int arity) {
		if (names == null) {
			final String[] result = new String[arity];
			for (int i = 0; i < arity; i++) {
				result[i] = this.parameters.get(i);
			}
			return result;
		} else {
			for (String name : names) {
				if (!this.parameters.contains(name)) {
					throw new RuntimeException("Parameter " + name + " not found -- TODO");
				}
			}
			return names;
		}
	}

	/**
	 * Method called whether a parameter has been applied
	 * 
	 * @param name
	 *            The applied parameter
	 * @return the expression list
	 */
	public List<Expression> apply(String[] names) {
		final List<String> remaining = new ArrayList<String>();
		remaining.addAll(this.parameters);
		for (String name : names) {
			remaining.remove(name);
		}
		if (remaining.size() == 0) {
			return this.expressions;
		} else {
			final Function result = new Function();
			result.parameters = remaining;
			result.expressions = this.expressions;
			return Arrays.<Expression> asList(result);
		}
	}

	@Override
	public void validate() throws ValidationException {
		// TODO Auto-generated method stub
	}

	@Override
	public <T, E extends Exception> T visit(ExpressionVisitor<T, E> visitor) throws E {
		return visitor.visit(this);
	}
}
