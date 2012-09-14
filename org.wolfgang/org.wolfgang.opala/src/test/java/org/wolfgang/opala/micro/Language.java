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

package org.wolfgang.opala.micro;

import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.impl.LexemeImpl;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.parsing.impl.AbstractLanguageSupport;
import org.wolfgang.opala.parsing.impl.AbstractSetOfCompilationUnit;
import org.wolfgang.opala.scanner.LexemeFilter;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * <code>Language</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface Language {

	/**
	 * <code>ExpressionUnit</code>::= Lambda | Application | String | Variable
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class ExpressionUnit extends AbstractSetOfCompilationUnit<Void, Void> {
		/**
		 * Constructor
		 * 
		 * @throws EntryAlreadyBoundException
		 */
		public ExpressionUnit() throws EntryAlreadyBoundException {
			this.addCompilationUnit(new LexemeImpl(LexemeKind.IDENT, "fun"), LambdaUnit.class.getName());
			this.addCompilationUnit(new LexemeImpl(LexemeKind.OPERATOR, "("), BlockUnit.class.getName());
			this.addCompilationUnit(new LexemeImpl(LexemeKind.IDENT, null), SingletonUnit.class.getName());
			this.addCompilationUnit(new LexemeImpl(LexemeKind.STRING, null), SingletonUnit.class.getName());
		}
	}

	public class SingletonUnit implements CompilationUnit<Void, Void> {
		@Override
		public Void compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			scanner.scan();
			return null;
		}

	}

	/**
	 * <code>LambdaUnit</code>::= fun ident* -> Expression
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class LambdaUnit implements CompilationUnit<Void, Void> {
		@Override
		public Void compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			scanner.scan(LexemeKind.IDENT, "fun");
			while (scanner.currentLexeme().isA(LexemeKind.IDENT)) {
				scanner.scan();
			}
			scanner.scan(LexemeKind.OPERATOR, "->");

			support.getUnitByKey(ExpressionUnit.class.getName()).compile(support, scanner, parameter);

			return null;
		}
	}

	/**
	 * <code>LambdaUnit</code>::= fun ident* -> Expression
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class BlockUnit implements CompilationUnit<Void, Void> {
		@Override
		public Void compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			scanner.scan(LexemeKind.OPERATOR, "(");
			while (!scanner.currentLexeme().isA(LexemeKind.OPERATOR, ")")) {
				support.getUnitByKey(ExpressionUnit.class.getName()).compile(support, scanner, parameter);
			}
			scanner.scan(LexemeKind.OPERATOR, ")");

			return null;
		}
	}

	public class S0Unit implements CompilationUnit<Void, Void> {
		@Override
		public Void compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			support.getUnitByKey(ExpressionUnit.class.getName()).compile(support, scanner, parameter);
			while (!scanner.isFinished()) {
				support.getUnitByKey(ExpressionUnit.class.getName()).compile(support, scanner, parameter);
			}
			return null;
		}
	}

	public class MyLang extends AbstractLanguageSupport {
		/**
		 * Constructor
		 * 
		 * @throws EntryAlreadyBoundException
		 */
		public MyLang() throws EntryAlreadyBoundException {
			super();
			this.addKeyword("fun");
			
			this.addUnit(ExpressionUnit.class.getName(), new ExpressionUnit());
			this.addUnit(SingletonUnit.class.getName(), new SingletonUnit());
			this.addUnit(LambdaUnit.class.getName(), new LambdaUnit());
			this.addUnit(BlockUnit.class.getName(), new BlockUnit());
		}

		@Override
		public LexemeFilter getSkippedLexemes() {
			return LexemeFilter.SPACE_AND_COMMENT;
		}
	}
}
