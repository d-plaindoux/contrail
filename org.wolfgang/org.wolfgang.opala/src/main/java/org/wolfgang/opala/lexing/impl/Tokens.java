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


import java.lang.reflect.Field;

import org.wolfgang.opala.lexing.LexemeKind;

/**
 * Tokens definition used for by lexing stage when tokens are recognized and produced.
 */

public class Tokens {

    //
    // Static Attributes defnition
    //

    public static final int _EOF_ = -1;
    public static final int _SPACES_ = _EOF_ + 1;
    public static final int _EOL_ = _SPACES_ + 1;
    public static final int _OPERATOR_ = _EOL_ + 1;
    public static final int _STRING_ = _OPERATOR_ + 1;
    public static final int _CHARACTERS_ = _STRING_ + 1;
    public static final int _INTEGER_ = _CHARACTERS_ + 1;
    public static final int _IDENT_ = _INTEGER_ + 1;
    public static final int _COMMENT_ = _IDENT_ + 1;
    public static final int _SPECIAL_ = _COMMENT_ + 1;
    public static final int _ERROR_ = _SPECIAL_ + 1;

    //
    // Static enum reflecting attributes
    //

    public static LexemeKind getKind(int k) {
        switch (k) {
            case _EOF_:
                return LexemeKind.EOF;
            case _SPACES_:
                return LexemeKind.SPACES;
            case _EOL_:
                return LexemeKind.EOL;
            case _OPERATOR_:
                return LexemeKind.OPERATOR;
            case _STRING_:
                return LexemeKind.STRING;
            case _CHARACTERS_:
                return LexemeKind.CHARACTERS;
            case _INTEGER_:
                return LexemeKind.INTEGER;
            case _IDENT_:
                return LexemeKind.IDENT;
            case _COMMENT_:
                return LexemeKind.COMMENT;
            case _SPECIAL_:
                return LexemeKind.SPECIAL;
            case _ERROR_:
                return LexemeKind.ERROR;
        }

        throw new IllegalArgumentException("unable to find kind related to value [" + k + "]");
    }

    /**
     * Provides a string representation of a given token value. The name came from the name of the corresponding token
     * value. If no definition has been found "UNDEF" is returned.
     *
     * @param status to be externalized as a string
     * @return string representation is defined; otherwise "UNDEF" is returned.
     */

    public static String toString(int status) {
        Field[] fields = Tokens.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                if (field.getType().equals(Integer.TYPE) && field.getInt(null) == status) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                // IGNORE
            }
        }

        return "UNDEF";
    }
}
