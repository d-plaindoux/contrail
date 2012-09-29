/*
			scanner.scan(LexemeKind.OPERATOR, ")");
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

import static org.junit.Assert.assertEquals;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.apply;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.atom;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.flow;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.function;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.reference;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.sequence;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.ScannerFactory;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * <code>TestImportDSL</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestExpressionDSL {

	@Test
	public void testAtom01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("\"Hello, World!\"".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(atom("Hello, World!"), compile);
	}

	@Test
	public void testAtom02() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("123".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(atom("123"), compile);
	}

	@Test
	public void testAtom03() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("'a'".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(atom("a"), compile);
	}

	@Test
	public void testUnit() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("()".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(unit(), compile);
	}

	@Test
	public void testFunction01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("fun a -> a".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(function(reference("a"), "a"), compile);
	}

	@Test
	public void testFunction02() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("fun a b -> a <> b".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(function(flow(reference("a"), reference("b")), "a", "b"), compile);
	}

	@Test
	public void testFunction03() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("fun a b -> a b".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(function(apply(reference("a"), reference("b")), "a", "b"), compile);
	}

	@Test
	public void testFunction04() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("fun a b -> { a ; b ; }".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(function(sequence(reference("a"), reference("b")), "a", "b"), compile);
	}

	@Test
	public void testApply01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("a b".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.getUnitByKey(ExpressionUnit.class).compile(celLanguage, scanner, ecosystemModel);

		assertEquals(apply(reference("a"), reference("b")), compile);
	}

	@Test
	public void testParenthesis01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("( 123 )".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(atom("123"), compile);
	}

	@Test
	public void testParenthesis02() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("( 'a' )".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(atom("a"), compile);
	}

	@Test
	public void testParenthesis03() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("( \"abc\" )".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(atom("abc"), compile);
	}

	@Test
	public void testParenthesis04() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("(fun a b -> a <> b)".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(function(flow(reference("a"), reference("b")), "a", "b"), compile);
	}

	@Test
	public void testParenthesis05() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("(fun a b -> a b)".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(SimpleExpressionUnit.class, scanner, ecosystemModel);

		assertEquals(function(apply(reference("a"), reference("b")), "a", "b"), compile);
	}
}
