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

import java.io.IOException;

public interface GenericLexer {

    /**
     * Provide the next lexeme type. If this lexeme type is <code>-1</code> the stream has been fully parsed.
     *
     * @return a integer denoting the lexeme type.
     * @throws java.io.IOException Thrown when lexeme can not be returned due to a stream error
     * @see org.wolfgang.opala.lexing.impl.Tokens#getKind(int)
     */
    int getLexemeType() throws IOException;

    /**
     * Provide the current lexeme value. If the type is <code>-1</code> the value is null.
     *
     * @return a string value or <code>null</code>
     */
    String getLexemeValue();

    /**
     * Provide the current cursor location
     *
     * @return a location.
     */
    Location getLocation();

    /**
     * This method will release resources used for the scanner.
     *
     * @throws java.io.IOException Thrown when finish operation cannot be done correctly
     */
    void finish() throws IOException;
}
