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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Didier Plaindoux
 * @version 1.0
 * 
 */
public class TestCoercion extends TestCase {

	@Test
	public void testNominal01() {
		final Object object = new Integer(1);
		assertTrue(Coercion.canCoerce(object, Integer.class));
		try {
			Coercion.coerce(object, Integer.class);
		} catch (ClassCastException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testNominal02() {
		final Object object = new Integer(1);
		assertTrue(Coercion.canCoerce(object, Number.class));
		try {
			Coercion.coerce(object, Number.class);
		} catch (ClassCastException e) {
			fail(e.getMessage());
		}
	}

}
