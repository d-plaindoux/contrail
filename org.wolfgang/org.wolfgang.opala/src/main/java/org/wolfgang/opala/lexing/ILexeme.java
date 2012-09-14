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

package org.wolfgang.opala.lexing;

import java.util.regex.Pattern;

/**
 * A ILexeme provides a set of information related to the location, the kind and the value for a given token.
 *
 * @see org.wolfgang.opala.scanner.IScanner
 */

public interface ILexeme {

    /**
     * Provide the location where this lexeme come from. This information was provided for tracability purpose.
     *
     * @return the location which cannot be <code>null</code>.
     */
    ILocation getLocation();

    /**
     * Each lexeme is linked to a basic kind for classification exposed in {@link LexemeKind ).
     *
     * @return the kind associated to this lexeme
     */
    LexemeKind getKind();

    /**
     * A lexeme can have a value. Then this method provides such value which ca be null under certain circumstance.
     *
     * @return a string value or <code>null</code>
     */
    String getValue();

    /**
     * Predicate checking the kind of the current lexeme.
     *
     * @param kind The kind to be checked
     * @return true if the kind is the one linked to this lexeme; false otherwise
     */
    boolean isA(LexemeKind kind);

    /**
     * Predicate checking the value of the current lexeme. The parameter can be null and the test still valid.
     *
     * @param value The value to be checked
     * @return true if both are equal or <code>null</code>; false otherwise
     */
    boolean isA(String value);

    /**
     * Predicate checking both kind and value of the current lexeme
     *
     * @param kind  The kind to be checked
     * @param value The value to be checked
     * @return true if the kind and the value are the same as the ones linked to this lexeme.
     * @see #isA(LexemeKind)
     * @see #isA(String)
     */
    boolean isA(LexemeKind kind, String value);

    /**
     * Predicate checking the value of the current lexeme. The parameter can be null and the test still valid.
     *
     * @param value The value to be checked
     * @return true if both are equal or <code>null</code>; false otherwise
     */
    boolean isA(Pattern value);

    /**
     * Predicate checking both kind and value of the current lexeme
     *
     * @param kind  The kind to be checked
     * @param value The value to be checked
     * @return true if the kind and the value are the same as the ones linked to this lexeme.
     * @see #isA(LexemeKind)
     * @see #isA(String)
     */
    boolean isA(LexemeKind kind, Pattern value);
}
