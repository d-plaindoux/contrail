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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Function</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "apply")
public class Apply extends ContentExpressions implements Expression, Validation {
	/**
	 * Constructor
	 */
	public Apply() {
		super();
	}

	@Override
	public void add(Expression expression) {
		if (this.getExpressions().size() == 2) {
			final Apply apply = new Apply();
			apply.add(this.getExpressions().remove(0));
			apply.add(this.getExpressions().remove(0));

			super.add(apply);
			super.add(expression);
		} else {
			super.add(expression);
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
