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

import org.wolfgang.opala.lexing.Lexeme;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.ScannerListener;

/**
 */

/**
 * The <code>CheckPointScanner</code> class provides backtrack facilities when
 * scanning the document.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CheckPointScanner implements ScannerListener {

	private final Scanner scanner;
	private final ScannerListener oldLexemeListener;
	private final List<Lexeme> scannedLexeme;

	public synchronized static CheckPointScanner newInstance(Scanner scanner) {
		return new CheckPointScanner(scanner);
	}

	{
		this.scannedLexeme = new ArrayList<Lexeme>();
	}

	private CheckPointScanner(Scanner scanner) {
		this.scanner = scanner;
		this.oldLexemeListener = this.scanner.setListener(this);
	}

	public void performScan(Lexeme lexeme) {
		this.scannedLexeme.add(0, lexeme);
	}

	public synchronized void commit() {
		for (int i = scannedLexeme.size() - 1; i > -1; i--) {
			this.oldLexemeListener.performScan(scannedLexeme.get(i));
		}

		this.scanner.setListener(this.oldLexemeListener);
	}

	public synchronized void rollback() {
		for (Lexeme lexeme : scannedLexeme) {
			this.scanner.rollback(lexeme);
		}

		this.scannedLexeme.clear();
		this.scanner.setListener(this.oldLexemeListener);
	}

	public synchronized List<Lexeme> getScannedLexeme() {
		List<Lexeme> scanned = new ArrayList<Lexeme>();
		for (Lexeme lexeme : this.scannedLexeme) {
			scanned.add(0, lexeme);
		}
		return scanned;
	}
}
