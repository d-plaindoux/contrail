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

package org.wolfgang.opala.lexing.impl;


import java.io.IOException;

import org.wolfgang.opala.lexing.IGenericLexer;
import org.wolfgang.opala.lexing.ILexeme;
import org.wolfgang.opala.lexing.ILexemeTokenizer;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * A Scanner provides a visitor of tokenized stream. A tokenized stream was in fact a finite sequence of terms
 * reflecting standardized lexical unit like string, identified sequence of letters, integers, sequence of characters
 * (without letter and digit) and special for unrecognized characters.
 */

public class LexerTokenizer implements ILexemeTokenizer {

    //
    // Attributes
    //

    private IGenericLexer lexer;

    /**
     * Constructor
     *
     * @param lexer which is visited
     * @throws ScannerException
     */
    public LexerTokenizer(IGenericLexer lexer) throws ScannerException {
        this.lexer = lexer;
    }

    /**
     * @throws ScannerException
     */
    public void finish() throws ScannerException {
        try {
            lexer.finish();
        } catch (IOException ioe) {
            throw new ScannerException(ioe);
        }
    }

    /**
     * @return
     * @throws ScannerException
     */
    public ILexeme getNextLexeme() throws ScannerException {
        try {
            int p = lexer.getLexemeType();
            if (p == -1) {
                return new Lexeme(lexer.getLocation(), LexemeKind.FINISHED, null);
            } else {
                return new Lexeme(lexer.getLocation(), Tokens.getKind(p), lexer.getLexemeValue());
            }
        } catch (IOException ioe) {
            throw new ScannerException(ioe);
        }
    }
}
