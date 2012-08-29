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

package org.wolfgang.common.lang;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * <code>TestTypeUtils</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestTypeUtils extends TestCase {

	@Test
	public void testNominal01() {
		try {
			assertEquals(String.class, TypeUtils.getType("java.lang.String"));
		} catch (ClassNotFoundException e) {
			fail();
		}
	}

	@Test
	public void testNominal02() {
		try {
			assertEquals(String.class, TypeUtils.getType("String"));
		} catch (ClassNotFoundException e) {
			fail();
		}
	}

	@Test
	public void testNominal03() {
		try {
			assertEquals(byte[].class, TypeUtils.getType("byte[]"));
		} catch (ClassNotFoundException e) {
			fail();
		}
	}

	@Test
	public void testNominal04() {
		try {
			assertEquals(long.class, TypeUtils.getType("long"));
		} catch (ClassNotFoundException e) {
			fail();
		}
	}

	@Test
	public void testFailure01() {
		try {
			TypeUtils.getType("titi");
			fail();
		} catch (ClassNotFoundException e) {
			// OK
		}
	}
}
