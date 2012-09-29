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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

/**
 * <code>TestMarshall</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestMarshall {

	@Test
	public void testInteger() throws IOException {
		assertEquals(12, Marshall.bytesToInt(Marshall.intToBytes(12)));
	}

	@Test
	public void testIntegerFailure() {
		try {
			assertEquals(12, Marshall.bytesToInt(new byte[] { 1, 2, 3 }));
			fail();
		} catch (IOException e) {
			// OK
		}
	}

	@Test
	public void testObject() throws IOException, ClassNotFoundException {
		assertEquals("Hello, World!", Marshall.bytesToObject(Marshall.objectToBytes("Hello, World!")));
	}

}
