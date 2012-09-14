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

package org.wolfgang.opala.scanner.impl;


import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import org.wolfgang.opala.lexing.ILexeme;
import org.wolfgang.opala.lexing.ILexemeTokenizer;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.impl.Lexeme;
import org.wolfgang.opala.scanner.ILexemeFilter;
import org.wolfgang.opala.scanner.IScanner;
import org.wolfgang.opala.scanner.IScannerListener;
import org.wolfgang.opala.scanner.exception.ScannerException;

public class Scanner implements IScanner {

    protected ILexemeFilter lexemeFilter;
    private int lookup;
    private List<ILexeme> lexemes;
    private ILexemeTokenizer tokenizer;
    private IScannerListener listener;

    /**
     * Constructor
     *
     * @param lexemeFilter Skipped tokens specified by types
     * @param tokenizer
     * @throws org.wolfgang.opala.scanner.exception.ScannerException
     *
     */

    public Scanner(ILexemeFilter lexemeFilter, ILexemeTokenizer tokenizer) throws ScannerException {
        this.lexemeFilter = lexemeFilter;
        this.lookup = 1;
        this.tokenizer = tokenizer;
        this.lexemes = new ArrayList<ILexeme>();
        this.listener = null;
        this.start();
    }

    public IScanner buildWith(ILexemeTokenizer tokenizer) throws ScannerException {
        return new Scanner(this.lexemeFilter, tokenizer);
    }

    private void start() throws ScannerException {
        this.lexemes.add(this.getNextLexeme());
        while (this.isFinished() == false && lexemes.size() < this.lookup) {
            this.lexemes.add(this.getNextLexeme());
        }
    }

    public ILexemeFilter setLexemeFilter(ILexemeFilter filter) {
        ILexemeFilter oldOne = this.lexemeFilter;
        this.lexemeFilter = filter;
        return oldOne;
    }

    public void finish() throws ScannerException {
        this.tokenizer.finish();
    }

    public boolean isFinished() throws ScannerException {
        return this.currentLexeme().getKind() == LexemeKind.FINISHED;
    }

    public ILexeme currentLexeme() throws ScannerException {
        return this.lookaheadAt(0);
    }

    public void rollback(ILexeme lexeme) {
        this.lexemes.add(0, lexeme);
    }

    private ILexeme getNextLexeme() throws ScannerException {
        return lexemeFilter.getNextLexeme(this.tokenizer.getNextLexeme(), this.tokenizer);
    }

    public ILexeme scan() throws ScannerException {
        ILexeme lexeme = this.currentLexeme();
        if (this.isFinished() == false) {
            this.notifyLexeme(this.lexemes.remove(0));
            this.lexemes.add(this.getNextLexeme());
        } else if (this.lexemes.size() > 0) {
            this.notifyLexeme(this.lexemes.remove(0));
        } else {
            throw new ScannerException(new EOFException());
        }

        return lexeme;
    }

    private ILexeme lookaheadAt(int index) throws ScannerException {
        if (index > this.lookup) {
            throw new IllegalArgumentException("out of bound (index > lookahead size");
        } else if (index < this.lexemes.size()) {
            return this.lexemes.get(index);
        } else {
            return new Lexeme(LexemeKind.FINISHED, null);
        }
    }

    public ILexeme scan(LexemeKind kind, String lexeme) throws LexemeNotFoundException, ScannerException {
        if (this.currentLexeme().isA(kind, lexeme)) {
            return this.scan();
        } else {
            throw new LexemeNotFoundException(this.currentLexeme());
        }
    }

    public ILexeme scan(LexemeKind kind) throws LexemeNotFoundException, ScannerException {
        if (this.currentLexeme().isA(kind)) {
            return this.scan();
        } else {
            throw new LexemeNotFoundException(this.currentLexeme());
        }
    }

    public ILexeme scan(String lexeme) throws LexemeNotFoundException, ScannerException {
        if (this.currentLexeme().isA(lexeme)) {
            return this.scan();
        } else {
            throw new LexemeNotFoundException(this.currentLexeme());
        }
    }

    private void notifyLexeme(ILexeme lexeme) {
        if (this.listener != null) {
            this.listener.scanPerformed(lexeme);
        }
    }

    public IScannerListener setListener(IScannerListener listener) {
        IScannerListener oldOne = this.listener;
        this.listener = listener;
        return oldOne;
    }
}
