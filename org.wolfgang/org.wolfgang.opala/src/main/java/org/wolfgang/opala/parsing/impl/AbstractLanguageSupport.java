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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.LexemeFilter;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.CastException;
import org.wolfgang.opala.scanner.exception.ScannerException;
import org.wolfgang.opala.utils.Cast;

/**
 * <code>AbstractSupport</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
abstract public class AbstractLanguageSupport implements LanguageSupport {

	private final Stack<String> context;
	private final List<String> keywords;
	private final Map<String, CompilationUnit<?, ?>> units;

	{
		this.context = new Stack<String>();
		this.units = new HashMap<String, CompilationUnit<?, ?>>();
		this.keywords = new ArrayList<String>();
	}

	protected void addKeyword(String keyword) {
		this.keywords.add(keyword);
	}

	@Override
	public boolean isKeyword(String keyword) {
		return this.keywords.contains(keyword);
	}

	protected <E, P> void addUnit(String key, CompilationUnit<E, P> unit) throws EntryAlreadyBoundException {
		if (this.units.get(key) == null) {
			this.units.put(key, new CompilationUnitObserver<E, P>(key, unit));
		} else {
			throw new EntryAlreadyBoundException(key);
		}
	}

	@Override
	public <E, P> CompilationUnit<E, P> getUnitByKey(String key) throws ParsingUnitNotFound {
		try {
			final CompilationUnit<E, P> unit = new Cast<CompilationUnit<E, P>>().perform(this.units.get(key));
			if (unit != null) {
				return unit;
			} else {
				throw new ParsingUnitNotFound(key);
			}
		} catch (CastException e) {
			throw new ParsingUnitNotFound(e);
		}
	}

	@Override
	public void enterUnit(String key) {
		this.context.push(key);
	}

	@Override
	public void exitUnit(String key) {
		while (key.equals(this.context.pop()) == false)
			;
	}

	@Override
	public String[] getContext() {
		return this.context.toArray(new String[this.context.size()]);
	}

	@SuppressWarnings("unchecked")
	public <E, P> E parse(String unit, Scanner scanner, P parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final LexemeFilter previousLexemeFilter = scanner.setLexemeFilter(this.getSkippedLexemes());
		try {
			return (E) this.getUnitByKey(unit).compile(this, scanner, parameter);
		} finally {
			scanner.setLexemeFilter(previousLexemeFilter);
		}
	}
}
