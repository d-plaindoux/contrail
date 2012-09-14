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

package org.wolfgang.opala.lexing;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.opala.lexing.impl.GenLex;
import org.wolfgang.opala.lexing.impl.Tokens;

/**
 * <code>TestGenLex</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestGenLex extends TestCase {

	@Test
	public void testGenLex01() throws IOException {
		IGenericLexer lexer = new GenLex(new ByteArrayInputStream("\"A String\" anId 'a'  +123".getBytes())).getLexer();

		assertEquals(Tokens.getKind(Tokens._STRING_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals("A String", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._SPACES_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals(" ", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._IDENT_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals("anId", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._SPACES_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals(" ", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._CHARACTERS_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals("a", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._SPACES_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals("  ", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._OPERATOR_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals("+", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._INTEGER_), Tokens.getKind(lexer.getLexemeType()));
		assertEquals("123", lexer.getLexemeValue());
		assertEquals(Tokens.getKind(Tokens._EOF_), Tokens.getKind(lexer.getLexemeType()));
	}

}
