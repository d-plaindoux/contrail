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

package org.wolfgang.contrail.ecosystem.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ComponentValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;
import org.wolfgang.contrail.ecosystem.lang.model.Apply;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.ExpressionVisitor;
import org.wolfgang.contrail.ecosystem.lang.model.Function;
import org.wolfgang.contrail.ecosystem.lang.model.Reference;

/**
 * <code>Interpret</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class EcosystemCompiler implements ExpressionVisitor<CodeValue, Exception> {

	private final EcosystemFactoryImpl factory;
	private final Map<String, CodeValue> environment;

	/**
	 * Constructor
	 * 
	 * @param environement
	 */
	EcosystemCompiler(EcosystemFactoryImpl factory, Map<String, CodeValue> environement) {
		super();
		this.factory = factory;
		this.environment = environement;
	}

	public CodeValue visit(final List<Expression> expressions) throws Exception {
		final CodeValue[] values = new CodeValue[expressions.size()];
		final EcosystemCompiler interpret = new EcosystemCompiler(factory, environment);
		for (int i = 0; i < values.length; i++) {
			values[i] = expressions.get(i).visit(interpret);
		}
		if (values.length == 1) {
			return values[0];
		} else {
			return new FlowValue(values);
		}
	}

	@Override
	public CodeValue visit(final Reference expression) throws Exception {
		if (factory.importations.containsKey(expression.getValue())) {
			return new ComponentValue(environment, factory.importations.get(expression.getValue()));
		} else {
			return null; // TODO
		}
	}

	@Override
	public CodeValue visit(Atom expression) throws CannotCreateComponentException {
		return new ConstantValue(expression.getValue());
	}

	@Override
	public CodeValue visit(Apply expression) throws Exception {
		assert expression.getExpressions().size() == 2;

		final Expression argument0 = expression.getExpressions().get(0);
		final Expression argument1 = expression.getExpressions().get(1);

		if (argument0 instanceof Function) {
			final Function function = (Function) argument0;
			final CodeValue result = argument1.visit(this);

			final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
			newEnvironment.putAll(environment);
			newEnvironment.put(function.getParameter(), result);

			final EcosystemCompiler interpret = new EcosystemCompiler(factory, newEnvironment);
			return interpret.visit(function.getExpressions());
		} else {
			throw new Exception("Evalutation Error : TODO : Waiting for a function");
		}
	}

	@Override
	public CodeValue visit(Function expression) throws Exception {
		final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
		newEnvironment.putAll(environment);
		return new ClosureValue(expression, newEnvironment);
	}
}