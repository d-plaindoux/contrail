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

package org.wolfgang.contrail.ecosystem.lang.code;

import java.util.List;
import java.util.Map;

import org.wolfgang.contrail.ecosystem.lang.EcosystemInterpretationException;
import org.wolfgang.contrail.ecosystem.lang.EcosystemInterpreter;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.Function;

/**
 * <code>ClosureValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClosureValue implements CodeValue {
	private final EcosystemInterpreter interpreter;
	private final Map<String, CodeValue> environment;
	private final Function function;

	/**
	 * Constructor
	 * 
	 * @param function
	 * @param environement
	 */
	public ClosureValue(EcosystemInterpreter interpreter, Function function, Map<String, CodeValue> environement) {
		super();
		this.interpreter = interpreter;
		this.function = function;
		this.environment = environement;
	}

	/**
	 * Apply the function a a given parameter
	 * 
	 * @param value
	 * @return the application result
	 * @throws EcosystemInterpretationException
	 */
	public CodeValue apply(CodeValue value) throws EcosystemInterpretationException {
		return this.apply(null, value);
	}

	/**
	 * Apply the function a a given parameter
	 * 
	 * @param value
	 * @return the application result
	 * @throws EcosystemInterpretationException
	 */
	public CodeValue apply(String name, CodeValue value) throws EcosystemInterpretationException {
		final String parameterName = function.getParameter(name);
		final List<Expression> applied = function.apply(parameterName);
		this.environment.put(parameterName, value);
		return interpreter.create(environment).visit(applied);
	}

	@Override
	public <T, E extends Exception> T visit(CodeValueVisitor<T, E> visitor) throws E {
		return visitor.visit(this);
	}
}