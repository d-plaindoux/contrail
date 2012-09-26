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

import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.define;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.function;
import static org.wolfgang.contrail.ecosystem.lang.model.ModelFactory.reference;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.dsl.ESLLanguage;
import org.wolfgang.contrail.dsl.StatementUnit;
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
public class TestStatementDSL extends TestCase {

	@Test
	public void testDefinition01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("def id = fun a -> a;".getBytes());
		final Scanner scanner = ScannerFactory.create(input);
		
		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(StatementUnit.class, scanner, ecosystemModel);

		assertEquals(define("id", function(reference("a"), "a")), compile);
	}

	@Test
	public void testDefinition02() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("def constant = a;".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final ESLLanguage celLanguage = new ESLLanguage();

		final Expression compile = celLanguage.parse(StatementUnit.class, scanner, ecosystemModel);

		assertEquals(define("constant", reference("a")), compile);
	}
}
