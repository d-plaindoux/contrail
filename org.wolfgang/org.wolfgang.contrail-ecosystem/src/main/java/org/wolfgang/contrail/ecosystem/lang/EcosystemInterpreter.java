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

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
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
class EcosystemInterpreter implements ExpressionVisitor<CodeValue, EcosystemInterpretationException> {

	private final EcosystemSymbolTable symbolTable;
	private final Map<String, CodeValue> environment;

	/**
	 * Constructor
	 * 
	 * @param environment
	 */
	EcosystemInterpreter(EcosystemSymbolTable factory, Map<String, CodeValue> environment) {
		super();
		this.symbolTable = factory;
		this.environment = environment;
	}

	public CodeValue visit(final List<Expression> expressions) throws EcosystemInterpretationException {
		final CodeValue[] values = new CodeValue[expressions.size()];
		final EcosystemInterpreter interpret = new EcosystemInterpreter(symbolTable, environment);

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
	public CodeValue visit(final Reference expression) throws EcosystemInterpretationException {
		final String name = expression.getValue();

		if (symbolTable.hasImportation(name)) {
			return new ComponentValue(environment, symbolTable.getImportation(name));
		} else if (environment.containsKey(name)) {
			return environment.get(name);
		} else {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "definition.not.found");
			throw new EcosystemInterpretationException(message.format(name));
		}
	}

	@Override
	public CodeValue visit(Atom expression) throws EcosystemInterpretationException {
		return new ConstantValue(expression.getValue());
	}

	@Override
	public CodeValue visit(Apply expression) throws EcosystemInterpretationException {
		final CodeValue interpreted = expression.getFunction().visit(this);

		if (interpreted instanceof ClosureValue) {
			final ClosureValue closure = (ClosureValue) interpreted;
			final CodeValue result = expression.getParameter().visit(this);
			final String parameterName = closure.getFunction().getParameter(expression.getBinding());
			final List<Expression> applied = closure.getFunction().apply(parameterName);
			closure.getEnvironment().put(parameterName, result);
			return new EcosystemInterpreter(symbolTable, closure.getEnvironment()).visit(applied);
		} else {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "function.required");
			throw new EcosystemInterpretationException(message.format());
		}
	}

	@Override
	public CodeValue visit(Function expression) throws EcosystemInterpretationException {
		final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
		newEnvironment.putAll(environment);
		return new ClosureValue(expression, newEnvironment);
	}
}