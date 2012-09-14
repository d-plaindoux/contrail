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

import java.util.regex.Pattern;

import org.wolfgang.common.utils.Predicate;
import org.wolfgang.opala.lexing.ILexeme;
import org.wolfgang.opala.lexing.ILocation;
import org.wolfgang.opala.lexing.LexemeKind;

/**
 * <code>Lexeme</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Lexeme implements ILexeme {
	private ILocation location;
	private LexemeKind kind;
	private String value;

	public Lexeme(ILocation location, LexemeKind kind, String value) {
		this.location = location;
		this.kind = kind;
		this.value = value;
	}

	public Lexeme(LexemeKind kind, String value) {
		this(null, kind, value);
	}

	public ILocation getLocation() {
		return location;
	}

	public LexemeKind getKind() {
		return kind;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return kind + "[" + value + "]";
	}

	public boolean isA(LexemeKind kind) {
		return this.kind == kind;
	}

	public boolean isA(String value) {
		return Predicate.isNullOfEquals(this.value, value);
	}

	public boolean isA(LexemeKind kind, String value) {
		return isA(kind) && isA(value);
	}

	public boolean isA(Pattern value) {
		return (this.value == null && value == null) || (this.value != null && value.matcher(this.value).matches());
	}

	public boolean isA(LexemeKind kind, Pattern value) {
		return isA(kind) && isA(value);
	}
}
