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

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * <code>UnitOrFlowExpressionUnit</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class UnitOrFlowExpressionUnit implements CompilationUnit<Expression, EcosystemModel> {

	@Override
	public Expression compile(LanguageSupport support, Scanner scanner, EcosystemModel parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final Expression expression;

		scanner.scan(LexemeKind.OPERATOR, "(");

		if (scanner.currentLexeme().isA(LexemeKind.OPERATOR, ")")) {
			expression = new Atom();
		} else {
			expression = Coercion.coerce(support.getUnitByKey(FlowExpressionUnit.class.getName()).compile(support, scanner, parameter), Expression.class);
		}

		scanner.scan(LexemeKind.OPERATOR, ")");

		return expression;
	}
}
