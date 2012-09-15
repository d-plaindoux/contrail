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

	public interface Expression {
	}

	public class Lambda implements Expression {
		public String variable;
		public Expression body;

		/**
		 * Constructor
		 * 
		 * @param variable
		 * @param body
		 */
		public Lambda(String variable, Expression body) {
			super();
			this.variable = variable;
			this.body = body;
		}
	}

	public class Application implements Expression {
		public Expression function;
		public Expression argument;

		/**
		 * Constructor
		 * 
		 * @param function
		 * @param argument
		 */
		public Application(Expression function, Expression argument) {
			super();
			this.function = function;
			this.argument = argument;
		}
	}

	public class Variable implements Expression {
		public String name;

		/**
		 * Constructor
		 * 
		 * @param name
		 */
		public Variable(String name) {
			super();
			this.name = name;
		}
	}

	public class Atom implements Expression {
		public String name;

		/**
		 * Constructor
		 * 
		 * @param name
		 */
		public Atom(String name) {
			super();
			this.name = name;
		}
	}

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
			this.addCompilationUnit(new LexemeImpl(LexemeKind.IDENT, null), IdentUnit.class.getName());
			this.addCompilationUnit(new LexemeImpl(LexemeKind.STRING, null), AtomUnit.class.getName());
		}
	}

	/**
	 * <code>SingletonUnit</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class IdentUnit implements CompilationUnit<Expression, Void> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			final Expression expression = new Variable(scanner.currentLexeme().getValue());
			scanner.scan();
			return expression;
		}

	}

	/**
	 * <code>SingletonUnit</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class AtomUnit implements CompilationUnit<Expression, Void> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			final Expression expression = new Atom(scanner.currentLexeme().getValue());
			scanner.scan();
			return expression;
		}

	}

	/**
	 * <code>LambdaUnit</code>::= fun ident* -> Expression
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public class LambdaUnit implements CompilationUnit<Expression, Void> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
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
	public class BlockUnit implements CompilationUnit<Expression, Void> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			scanner.scan(LexemeKind.OPERATOR, "(");
			while (!scanner.currentLexeme().isA(LexemeKind.OPERATOR, ")")) {
				support.getUnitByKey(ExpressionUnit.class.getName()).compile(support, scanner, parameter);
			}
			scanner.scan(LexemeKind.OPERATOR, ")");

			return null;
		}
	}

	public class S0Unit implements CompilationUnit<Expression, Void> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
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

			this.addUnit(S0Unit.class.getName(), new S0Unit());
			this.addUnit(ExpressionUnit.class.getName(), new ExpressionUnit());
			this.addUnit(IdentUnit.class.getName(), new IdentUnit());
			this.addUnit(AtomUnit.class.getName(), new AtomUnit());
			this.addUnit(LambdaUnit.class.getName(), new LambdaUnit());
			this.addUnit(BlockUnit.class.getName(), new BlockUnit());
		}

		@Override
		public LexemeFilter getSkippedLexemes() {
			return LexemeFilter.SPACE_AND_COMMENT;
		}
	}
}
