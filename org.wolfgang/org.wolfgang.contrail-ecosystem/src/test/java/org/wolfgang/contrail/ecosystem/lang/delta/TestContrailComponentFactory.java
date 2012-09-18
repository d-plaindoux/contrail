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

package org.wolfgang.contrail.ecosystem.lang.delta;

import java.lang.reflect.Method;
import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.connection.Clients;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.Servers;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
import org.wolfgang.contrail.ecosystem.annotation.ContrailLibrary;
import org.wolfgang.contrail.ecosystem.annotation.ContrailMethod;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>TestContrailComponentFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */

public class TestContrailComponentFactory extends TestCase {

	@ContrailLibrary
	public static class TestClass {
		private final String i;

		public TestClass() {
			this.i = "anon";
		}

		public TestClass(String a) {
			this.i = a;
		}

		@ContrailMethod
		public static TestClass add1(@ContrailArgument("a") String a) {
			return new TestClass(a);
		}

		@ContrailMethod
		public static TestClass add2(@ContrailArgument("a") String a, @ContrailArgument("b") String b) {
			return new TestClass(a + b);
		}
	}

	@Test
	public void testDefinitions() {
		final Method[] declaredMethods = LibraryBuilder.getDeclaredMethods(null, TestClass.class);

		assertEquals(2, declaredMethods.length);
		assertEquals(2, declaredMethods[0].getParameterTypes().length);
		assertEquals(1, declaredMethods[1].getParameterTypes().length);
	}

	@SuppressWarnings("serial")
	@Test
	public void testMethod01() throws CannotCreateComponentException {
		final TestClass test = LibraryBuilder.create("add1", new ContextFactory() {
			@Override
			public Servers getServerFactory() {
				return null;
			}

			@Override
			public Clients getClientFactory() {
				return null;
			}

			@Override
			public ClassLoader getClassLoader() {
				return null;
			}

			@Override
			public ComponentLinkManager getLinkManager() {
				return null;
			}
		}, TestClass.class, new HashMap<String, CodeValue>() {
			{
			}
		});

		assertEquals("anon", test.i);
	}

	@SuppressWarnings("serial")
	@Test
	public void testConstructor02() throws CannotCreateComponentException {
		final TestClass test = LibraryBuilder.create("add2", new ContextFactory() {
			@Override
			public Servers getServerFactory() {
				return null;
			}

			@Override
			public Clients getClientFactory() {
				return null;
			}

			@Override
			public ClassLoader getClassLoader() {
				return null;
			}

			@Override
			public ComponentLinkManager getLinkManager() {
				return null;
			}
		}, TestClass.class, new HashMap<String, CodeValue>() {
			{
				this.put("a", new ConstantValue("Hello"));
			}
		});

		assertEquals("Hello", test.i);
	}
}
