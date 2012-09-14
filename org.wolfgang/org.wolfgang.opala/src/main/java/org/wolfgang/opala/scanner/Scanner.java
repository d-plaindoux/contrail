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

package org.wolfgang.opala.scanner;

import org.wolfgang.opala.lexing.Lexeme;
import org.wolfgang.opala.lexing.LexemeTokenizer;
import org.wolfgang.opala.lexing.LexemeKind;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.scanner.exception.ScannerException;


/**
 * This interface defines the minimal set of functionality for scanners used by parsers. This covers lexeme analysis,
 * lexeme scan etc. ...
 *
 * @author D. Plaindoux
 */

public interface Scanner {

    /**
     * Method called to set the lexeme filter.
     *
     * @param filter The filter
     * @return
     */
    LexemeFilter setLexemeFilter(LexemeFilter filter);


    /**
     * @param listener
     * @return
     */
    ScannerListener setListener(ScannerListener listener);

    /**
     * @param tokenizer
     * @return
     */
    Scanner buildWith(LexemeTokenizer tokenizer) throws ScannerException;

    /**
     * Convenient method called when the scan will finish. It releases resources.
     *
     * @throws org.wolfgang.opala.scanner.exception.ScannerException
     *          scan exception when the finish cannot be normally performed
     */

    void finish() throws ScannerException;

    /**
     * Predicate which establish the end of the scan or not.
     *
     * @return true if the scanner has finished; false otherwise
     * @throws ScannerException
     */
    boolean isFinished() throws ScannerException;

    /**
     * Push a given lexeme on the top of the scanned token stream.
     *
     * @param lexeme The push-backed lexeme
     */

    void rollback(Lexeme lexeme);

    /**
     * Provide the current lexeme.
     *
     * @return the current lexeme to be analyzed.
     * @throws org.wolfgang.opala.scanner.exception.ScannerException
     *          thrown if the lexer is not able to provide the current lexeme
     */
    Lexeme currentLexeme() throws ScannerException;

    /**
     * Consume the current lexeme and read the next one. At this time the consumed lexeme is lost and cannot be
     * retreived by the system.
     *
     * @return the dropped lexeme.
     * @throws ScannerException Raised when the scanner is not able to consume the lexeme
     */
    Lexeme scan() throws ScannerException;

    /**
     * Consume the current lexeme if the conditions are verified. These conditions are the required kind and the
     * required value at the same time. If these conditions are verified the lexeme is consumed ({@link #scan()};
     * otherwise an exception {@link org.wolfgang.opala.lexing.exception.LexemeNotFoundException} is raised by the system.
     *
     * @param kind   kind required for the current lexeme
     * @param lexeme value required for the current lexem
     * @return the dropped lexeme
     * @throws org.wolfgang.opala.lexing.exception.LexemeNotFoundException
     *          Raised when at least one condition is not verified
     * @throws org.wolfgang.opala.scanner.exception.ScannerException
     *          Raised when the scanner is not able to consume the lexeme
     */
    Lexeme scan(LexemeKind kind, String lexeme) throws LexemeNotFoundException, ScannerException;

    /**
     * Consume the current lexeme if the condition is verified and returns the corresponding value. This conditions was
     * the required kind. If this condition is verified the lexeme is consumed ({@link #scan()}; otherwise an exception
     * {@link org.wolfgang.opala.lexing.exception.LexemeNotFoundException} is raised by the system.
     *
     * @param kind kind required for the current lexeme
     * @return the dropped lexeme
     * @throws LexemeNotFoundException Raised when the condition is not verified
     * @throws ScannerException        Raised when the scanner is not able to consume the lexeme
     */
    Lexeme scan(LexemeKind kind) throws LexemeNotFoundException, ScannerException;

    /**
     * Consume the current lexeme if the condition is verified and returns the corresponding kind. This conditions was
     * the required value. If this condition is verified the lexeme is consumed ({@link #scan()}; otherwise an exception
     * {@link org.wolfgang.opala.lexing.exception.LexemeNotFoundException} is raised by the system.
     *
     * @param lexeme value required for the current lexeme
     * @return the dropped lexeme
     * @throws LexemeNotFoundException Raised when the condition is not verified
     * @throws ScannerException        Raised when the scanner is not able to consume the lexeme
     */
    Lexeme scan(String lexeme) throws LexemeNotFoundException, ScannerException;
}
