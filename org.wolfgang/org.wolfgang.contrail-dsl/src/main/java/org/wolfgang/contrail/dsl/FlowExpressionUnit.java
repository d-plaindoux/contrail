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

import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.Flow;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * <code>FlowExpressionUnit</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class FlowExpressionUnit implements CompilationUnit<Expression, EcosystemModel> {

	@Override
	public Expression compile(LanguageSupport support, Scanner scanner, EcosystemModel parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {

		final List<Expression> expressions = new ArrayList<Expression>();

		expressions.add(support.getUnitByKey(ExpressionUnit.class).compile(support, scanner, parameter));

		System.err.println("Found " + scanner.currentLexeme());
		while (scanner.currentLexeme().isA(LexemeKind.OPERATOR, "<>")) {
			scanner.scan(LexemeKind.OPERATOR, "<>");
			expressions.add(support.getUnitByKey(ExpressionUnit.class).compile(support, scanner, parameter));
		}

		
		if (expressions.size() == 1) {
			return expressions.get(0);
		} else {
			final Flow flow = new Flow();
			for (Expression expression : expressions) {
				flow.add(expression);
			}
			return flow;
		}
	}
}
