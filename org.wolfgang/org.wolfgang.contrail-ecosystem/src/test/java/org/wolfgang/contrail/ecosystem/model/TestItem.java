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

package org.wolfgang.contrail.ecosystem.model;

import org.wolfgang.contrail.ecosystem.model.Flow.Item;

import junit.framework.TestCase;

/**
 * <code>TestItem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestItem extends TestCase {

	public void testItem01() {
		final Item[] decompose = Flow.decompose("A");
		assertEquals(1, decompose.length);
		assertFalse(decompose[0].asAlias());
		assertEquals("A", decompose[0].getName());
		assertEquals(0, decompose[0].getParameters().length);
	}

	public void testItem02() {
		final Item[] decompose = Flow.decompose("A B");
		assertEquals(2, decompose.length);
		assertFalse(decompose[0].asAlias());
		assertEquals("A", decompose[0].getName());
		assertEquals(0, decompose[0].getParameters().length);
		assertFalse(decompose[1].asAlias());
		assertEquals("B", decompose[1].getName());
		assertEquals(0, decompose[1].getParameters().length);
	}
	
	public void testItem03() {
		final Item[] decompose = Flow.decompose("a=A");
		assertEquals(1, decompose.length);
		assertTrue(decompose[0].asAlias());
		assertEquals("a", decompose[0].getAlias());
		assertEquals("A", decompose[0].getName());
		assertEquals(0, decompose[0].getParameters().length);
	}	

	public void testItem04() {
		final Item[] decompose = Flow.decompose("a=A()");
		assertEquals(1, decompose.length);
		assertTrue(decompose[0].asAlias());
		assertEquals("a", decompose[0].getAlias());
		assertEquals("A", decompose[0].getName());
		assertEquals(0, decompose[0].getParameters().length);
	}	

	public void testItem05() {
		final Item[] decompose = Flow.decompose("a=A(1)");
		assertEquals(1, decompose.length);
		assertTrue(decompose[0].asAlias());
		assertEquals("a", decompose[0].getAlias());
		assertEquals("A", decompose[0].getName());
		assertEquals(1, decompose[0].getParameters().length);
		assertEquals("1", decompose[0].getParameters()[0]);
	}	

	public void testItem06() {
		final Item[] decompose = Flow.decompose("a=A(1,dd)");
		assertEquals(1, decompose.length);
		assertTrue(decompose[0].asAlias());
		assertEquals("a", decompose[0].getAlias());
		assertEquals("A", decompose[0].getName());
		assertEquals(2, decompose[0].getParameters().length);
		assertEquals("1", decompose[0].getParameters()[0]);
		assertEquals("dd", decompose[0].getParameters()[1]);
	}	
}
