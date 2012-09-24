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

package org.wolfgang.contrail.dsl;

import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.impl.LexemeImpl;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.impl.AbstractSetOfCompilationUnit;

/**
 * <code>ExpressionUnit</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class SimpleExpressionUnit extends AbstractSetOfCompilationUnit<Expression, EcosystemModel> {

	public SimpleExpressionUnit() throws EntryAlreadyBoundException {
		super();
		this.addCompilationUnit(new LexemeImpl(LexemeKind.STRING, null), TerminalUnit.String.class);
		this.addCompilationUnit(new LexemeImpl(LexemeKind.INTEGER, null), TerminalUnit.Integer.class);
		this.addCompilationUnit(new LexemeImpl(LexemeKind.CHARACTERS, null), TerminalUnit.Character.class);
		this.addCompilationUnit(new LexemeImpl(LexemeKind.OPERATOR, "{"), BlockUnit.class);
		this.addCompilationUnit(new LexemeImpl(LexemeKind.OPERATOR, "("), UnitOrFlowExpressionUnit.class);
		this.addCompilationUnit(new LexemeImpl(LexemeKind.OPERATOR, "["), ChoiceExpressionUnit.class);
		this.addCompilationUnit(new LexemeImpl(LexemeKind.IDENT, "fun"), AbstractionUnit.class);
		this.addCompilationUnit(new LexemeImpl(LexemeKind.IDENT, null), TerminalUnit.Variable.class);
	}

}
