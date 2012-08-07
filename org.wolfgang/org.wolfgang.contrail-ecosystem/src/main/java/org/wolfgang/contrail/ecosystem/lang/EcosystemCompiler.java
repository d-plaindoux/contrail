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

	private final EcosystemSymbolTable symbolTable;
	private final Map<String, CodeValue> environment;

	/**
	 * Constructor
	 * 
	 * @param environment
	 */
	EcosystemCompiler(EcosystemSymbolTable factory, Map<String, CodeValue> environment) {
		super();
		this.symbolTable = factory;
		this.environment = environment;
	}

	public CodeValue visit(final List<Expression> expressions) throws Exception {
		final CodeValue[] values = new CodeValue[expressions.size()];
		final EcosystemCompiler interpret = new EcosystemCompiler(symbolTable, environment);
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
		if (symbolTable.hasImportation(expression.getValue())) {
			return new ComponentValue(environment, symbolTable.getImportation(expression.getValue()));
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
		final Expression argument0 = expression.getFunction();
		final Expression argument1 = expression.getParameter();

		if (argument0 instanceof Function) {
			final Function function = (Function) argument0;
			final CodeValue result = argument1.visit(this);

			final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
			newEnvironment.putAll(environment);
			final String parameterName = function.getParameter(expression.getBinding());
			newEnvironment.put(parameterName, result);

			final EcosystemCompiler interpret = new EcosystemCompiler(symbolTable, newEnvironment);
			return interpret.visit(function.apply(parameterName));
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