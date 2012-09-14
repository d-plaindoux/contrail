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

import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.lexing.exception.UnexpectedLexemeException;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;



public class CompilationUnitObserver<E, P> implements CompilationUnit<E, P> {
    private final String compilationKey;
    private final CompilationUnit<E, P> compilationUnit;

    public CompilationUnitObserver(String compilationKey, CompilationUnit<E, P> unit) {
        this.compilationKey = compilationKey;
        this.compilationUnit = unit;
    }

    public E compile(LanguageSupport support, Scanner scanner, P parameter) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
        support.enterUnit(compilationKey);
        try {
            return compilationUnit.compile(support, scanner, parameter);
        } catch (UnexpectedLexemeException e) {
            throw e;
        } catch (LexemeNotFoundException e) {
            final UnexpectedLexemeException unexpectedLexemeException = new UnexpectedLexemeException(support.getContext(), e.getLexeme());
            unexpectedLexemeException.setStackTrace(e.getStackTrace());
            throw unexpectedLexemeException;
        } finally {
            support.exitUnit(compilationKey);
        }
    }
}
