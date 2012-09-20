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
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.dsl.AtomUnit;
import org.wolfgang.contrail.dsl.CELLanguage;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;
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
public class TestAtomDSL extends TestCase {

	@Test
	public void testAtom01() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("\"Hello, World!\"".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final CELLanguage celLanguage = new CELLanguage();

		final Expression compile = celLanguage.getUnitByKey(AtomUnit.String.class).compile(celLanguage, scanner, ecosystemModel);
		assertTrue(Coercion.canCoerce(compile, Atom.class));
		assertEquals("Hello, World!", Coercion.coerce(compile, Atom.class).getValue());
	}

	@Test
	public void testAtom02() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("123".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final CELLanguage celLanguage = new CELLanguage();

		final Expression compile = celLanguage.getUnitByKey(AtomUnit.Integer.class).compile(celLanguage, scanner, ecosystemModel);
		assertTrue(Coercion.canCoerce(compile, Atom.class));
		assertEquals("123", Coercion.coerce(compile, Atom.class).getValue());
	}

	@Test
	public void testAtom03() throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("'a'".getBytes());
		final Scanner scanner = ScannerFactory.create(input);

		final EcosystemModel ecosystemModel = new EcosystemModel();
		final CELLanguage celLanguage = new CELLanguage();

		final Expression compile = celLanguage.getUnitByKey(AtomUnit.Character.class).compile(celLanguage, scanner, ecosystemModel);
		assertTrue(Coercion.canCoerce(compile, Atom.class));
		assertEquals("a", Coercion.coerce(compile, Atom.class).getValue());
	}
}
