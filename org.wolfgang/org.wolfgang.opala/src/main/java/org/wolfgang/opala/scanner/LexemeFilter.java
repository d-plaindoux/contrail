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

package org.wolfgang.opala.scanner;

import org.wolfgang.opala.lexing.Lexeme;
import org.wolfgang.opala.lexing.LexemeTokenizer;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.scanner.exception.ScannerException;

public interface LexemeFilter {

	public static LexemeFilter NONE = new None();
	public static LexemeFilter SPACE_AND_COMMENT = new SpaceAndComment();

	static class None implements LexemeFilter {
		public Lexeme getNextLexeme(Lexeme current, LexemeTokenizer tokenizer) throws ScannerException {
			return current;
		}
	}

	static class SpaceAndComment implements LexemeFilter {
		public Lexeme getNextLexeme(Lexeme current, LexemeTokenizer tokenizer) throws ScannerException {
			Lexeme lexeme = current;

			while (lexeme.isA(LexemeKind.SPACES) || lexeme.isA(LexemeKind.EOL) || lexeme.isA(LexemeKind.COMMENT)) {
				lexeme = tokenizer.getNextLexeme();
			}

			return lexeme;
		}
	}

	static class LexemeSetOfFilters implements LexemeFilter {
		LexemeFilter[] filters;

		public LexemeSetOfFilters(LexemeFilter[] filters) {
			this.filters = filters;
		}

		public Lexeme getNextLexeme(Lexeme current, LexemeTokenizer tokenizer) throws ScannerException {
			Lexeme previouslexeme;
			Lexeme lastlexeme = current;

			do {
				previouslexeme = lastlexeme;
				for (LexemeFilter filter : filters) {
					lastlexeme = filter.getNextLexeme(lastlexeme, tokenizer);
				}
			} while (previouslexeme != lastlexeme);

			return lastlexeme;
		}
	}

	Lexeme getNextLexeme(Lexeme current, LexemeTokenizer tokenizer) throws ScannerException;
}