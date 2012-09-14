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

import org.wolfgang.opala.lexing.ILexeme;
import org.wolfgang.opala.lexing.ILexemeTokenizer;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.scanner.exception.ScannerException;


public interface ILexemeFilter {

    public static ILexemeFilter SPACE_AND_COMMENT = new SpaceAndComment();

    static class SpaceAndComment implements ILexemeFilter {
        public ILexeme getNextLexeme(ILexeme current, ILexemeTokenizer tokenizer) throws ScannerException {
            ILexeme lexeme = current;

            while (lexeme.isA(LexemeKind.SPACES) || lexeme.isA(LexemeKind.EOL) || lexeme.isA(LexemeKind.COMMENT)) {
                lexeme = tokenizer.getNextLexeme();
            }

            return lexeme;
        }
    }

    static class LexemeSetOfFilters implements ILexemeFilter {
        ILexemeFilter[] filters;

        public LexemeSetOfFilters(ILexemeFilter[] filters) {
            this.filters = filters;
        }

        public ILexeme getNextLexeme(ILexeme current, ILexemeTokenizer tokenizer) throws ScannerException {
            ILexeme previouslexeme;
            ILexeme lastlexeme = current;

            do {
                previouslexeme = lastlexeme;
                for (ILexemeFilter filter : filters) {
                    lastlexeme = filter.getNextLexeme(lastlexeme, tokenizer);
                }
            } while (previouslexeme != lastlexeme);

            return lastlexeme;
        }
    }

    ILexeme getNextLexeme(ILexeme current, ILexemeTokenizer tokenizer) throws ScannerException;
}