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

package org.wolfgang.java.classes;

import java.lang.reflect.Array;

import junit.framework.TestCase;

/**
 * <code>TestTypes</code>
 * 
 * @author Didier Plaindoux
 * 
 */
public class TestTypes extends TestCase {

	public void testSingleType() {
		assertEquals(Byte.TYPE.getName(), TypeDecoder.getType("B"));
		assertEquals(Character.TYPE.getName(), TypeDecoder.getType("C"));
		assertEquals(Double.TYPE.getName(), TypeDecoder.getType("D"));
		assertEquals(Float.TYPE.getName(), TypeDecoder.getType("F"));
		assertEquals(Integer.TYPE.getName(), TypeDecoder.getType("I"));
		assertEquals(Long.TYPE.getName(), TypeDecoder.getType("J"));
		assertEquals(Short.TYPE.getName(), TypeDecoder.getType("S"));
		assertEquals(Void.TYPE.getName(), TypeDecoder.getType("V"));
		assertEquals(Boolean.TYPE.getName(), TypeDecoder.getType("Z"));
		assertEquals("foo.bar.Baz", TypeDecoder.getType("Lfoo/bar/Baz;"));
		assertEquals(Array.class.getName(), TypeDecoder.getType("[Lfoo/bar/Baz;"));
	}

	public void testMethodType() {
		final String type = "(Lfoo/Bar$1;ILfoo/Baz$2;)V";
		assertEquals(Void.TYPE.getName(), TypeDecoder.getMethodResult(type));
		final String[] expected = new String[] { "foo.Bar$1", Integer.TYPE.getName(), "foo.Baz$2" };
		final String[] provided = TypeDecoder.getMethodParameters(type);
		assertEquals("Indices", expected.length, provided.length);
		for (int i = 0; i < Math.min(expected.length, provided.length); i++) {
			assertEquals("Item", expected[i], provided[i]);
		}
	}
}
