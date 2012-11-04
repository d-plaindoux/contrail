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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wolfgang.contrail.connection.Clients;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.Servers;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.model.Apply;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;
import org.wolfgang.contrail.ecosystem.lang.model.Function;
import org.wolfgang.contrail.ecosystem.lang.model.Reference;
import org.wolfgang.contrail.link.ComponentManager;

/**
 * <code>TestCodeValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestCodeValue {

	private static class TestContextFactory implements ContextFactory {
		@Override
		public ComponentManager getLinkManager() {
			return null;
		}

		@Override
		public ClassLoader getClassLoader() {
			return null;
		}

		@Override
		public Servers getServerFactory() {
			return null;
		}

		@Override
		public Clients getClientFactory() {
			return null;
		}
	}

	@Test
	public void testAtom() throws EcosystemCodeValueGeneratorException {
		final ContextFactory context = new TestContextFactory();
		final EcosystemSymbolTable factory = new EcosystemSymbolTableImpl();
		final EcosystemCodeValueGenerator ecosystemCompiler = new EcosystemCodeValueGenerator(context, factory);

		final Atom expression = new Atom();
		expression.setValue("Hello, World!");

		final CodeValue interpreted = ecosystemCompiler.visit(expression);

		assertEquals(ConstantValue.class, interpreted.getClass());
	}

	@Test
	public void testFunction() throws EcosystemCodeValueGeneratorException {
		final ContextFactory context = new TestContextFactory();
		final EcosystemSymbolTable factory = new EcosystemSymbolTableImpl();
		final EcosystemCodeValueGenerator ecosystemCompiler = new EcosystemCodeValueGenerator(context, factory);

		final Function expression = new Function();
		expression.add("var1");

		final Reference reference = new Reference();
		reference.setValue("var1");

		expression.add(reference);

		final CodeValue interpreted = ecosystemCompiler.visit(expression);

		assertEquals(ClosureValue.class, interpreted.getClass());
	}

	@Test
	public void testApply01() throws EcosystemCodeValueGeneratorException {
		final ContextFactory context = new TestContextFactory();
		final EcosystemSymbolTable factory = new EcosystemSymbolTableImpl();
		final EcosystemCodeValueGenerator ecosystemCompiler = new EcosystemCodeValueGenerator(context, factory);

		final Function expression = new Function();
		expression.add("var1");

		final Reference reference = new Reference();
		reference.setValue("var1");

		expression.add(reference);

		final Atom atom = new Atom();
		final String value = "Hello, World!";
		atom.setValue(value);

		final Apply apply = new Apply();
		apply.add(expression);
		apply.add(atom);

		final CodeValue interpreted = ecosystemCompiler.visit(apply);

		assertEquals(ConstantValue.class, interpreted.getClass());
		assertEquals(value, ((ConstantValue) interpreted).getValue());
	}

	@Test
	public void testApply02() throws EcosystemCodeValueGeneratorException {
		final ContextFactory context = new TestContextFactory();
		final EcosystemSymbolTable factory = new EcosystemSymbolTableImpl();
		final EcosystemCodeValueGenerator ecosystemCompiler = new EcosystemCodeValueGenerator(context, factory);

		final Function expression = new Function();
		expression.add("var0");
		expression.add("var1");

		final Reference reference1 = new Reference();
		reference1.setValue("var1");

		expression.add(reference1);

		final String value = "Hello, World!";
		final Atom atom1 = new Atom();
		atom1.setValue(value);

		final Atom atom2 = new Atom();
		atom2.setValue("unbound");

		final Apply apply1 = new Apply();
		apply1.setBinding("var1");
		apply1.add(expression);
		apply1.add(atom1);

		final CodeValue interpreted1 = ecosystemCompiler.visit(apply1);

		assertEquals(ClosureValue.class, interpreted1.getClass());
	}

	@Test
	public void testApply03() throws EcosystemCodeValueGeneratorException {
		final ContextFactory context = new TestContextFactory();
		final EcosystemSymbolTable factory = new EcosystemSymbolTableImpl();
		final EcosystemCodeValueGenerator ecosystemCompiler = new EcosystemCodeValueGenerator(context, factory);

		final Function expression = new Function();
		expression.add("var0");
		expression.add("var1");

		final Reference reference1 = new Reference();
		reference1.setValue("var1");

		expression.add(reference1);

		final String value = "Hello, World!";
		final Atom atom1 = new Atom();
		atom1.setValue(value);

		final Atom atom2 = new Atom();
		atom2.setValue("unbound");

		final Apply apply1 = new Apply();
		apply1.setBinding("var1");
		apply1.add(expression);
		apply1.add(atom1);

		final Apply apply2 = new Apply();
		apply2.setBinding("var0");
		apply2.add(apply1);
		apply2.add(atom2);

		final CodeValue interpreted2 = ecosystemCompiler.visit(apply2);

		assertEquals(ConstantValue.class, interpreted2.getClass());
		assertEquals(value, ((ConstantValue) interpreted2).getValue());
	}

	@Test
	public void testApply04() throws EcosystemCodeValueGeneratorException {
		final ContextFactory context = new TestContextFactory();
		final EcosystemSymbolTable factory = new EcosystemSymbolTableImpl();
		final EcosystemCodeValueGenerator ecosystemCompiler = new EcosystemCodeValueGenerator(context, factory);

		final Function expression = new Function();
		expression.add("var0");
		expression.add("var1");

		final Reference reference1 = new Reference();
		reference1.setValue("var1");

		expression.add(reference1);

		final Atom atom1 = new Atom();
		atom1.setValue("unbound");

		final String value = "Hello, World!";
		final Atom atom2 = new Atom();
		atom2.setValue(value);

		final Apply apply1 = new Apply();
		apply1.add(expression);
		apply1.add(atom1);

		final Apply apply2 = new Apply();
		apply2.add(apply1);
		apply2.add(atom2);

		final CodeValue interpreted2 = ecosystemCompiler.visit(apply2);

		assertEquals(ConstantValue.class, interpreted2.getClass());
		assertEquals(value, ((ConstantValue) interpreted2).getValue());
	}

	@Test
	public void testApply05() throws EcosystemCodeValueGeneratorException {
		final ContextFactory context = new TestContextFactory();
		final EcosystemSymbolTable factory = new EcosystemSymbolTableImpl();
		final EcosystemCodeValueGenerator ecosystemCompiler = new EcosystemCodeValueGenerator(context, factory);

		final Function expression = new Function();
		expression.add("var0");
		expression.add("var1");

		final Reference reference1 = new Reference();
		reference1.setValue("var1");

		expression.add(reference1);

		final Atom atom1 = new Atom();
		atom1.setValue("unbound");

		final String value = "Hello, World!";
		final Atom atom2 = new Atom();
		atom2.setValue(value);

		final Apply apply = new Apply();
		apply.add(expression);
		apply.add(atom1);
		apply.add(atom2);

		final CodeValue interpreted = ecosystemCompiler.visit(apply);

		assertEquals(ConstantValue.class, interpreted.getClass());
		assertEquals(value, ((ConstantValue) interpreted).getValue());
	}
}
