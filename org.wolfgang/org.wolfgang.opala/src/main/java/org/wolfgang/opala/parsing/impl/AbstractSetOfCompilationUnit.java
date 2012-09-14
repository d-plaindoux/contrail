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

package org.wolfgang.opala.parsing.impl;

import java.util.ArrayList;
import java.util.List;

import org.wolfgang.opala.lexing.Lexeme;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.exception.UnexpectedLexemeException;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;
import org.wolfgang.opala.scanner.impl.CheckPointScanner;
import org.wolfgang.opala.utils.Cast;

/**
 * This class provides the main entry for statement compilation. Statements can
 * be extended using
 * {@link #addCompilationUnit(org.wolfgang.opala.lexing.Lexeme,String)}
 * function.
 */

public abstract class AbstractSetOfCompilationUnit<E, P> implements CompilationUnit<E, P> {

	private final Cast<E> localCast = new Cast<E>();

	private class Entry {
		private final Lexeme key;
		private final String val;
		private final boolean tryDefault;

		public Entry(Lexeme key, String val, boolean tryDefault) {
			this.key = key;
			this.val = val;
			this.tryDefault = tryDefault;
		}
	}

	protected final List<Entry> unitSet;
	protected String defaultUnit;

	protected AbstractSetOfCompilationUnit() {
		this.unitSet = new ArrayList<Entry>();
	}

	private Entry getCompilationUnit(Lexeme lexeme) {
		for (Entry entry : unitSet) {
			Lexeme iLexeme = entry.key;
			if (iLexeme.isA(lexeme.getKind(), iLexeme.getValue() == null ? null : lexeme.getValue())) {
				return entry;
			}
		}

		return null;
	}

	final public boolean hasCompilationUnit(Lexeme lexeme) {
		return this.getCompilationUnit(lexeme) != null;
	}

	final public void setDefaultCompilationUnit(String defautUnit) {
		this.defaultUnit = defautUnit;
	}

	final public void addCompilationUnit(Lexeme lexeme, String unitName) throws EntryAlreadyBoundException {
		this.addCompilationUnit(lexeme, unitName, false);
	}

	final public void addCompilationUnit(Lexeme lexeme, String unitName, boolean tryDefault) throws EntryAlreadyBoundException {
		if (this.getCompilationUnit(lexeme) == null) {
			this.unitSet.add(new Entry(lexeme, unitName, tryDefault));
		} else {
			throw new EntryAlreadyBoundException(lexeme.getValue());
		}
	}

	public boolean canCompile(Scanner scanner) throws ScannerException {
		return this.getCompilationUnit(scanner.currentLexeme()) != null;
	}

	public E compile(LanguageSupport support, Scanner scanner, P parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		scanner.setLexemeFilter(support.getSkippedLexemes());

		final Entry unitName = this.getCompilationUnit(scanner.currentLexeme());

		if (unitName != null) {
			final CheckPointScanner cp;
			if (unitName.tryDefault) {
				cp = CheckPointScanner.newInstance(scanner);
			} else {
				cp = null;
			}

			try {
				final E e = this.localCast.perform(support.getUnitByKey(unitName.val).compile(support, scanner, parameter));
				if (cp != null) {
					cp.commit();
				}
				return e;
			} catch (ScannerException e) {
				if (cp != null) {
					cp.rollback();
				} else {
					throw e;
				}
			} catch (LexemeNotFoundException e) {
				if (cp != null) {
					cp.rollback();
				} else {
					throw e;
				}
			}
		}

		if (defaultUnit != null) {
			return this.localCast.perform(support.getUnitByKey(defaultUnit).compile(support, scanner, parameter));
		} else {
			throw new UnexpectedLexemeException(support.getContext(), scanner.currentLexeme());
		}
	}

}
