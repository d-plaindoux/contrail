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

package org.wolfgang.contrail.dsl;

import org.wolfgang.opala.parsing.exception.EntryAlreadyBoundException;
import org.wolfgang.opala.parsing.impl.AbstractLanguageSupport;
import org.wolfgang.opala.scanner.LexemeFilter;

/**
 * <code>CELLanguage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CELLanguage extends AbstractLanguageSupport {

	/**
	 * Constructor
	 * 
	 * @throws EntryAlreadyBoundException
	 */
	public CELLanguage() {
		super();
		this.addKeyword("import");
		this.addKeyword("fun");
		this.addKeyword("var");

		try {
			this.addUnit(new SourceUnit());

			// Toplevel imports
			this.addUnit(new ImportsUnit());
			this.addUnit(new ImportUnit());

			// Toplevel statements
			this.addUnit(new ToplevelUnit());
			this.addUnit(new VariableUnit());
			this.addUnit(new StatementUnit());
			this.addUnit(new FlowStatementUnit());

			// Flow / Choice or Application
			this.addUnit(new FlowExpressionUnit());
			this.addUnit(new ChoiceExpressionUnit());
			this.addUnit(new ExpressionUnit());

			// Simple expression
			this.addUnit(new SimpleExpressionUnit());
			this.addUnit(new BlockUnit());

			// Simple expressions
			this.addUnit(new AbstractionUnit());
			this.addUnit(new UnitOrFlowExpressionUnit());
			this.addUnit(new TerminalUnit.Variable());
			this.addUnit(new TerminalUnit.String());
			this.addUnit(new TerminalUnit.Integer());
			this.addUnit(new TerminalUnit.Character());

		} catch (EntryAlreadyBoundException consume) {
			// Ignore
		}
	}

	@Override
	public LexemeFilter getSkippedLexemes() {
		return LexemeFilter.SPACE_AND_COMMENT;
	}

}
