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

/**
 * <code>Expression</code> is the main type used for the Ecosystem AST
 * representation.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface Expression {

	/**
	 * Method called whether an expression is visited
	 * 
	 * @param visitor
	 *            The visitor
	 * @return a value
	 * @throws E
	 *             if any problem occurs
	 */
	<T, E extends Exception> T visit(ExpressionVisitor<T, E> visitor) throws E;

}
