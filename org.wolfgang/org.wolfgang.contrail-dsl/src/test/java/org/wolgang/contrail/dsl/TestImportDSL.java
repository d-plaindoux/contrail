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

package org.wolgang.contrail.dsl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.dsl.CELLanguage;
import org.wolfgang.contrail.dsl.ImportUnit;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.exception.UnexpectedLexemeException;
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
public class TestImportDSL extends TestCase {

	@Test
	public void testImport01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("import abcd".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final CELLanguage celLanguage = new CELLanguage();

		celLanguage.parse(ImportUnit.class.getName(), scanner, ecosystemModel);

		assertEquals(1, ecosystemModel.getImportations().size());
		assertEquals("abcd", ecosystemModel.getImportations().get(0).getElement());
	}

	@Test
	public void testImport02() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("import a.b.c.d".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final CELLanguage celLanguage = new CELLanguage();

		celLanguage.parse(ImportUnit.class.getName(), scanner, ecosystemModel);

		assertEquals(1, ecosystemModel.getImportations().size());
		assertEquals("a.b.c.d", ecosystemModel.getImportations().get(0).getElement());
	}

	@Test
	public void testImportFail01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("import a.b.c.d.".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final CELLanguage celLanguage = new CELLanguage();

		try {
			celLanguage.parse(ImportUnit.class.getName(), scanner, ecosystemModel);
			fail();
		} catch (UnexpectedLexemeException e) {
			assertTrue(e.getLexeme().isA(LexemeKind.FINISHED));
		}
	}

	@Test
	public void testImportFail02() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("import a.b.12.d.".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final CELLanguage celLanguage = new CELLanguage();

		try {
			celLanguage.parse(ImportUnit.class.getName(), scanner, ecosystemModel);
			fail();
		} catch (UnexpectedLexemeException e) {
			assertTrue(e.getLexeme().isA(LexemeKind.INTEGER));
		}
	}
}
