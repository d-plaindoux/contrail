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

import java.util.ArrayList;
import java.util.List;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.Flow;
import org.wolfgang.contrail.ecosystem.lang.model.Function;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * <code>ImportUnit</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class AbstractionUnit implements CompilationUnit<Expression, EcosystemModel> {

	@Override
	public Expression compile(LanguageSupport support, Scanner scanner, EcosystemModel ecosystemModel) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		scanner.scan(LexemeKind.IDENT, "fun");

		final List<String> variables = new ArrayList<String>();

		if (scanner.currentLexeme().isA(LexemeKind.OPERATOR, "(")) {
			scanner.scan(LexemeKind.OPERATOR, "(");
			scanner.scan(LexemeKind.OPERATOR, ")");
			variables.add(null);
		} else {
			variables.add(scanner.scan(LexemeKind.IDENT).getValue());
			while (scanner.currentLexeme().isA(LexemeKind.IDENT)) {
				variables.add(scanner.scan(LexemeKind.IDENT).getValue());
			}
		}

		scanner.scan(LexemeKind.OPERATOR, "->");

		final Expression expression = support.getUnitByKey(FlowExpressionUnit.class).compile(support, scanner, ecosystemModel);
		
		final Function function = new Function();
		for (String string : variables) {
			function.add(string);
		}

		if (Coercion.canCoerce(expression, Flow.class)) {
			final Flow flow = Coercion.coerce(expression, Flow.class);
			for (Expression subExpression : flow.getExpressions()) {
				function.add(subExpression);
			}
		} else {
			function.add(expression);
		}

		return function;
	}
}
