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

import org.wolfgang.common.utils.Coercion;

/**
 * <code>ModelFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ModelFactory {

	private ModelFactory() {
		super();
	}

	public static Expression apply(Expression function, Expression... expressions) {
		if (expressions.length == 0) {
			return function;
		} else {
			final Apply apply = new Apply();
			apply.add(function);
			for (Expression expression : expressions) {
				apply.add(expression);
			}
			return apply;
		}
	}

	public static Atom unit() {
		return new Atom();
	}

	public static Atom atom(String value) {
		final Atom expression = new Atom();
		expression.setValue(value);
		return expression;
	}

	public static Expression flow(Expression... expressions) {
		if (expressions.length == 0) {
			return atom(null);
		} else if (expressions.length == 1) {
			return expressions[0];
		} else {
			final Flow expression = new Flow();
			for (Expression expression2 : expressions) {
				expression.add(expression2);
			}
			return expression;
		}
	}

	public static Expression sequence(Expression... expressions) {
		if (expressions.length == 0) {
			return atom(null);
		} else if (expressions.length == 1) {
			return expressions[0];
		} else {
			final Sequence expression = new Sequence();
			for (Expression expression2 : expressions) {
				expression.add(expression2);
			}
			return expression;
		}
	}

	public static Function function(Expression body, String... parameters) {
		assert parameters.length > 0;
		final Function expression = new Function();
		if (Coercion.canCoerce(body, Flow.class)) {
			final Flow flow = Coercion.coerce(body, Flow.class);
			for (Expression subExpression : flow.getExpressions()) {
				expression.add(subExpression);
			}
		} else {
			expression.add(body);
		}
		for (String parameter : parameters) {
			expression.add(parameter);
		}
		return expression;
	}

	public static Reference reference(String value) {
		final Reference expression = new Reference();
		expression.setValue(value);
		return expression;
	}

	public static Definition define(String name, Expression value) {
		final Definition definition = new Definition();
		definition.setName(name);
		definition.add(value);
		return definition;
	}
}
