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

import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.EcosystemCodeValueGenerator;
import org.wolfgang.contrail.ecosystem.lang.EcosystemCodeValueGeneratorException;
import org.wolfgang.contrail.ecosystem.lang.EcosystemSymbolTable;
import org.wolfgang.contrail.ecosystem.lang.EcosystemSymbolTableImpl;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.Function;

/**
 * <code>ClosureValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClosureValue implements CodeValue {
	private final ContextFactory contextFactory;
	private final EcosystemSymbolTableImpl symbolTable;
	private final Function function;

	/**
	 * Constructor
	 * 
	 * @param function
	 * @param environement
	 */
	public ClosureValue(ContextFactory contextFactory, EcosystemSymbolTable symbolTable, Function function) {
		super();
		this.contextFactory = contextFactory;
		this.symbolTable = new EcosystemSymbolTableImpl(symbolTable);
		this.function = function;
	}

	/**
	 * Apply the function a a given parameter
	 * 
	 * @param value
	 * @return the application result
	 * @throws EcosystemCodeValueGeneratorException
	 */
	public CodeValue apply(CodeValue... values) throws EcosystemCodeValueGeneratorException {
		return this.apply(null, values);
	}

	/**
	 * Apply the function a a given parameter
	 * 
	 * @param value
	 * @return the application result
	 * @throws EcosystemCodeValueGeneratorException
	 */
	public CodeValue apply(String[] names, CodeValue... values) throws EcosystemCodeValueGeneratorException {
		final String[] parameterNames = function.getParameters(names, values.length);
		final List<Expression> applied = function.apply(parameterNames);

		for (int i = 0; i < parameterNames.length; i++) {
			this.symbolTable.putDefinition(parameterNames[i], values[i]);
		}

		return new EcosystemCodeValueGenerator(contextFactory, symbolTable).visit(applied);
	}

	@Override
	public <T, E extends Exception> T visit(CodeValueVisitor<T, E> visitor) throws E {
		return visitor.visit(this);
	}
}