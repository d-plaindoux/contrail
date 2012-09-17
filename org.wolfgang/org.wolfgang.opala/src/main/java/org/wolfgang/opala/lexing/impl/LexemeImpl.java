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

import org.wolfgang.opala.lexing.Lexeme;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.Location;

/**
 * <code>LexemeImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class LexemeImpl implements Lexeme {

	private final Location location;
	private final LexemeKind kind;
	private final String value;

	/**
	 * Constructor
	 * 
	 * @param location
	 * @param kind
	 * @param value
	 */
	public LexemeImpl(Location location, LexemeKind kind, String value) {
		this.location = location;
		this.kind = kind;
		this.value = value;
	}

	/**
	 * Constructor
	 * 
	 * @param kind
	 * @param value
	 */
	public LexemeImpl(LexemeKind kind, String value) {
		this(null, kind, value);
	}

	/**
	 * @return the location (can be <code>null</code>)
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the kind
	 */
	public LexemeKind getKind() {
		return kind;
	}

	/**
	 * @return the value (can be <code>null</code>)
	 */
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
		return (this.value == null && value == null) || (this.value != null && this.value.equals(value));
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
