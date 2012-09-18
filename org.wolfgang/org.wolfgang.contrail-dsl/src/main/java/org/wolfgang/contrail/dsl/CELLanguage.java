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
	 * @throws EntryAlreadyBoundException 
	 */
	public CELLanguage() throws EntryAlreadyBoundException {
		super();
		this.addKeyword("import");
		this.addKeyword("function");
		this.addKeyword("var");
		
		this.addUnit(SourceUnit.class.getName(), new SourceUnit());
		this.addUnit(ImportsUnit.class.getName(), new ImportsUnit());
		this.addUnit(StatementsUnit.class.getName(), new StatementsUnit());
		this.addUnit(StatementUnit.class.getName(), new StatementUnit());
		this.addUnit(FlowExpressionUnit.class.getName(), new FlowExpressionUnit());
		this.addUnit(ExpressionUnit.class.getName(), new ExpressionUnit());
	}

	@Override
	public LexemeFilter getSkippedLexemes() {
		return LexemeFilter.SPACE_AND_COMMENT;
	}

}
