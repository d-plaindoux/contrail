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
import javax.xml.bind.annotation.XmlElements;

/**
 * <code>ContentExpressions</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public abstract class ContentExpressions implements Validation {

	protected List<Expression> expressions;

	{
		this.expressions = new ArrayList<Expression>();
	}

	/**
	 * Constructor
	 */
	ContentExpressions() {
		super();
	}

	/**
	 * Return the value of expression
	 * 
	 * @return the expression
	 */
	@XmlElements(value = { @XmlElement(type = Function.class, name = "function"), @XmlElement(type = Flow.class, name = "flow"), @XmlElement(type = Apply.class, name = "apply"),
			@XmlElement(type = Reference.class, name = "ref"), @XmlElement(type = Router.class, name = "router"), @XmlElement(type = Switch.class, name = "switch"),
			@XmlElement(type = Atom.class, name = "atom") })
	public List<Expression> getExpressions() {
		return expressions;
	}

	/**
	 * Set the value of expression
	 * 
	 * @param expression
	 *            the expression to set
	 */
	public void add(Expression expression) {
		this.expressions.add(expression);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expressions == null) ? 0 : expressions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContentExpressions other = (ContentExpressions) obj;
		if (expressions == null) {
			if (other.expressions != null)
				return false;
		} else if (!expressions.equals(other.expressions))
			return false;
		return true;
	}
}
