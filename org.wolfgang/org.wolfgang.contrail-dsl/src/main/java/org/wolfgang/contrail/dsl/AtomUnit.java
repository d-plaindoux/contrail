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
 * <code>AtomUnit</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface AtomUnit {

	class String implements CompilationUnit<Expression, EcosystemModel> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, EcosystemModel parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			final Atom atom = new Atom();
			atom.setValue(scanner.scan(LexemeKind.STRING).getValue());
			return atom;
		}
	}

	class Integer implements CompilationUnit<Expression, EcosystemModel> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, EcosystemModel parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			final Atom atom = new Atom();
			atom.setValue(scanner.scan(LexemeKind.INTEGER).getValue());
			return atom;
		}
	}

	class Character implements CompilationUnit<Expression, EcosystemModel> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, EcosystemModel parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			final Atom atom = new Atom();
			atom.setValue(scanner.scan(LexemeKind.CHARACTERS).getValue());
			return atom;
		}
	}

}
