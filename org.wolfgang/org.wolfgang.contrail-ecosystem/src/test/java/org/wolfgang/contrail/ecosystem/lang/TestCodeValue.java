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

package org.wolfgang.contrail.ecosystem.lang;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;

/**
 * <code>TestCodeValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestCodeValue extends TestCase {

	private static class TestSymbolTable implements EcosystemSymbolTable {

		@Override
		public boolean hasImportation(String name) {
			return false;
		}

		@Override
		public EcosystemImportation<?> getImportation(String name) {
			return null;
		}

	}

	public void testAtom() throws CannotCreateComponentException {
		final Map<String, CodeValue> environment = new HashMap<String, CodeValue>();
		final EcosystemSymbolTable factory = new TestSymbolTable();
		final EcosystemCompiler ecosystemCompiler = new EcosystemCompiler(factory, environment);
		
		final Atom expression = new Atom();
		expression.setValue("Hello, World!");
		
		final CodeValue visit = ecosystemCompiler.visit(expression);
	}
}
