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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.impl.GenLex;
import org.wolfgang.opala.lexing.impl.LexerTokenizer;
import org.wolfgang.opala.micro.Language.ExpressionUnit;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;
import org.wolfgang.opala.scanner.impl.ScannerImpl;

/**
 * <code>TestLanguage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestLanguage extends TestCase {

	@Test
	public void testNominal01() throws EntryAlreadyBoundException, ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final InputStream input = new ByteArrayInputStream("fun a -> a".getBytes());
		final GenLex genLex = new GenLex(input);
		final LexerTokenizer lexer = new LexerTokenizer(genLex.getLexer());
		final Scanner scanner = new ScannerImpl(lexer);
		final Language.MyLang myLang = new Language.MyLang();
		
		myLang.parse(ExpressionUnit.class.getName(), scanner, null);
	}

}
