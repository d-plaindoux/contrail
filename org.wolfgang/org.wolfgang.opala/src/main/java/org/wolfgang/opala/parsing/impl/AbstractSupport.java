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

import org.wolfgang.opala.parsing.ICompilationUnit;
import org.wolfgang.opala.parsing.ILanguageSupport;
import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.exception.CastException;
import org.wolfgang.opala.utils.Cast;

/**
 * <code>AbstractSupport</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
abstract public class AbstractSupport implements ILanguageSupport {

	private final Stack<String> context;
	private final List<String> keywords;
	private final Map<String, ICompilationUnit<?, ?>> units;

	{
		this.context = new Stack<String>();
		this.units = new HashMap<String, ICompilationUnit<?, ?>>();
		this.keywords = new ArrayList<String>();
	}

	protected void addKeyword(String keyword) {
		this.keywords.add(keyword);
	}

	public boolean isKeyword(String keyword) {
		return this.keywords.contains(keyword);
	}

	protected <E, P> void addUnit(String key, ICompilationUnit<E, P> unit) throws EntryAlreadyBoundException {
		if (this.units.get(key) == null) {
			this.units.put(key, new CompilationUnitTracker<E, P>(key, unit));
		} else {
			throw new EntryAlreadyBoundException(key);
		}
	}

	public <E, P> ICompilationUnit<E, P> getUnitByKey(String key) throws ParsingUnitNotFound {
		try {
			final ICompilationUnit<E, P> unit = new Cast<ICompilationUnit<E, P>>().perform(this.units.get(key));
			if (unit != null) {
				return unit;
			} else {
				throw new ParsingUnitNotFound(key);
			}
		} catch (CastException e) {
			throw new ParsingUnitNotFound(e);
		}
	}

	public void enterUnit(String key) {
		this.context.push(key);
	}

	public void exitUnit(String key) {
		while (key.equals(this.context.pop()) == false)
			;
	}

	public String[] getContext() {
		return this.context.toArray(new String[this.context.size()]);
	}
}
