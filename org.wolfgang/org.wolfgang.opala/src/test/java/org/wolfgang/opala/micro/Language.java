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

import java.util.ArrayList;
import java.util.List;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.impl.LexemeImpl;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.exception.ParsingException;
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
	public class ExpressionUnit extends AbstractSetOfCompilationUnit<Expression, Void> {
		/**
		 * Constructor
		 * 
		 * @throws EntryAlreadyBoundException
		 */
		public ExpressionUnit() throws EntryAlreadyBoundException {
			this.addCompilationUnit(new LexemeImpl(LexemeKind.IDENT, "fun"), LambdaUnit.class);
			this.addCompilationUnit(new LexemeImpl(LexemeKind.OPERATOR, "("), BlockUnit.class);
			this.addCompilationUnit(new LexemeImpl(LexemeKind.IDENT, null), IdentUnit.class);
			this.addCompilationUnit(new LexemeImpl(LexemeKind.STRING, null), AtomUnit.class);
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

			final List<String> values = new ArrayList<String>();

			values.add(0, scanner.scan(LexemeKind.IDENT).getValue());
			while (scanner.currentLexeme().isA(LexemeKind.IDENT)) {
				values.add(0, scanner.scan(LexemeKind.IDENT).getValue());
			}

			scanner.scan(LexemeKind.OPERATOR, "->");

			Expression body = Coercion.coerce(support.getUnitByKey(ExpressionUnit.class).compile(support, scanner, parameter), Expression.class);

			for (String value : values) {
				body = new Lambda(value, body);
			}

			return body;
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

			Expression expression = Coercion.coerce(support.getUnitByKey(ExpressionUnit.class).compile(support, scanner, parameter), Expression.class);

			while (!scanner.currentLexeme().isA(LexemeKind.OPERATOR, ")")) {
				expression = new Application(expression, Coercion.coerce(support.getUnitByKey(ExpressionUnit.class).compile(support, scanner, parameter), Expression.class));
			}

			scanner.scan(LexemeKind.OPERATOR, ")");

			return expression;
		}
	}

	public class S0Unit implements CompilationUnit<Expression, Void> {
		@Override
		public Expression compile(LanguageSupport support, Scanner scanner, Void parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
			Expression expression = Coercion.coerce(support.getUnitByKey(ExpressionUnit.class).compile(support, scanner, parameter), Expression.class);

			while (!scanner.isFinished()) {
				expression = new Application(expression, Coercion.coerce(support.getUnitByKey(ExpressionUnit.class).compile(support, scanner, parameter), Expression.class));
			}

			return expression;
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

			this.addUnit(new S0Unit());
			this.addUnit(new ExpressionUnit());
			this.addUnit(new IdentUnit());
			this.addUnit(new AtomUnit());
			this.addUnit(new LambdaUnit());
			this.addUnit(new BlockUnit());
		}

		@Override
		public LexemeFilter getSkippedLexemes() {
			return LexemeFilter.SPACE_AND_COMMENT;
		}
	}
}
