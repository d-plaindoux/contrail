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


import java.util.List;

import org.wolfgang.opala.lexing.Lexeme;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.LexemeTokenizer;
import org.wolfgang.opala.lexing.Location;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * A Scanner provides a visitor of tokenized stream. A tokenized stream was in fact a finite sequence of terms
 * reflecting standardized lexical unit like string, identified sequence of letters, integers, sequence of characters
 * (without letter and digit) and special for unrecognized characters.
 */

public class ListBasedTokenizer implements LexemeTokenizer {

    //
    // Attributes
    //

    private List<Lexeme> lexemes;
    private Location lastLocation;

    /**
     * Constructor
     *
     * @param lexemes which is visited
     * @throws org.wolfgang.opala.scanner.exception.ScannerException
     *
     */
    public ListBasedTokenizer(List<Lexeme> lexemes) throws ScannerException {
        assert lexemes.size() > 0;
        this.lexemes = lexemes;
        this.lastLocation = null;
    }

    /**
     * @throws org.wolfgang.opala.scanner.exception.ScannerException
     *
     */
    public void finish() {
        lexemes.clear();
    }

    /**
     * @return
     * @throws org.wolfgang.opala.scanner.exception.ScannerException
     *
     */
    public Lexeme getNextLexeme() throws ScannerException {
        if (lexemes.size() == 0) {
            return new LexemeImpl(lastLocation, LexemeKind.FINISHED, null);
        } else {
            return lexemes.remove(0);
        }
    }
}