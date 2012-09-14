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

import org.wolfgang.opala.lexing.Lexeme;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.LexemeTokenizer;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.impl.LexemeImpl;
import org.wolfgang.opala.scanner.LexemeFilter;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.ScannerListener;
import org.wolfgang.opala.scanner.exception.ScannerException;

public class ScannerImpl implements Scanner {

	protected LexemeFilter lexemeFilter;
	private int lookup;
	private List<Lexeme> lexemes;
	private LexemeTokenizer tokenizer;
	private ScannerListener listener;

	/**
	 * Constructor
	 * 
	 * @param lexemeFilter
	 *            Skipped tokens specified by types
	 * @param tokenizer
	 * @throws org.wolfgang.opala.scanner.exception.ScannerException
	 */

	public ScannerImpl(LexemeTokenizer tokenizer) throws ScannerException {
		this(LexemeFilter.NONE, tokenizer);
	}

	public ScannerImpl(LexemeFilter lexemeFilter, LexemeTokenizer tokenizer) throws ScannerException {
		this.lexemeFilter = lexemeFilter;
		this.lookup = 1;
		this.tokenizer = tokenizer;
		this.lexemes = new ArrayList<Lexeme>();
		this.listener = null;
		this.start();
	}

	public Scanner buildWith(LexemeTokenizer tokenizer) throws ScannerException {
		return new ScannerImpl(this.lexemeFilter, tokenizer);
	}

	private void start() throws ScannerException {
		this.lexemes.add(this.getNextLexeme());
		while (this.isFinished() == false && lexemes.size() < this.lookup) {
			this.lexemes.add(this.getNextLexeme());
		}
	}

	public LexemeFilter setLexemeFilter(LexemeFilter filter) {
		LexemeFilter oldOne = this.lexemeFilter;
		this.lexemeFilter = filter;
		return oldOne;
	}

	public void finish() throws ScannerException {
		this.tokenizer.finish();
	}

	public boolean isFinished() throws ScannerException {
		return this.currentLexeme().getKind() == LexemeKind.FINISHED;
	}

	public Lexeme currentLexeme() throws ScannerException {
		return this.lookaheadAt(0);
	}

	public void rollback(Lexeme lexeme) {
		this.lexemes.add(0, lexeme);
	}

	private Lexeme getNextLexeme() throws ScannerException {
		return lexemeFilter.getNextLexeme(this.tokenizer.getNextLexeme(), this.tokenizer);
	}

	public Lexeme scan() throws ScannerException {
		Lexeme lexeme = this.currentLexeme();
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

	private Lexeme lookaheadAt(int index) throws ScannerException {
		if (index > this.lookup) {
			throw new IllegalArgumentException("out of bound (index > lookahead size");
		} else if (index < this.lexemes.size()) {
			return this.lexemes.get(index);
		} else {
			return new LexemeImpl(LexemeKind.FINISHED, null);
		}
	}

	public Lexeme scan(LexemeKind kind, String lexeme) throws LexemeNotFoundException, ScannerException {
		if (this.currentLexeme().isA(kind, lexeme)) {
			return this.scan();
		} else {
			throw new LexemeNotFoundException(this.currentLexeme());
		}
	}

	public Lexeme scan(LexemeKind kind) throws LexemeNotFoundException, ScannerException {
		if (this.currentLexeme().isA(kind)) {
			return this.scan();
		} else {
			throw new LexemeNotFoundException(this.currentLexeme());
		}
	}

	public Lexeme scan(String lexeme) throws LexemeNotFoundException, ScannerException {
		if (this.currentLexeme().isA(lexeme)) {
			return this.scan();
		} else {
			throw new LexemeNotFoundException(this.currentLexeme());
		}
	}

	private void notifyLexeme(Lexeme lexeme) {
		if (this.listener != null) {
			this.listener.performScan(lexeme);
		}
	}

	public ScannerListener setListener(ScannerListener listener) {
		ScannerListener oldOne = this.listener;
		this.listener = listener;
		return oldOne;
	}
}
