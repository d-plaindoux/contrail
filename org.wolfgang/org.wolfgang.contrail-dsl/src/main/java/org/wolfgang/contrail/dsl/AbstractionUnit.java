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
public class AbstractionUnit implements CompilationUnit<Void, EcosystemModel> {

	@Override
	public Void compile(LanguageSupport support, Scanner scanner, EcosystemModel ecosystemModel) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		scanner.scan(LexemeKind.IDENT, "fun");

		if (scanner.currentLexeme().isA(LexemeKind.OPERATOR, "(")) {
			scanner.scan(LexemeKind.OPERATOR, "(");
			scanner.scan(LexemeKind.OPERATOR, ")");
		} else {
			scanner.scan(LexemeKind.IDENT);
			while (scanner.currentLexeme().isA(LexemeKind.IDENT)) {
				scanner.scan(LexemeKind.IDENT);
			}
		}

		scanner.scan(LexemeKind.OPERATOR, "->");

		support.getUnitByKey(StatementUnit.class.getName()).compile(support, scanner, ecosystemModel);

		return null;
	}
}
