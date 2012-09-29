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

package org.wolfgang.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * <code>TestPair</code>
 * 
 * @author Didier Plaindoux
 * @verision 1.0
 */
public class TestPair {

	@Test
	public void testPair01() {
		final Pair<Integer, String> pair1 = new Pair<Integer, String>(1, "a");
		assertEquals(pair1, pair1);
		assertEquals(new Integer(1), pair1.getFirst());
		assertEquals("a", pair1.getSecond());
		assertFalse(pair1.equals(null));
		assertFalse(pair1.equals("a"));

		final Pair<Integer, String> pair2 = new Pair<Integer, String>(1, "a");
		assertEquals(pair1, pair2);
		assertEquals(pair1.hashCode(), pair2.hashCode());

		final Pair<Integer, String> pair3 = new Pair<Integer, String>(1, "b");
		assertNotSame(pair1, pair3);
	}
}
