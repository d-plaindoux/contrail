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

import org.wolfgang.contrail.ecosystem.lang.model.Apply;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;
import org.wolfgang.contrail.ecosystem.lang.model.Definition;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.ExpressionVisitor;
import org.wolfgang.contrail.ecosystem.lang.model.Flow;
import org.wolfgang.contrail.ecosystem.lang.model.Function;
import org.wolfgang.contrail.ecosystem.lang.model.ModelFactory;
import org.wolfgang.contrail.ecosystem.lang.model.Reference;
import org.wolfgang.contrail.ecosystem.lang.model.Router;
import org.wolfgang.contrail.ecosystem.lang.model.Sequence;
import org.wolfgang.contrail.ecosystem.lang.model.Switch;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.parsing.CompilationUnit;
import org.wolfgang.opala.parsing.LanguageSupport;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.exception.ScannerException;

public class ToplevelUnit implements CompilationUnit<Void, EcosystemModel>, ExpressionVisitor<Definition, Exception> {

	@Override
	public Void compile(LanguageSupport support, Scanner scanner, EcosystemModel ecosystemModel) throws ScannerException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		while (!scanner.isFinished()) {
			final Expression expression = support.getUnitByKey(StatementUnit.class).compile(support, scanner, ecosystemModel);
			try {
				ecosystemModel.add(expression.visit(this));
			} catch (Exception e) {
				throw new ParsingException(e);
			}
		}

		return null;
	}

	@Override
	public Definition visit(Reference expression) throws Exception {
		return ModelFactory.define(null, expression);
	}

	@Override
	public Definition visit(Atom expression) throws Exception {
		return ModelFactory.define(null, expression);
	}

	@Override
	public Definition visit(Apply expression) throws Exception {
		return ModelFactory.define(null, expression);
	}

	@Override
	public Definition visit(Flow expression) throws Exception {
		return ModelFactory.define(null, expression);
	}

	@Override
	public Definition visit(Function expression) throws Exception {
		// Unreachable
		return ModelFactory.define(null, expression);
	}

	@Override
	public Definition visit(Router expression) throws Exception {
		return ModelFactory.define(null, expression);
	}

	@Override
	public Definition visit(Switch expression) throws Exception {
		return ModelFactory.define(null, expression);
	}

	public Definition visit(Definition definition) throws Exception {
		return definition;
	}

	@Override
	public Definition visit(Sequence sequence) throws Exception {
		return ModelFactory.define(null, sequence);
	}
}