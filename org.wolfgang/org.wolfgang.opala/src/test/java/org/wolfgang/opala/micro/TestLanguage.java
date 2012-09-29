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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.micro.Language.Expression;
import org.wolfgang.opala.micro.Language.Lambda;
import org.wolfgang.opala.micro.Language.S0Unit;
import org.wolfgang.opala.micro.Language.Variable;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.ScannerFactory;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * <code>TestLanguage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestLanguage {

	@Test
	public void testNominal01() throws EntryAlreadyBoundException, ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("fun a -> a".getBytes());
		final Scanner scanner = ScannerFactory.create(input);
		final Language.MyLang myLang = new Language.MyLang();

		final Expression expression = myLang.parse(S0Unit.class, scanner, null);
		
		assertEquals(Lambda.class, expression.getClass());
		final Lambda lambda = (Lambda) expression;
		assertEquals("a", lambda.variable);
		assertEquals(Variable.class, lambda.body.getClass());
		final Variable variable = (Variable) lambda.body;
		assertEquals("a", variable.name);
	}

}
