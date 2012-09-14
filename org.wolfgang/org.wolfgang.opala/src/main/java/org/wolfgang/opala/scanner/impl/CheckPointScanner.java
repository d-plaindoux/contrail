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

package org.wolfgang.opala.scanner.impl;


import java.util.ArrayList;
import java.util.List;

import org.wolfgang.opala.lexing.ILexeme;
import org.wolfgang.opala.scanner.IScanner;
import org.wolfgang.opala.scanner.IScannerListener;

/**
 * This class provides backtrack facilities when scanning the document.
 */

public class CheckPointScanner implements IScannerListener {

    private IScanner scanner;
    private IScannerListener oldLexemeListener;
    private List<ILexeme> scannedLexeme;

    public synchronized static CheckPointScanner newInstance(IScanner scanner) {
        return new CheckPointScanner(scanner);
    }

    private CheckPointScanner(IScanner scanner) {
        this.scanner = scanner;
        this.scannedLexeme = new ArrayList<ILexeme>();
        this.oldLexemeListener = this.scanner.setListener(this);
    }

    public void scanPerformed(ILexeme lexeme) {
        this.scannedLexeme.add(0, lexeme);
    }

    public synchronized void commit() {
        if (this.oldLexemeListener != null) {
            for (int i = scannedLexeme.size() - 1; i > -1; i--) {
                this.oldLexemeListener.scanPerformed(scannedLexeme.get(i));
            }
        }

        this.scanner.setListener(this.oldLexemeListener);
    }

    public synchronized void rollback() {
        for (ILexeme lexeme : scannedLexeme) {
            this.scanner.rollback(lexeme);
        }

        this.scannedLexeme.clear();
        this.scanner.setListener(this.oldLexemeListener);
    }

    public synchronized List<ILexeme> getScannedLexeme() {
        List<ILexeme> scanned = new ArrayList<ILexeme>();
        for (ILexeme lexeme : this.scannedLexeme) {
            scanned.add(0, lexeme);
        }
        return scanned;
    }
}
