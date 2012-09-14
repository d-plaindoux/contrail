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

import org.wolfgang.opala.scanner.exception.ScannerException;

public interface LexemeTokenizer {

    /**
     * Provide the next available lexeme. If no one is available a scanner exception is thrown
     *
     * @return a new lexeme which cannot be null
     * @throws ScannerException Thrown when no more lexeme can be read
     */

    Lexeme getNextLexeme() throws ScannerException;

    /**
     * Method used when scanner can be disposed by the system.
     *
     * @throws ScannerException Thrown when the finish cannot be normally performed.
     */
    void finish() throws ScannerException;
}
