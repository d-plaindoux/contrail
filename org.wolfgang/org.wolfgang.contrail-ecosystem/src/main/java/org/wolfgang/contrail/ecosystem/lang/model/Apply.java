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

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Function</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "apply")
public class Apply extends ContentExpressions implements Expression, Validation {

	private String binding;

	/**
	 * Constructor
	 */
	public Apply() {
		super();
	}

	/**
	 * Return the value of binding
	 * 
	 * @return the binding
	 */
	@XmlAttribute(name = "bind")
	public String getBinding() {
		return binding;
	}

	/**
	 * Set the value of binding
	 * 
	 * @param binding
	 *            the binding to set
	 */
	public void setBinding(String binding) {
		this.binding = binding;
	}

	/**
	 * @return The functional part of the apply
	 */
	public Expression getFunction() {
		return this.expressions.get(0);
	}

	/**
	 * @return the parameter part of the apply
	 */
	public Expression[] getParameters() {
		final int size = this.expressions.size();
		return Arrays.copyOfRange(this.expressions.toArray(new Expression[size]), 1, size);
	}

	@Override
	public void validate() throws ValidationException {
		if (this.expressions.size() != 2) {
			throw new ValidationException("TODO");
			/** TODO */
		}
	}

	@Override
	public <T, E extends Exception> T visit(ExpressionVisitor<T, E> visitor) throws E {
		return visitor.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((binding == null) ? 0 : binding.hashCode());
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
		Apply other = (Apply) obj;
		if (binding == null) {
			if (other.binding != null)
				return false;
		} else if (!binding.equals(other.binding))
			return false;
		return super.equals(obj);
	}

}
