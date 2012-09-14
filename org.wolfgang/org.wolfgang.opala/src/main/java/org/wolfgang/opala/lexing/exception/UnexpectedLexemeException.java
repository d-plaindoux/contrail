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

package org.wolfgang.opala.lexing.exception;

import org.wolfgang.opala.lexing.ILexeme;

public class UnexpectedLexemeException extends LexemeNotFoundException {
    private static final long serialVersionUID = -214362872107039294L;

    final private String[] context;

    public UnexpectedLexemeException(String[] context, ILexeme lexeme) {
        super(lexeme);
        this.context = context;
    }

    public String[] getContext() {
        return context;
    }
}