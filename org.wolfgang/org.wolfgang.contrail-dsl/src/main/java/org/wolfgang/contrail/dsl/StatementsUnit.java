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
 * <code>StatementsUnit</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface StatementsUnit {
	public class Toplevel implements CompilationUnit<Void, EcosystemModel> {
		@Override
		public Void compile(LanguageSupport support, Scanner scanner, EcosystemModel parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			while (!scanner.isFinished()) {
				support.getUnitByKey(StatementUnit.class).compile(support, scanner, parameter);
			}

			return null;
		}
	}

	public class Block implements CompilationUnit<Void, EcosystemModel> {

		@Override
		public Void compile(LanguageSupport support, Scanner scanner, EcosystemModel parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			scanner.scan(LexemeKind.OPERATOR, "{");

			while (!scanner.currentLexeme().isA(LexemeKind.OPERATOR, "}")) {
				support.getUnitByKey(StatementUnit.class).compile(support, scanner, parameter);
			}

			scanner.scan(LexemeKind.OPERATOR, "}");

			return null;
		}
	}
}
