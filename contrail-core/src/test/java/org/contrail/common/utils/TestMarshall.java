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

package org.contrail.common.utils;

import static org.junit.Assert.assertEquals;

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
	public void GivenAnNumberUnmarshallMarshallMustBeIdentity() throws IOException {
		final byte[] marshalled = Marshall.numberToBytes(12);
		assertEquals(4, marshalled.length);
		assertEquals(12, Marshall.bytesToNumber(marshalled));
	}

	@Test(expected = IOException.class)
	public void GivenAnArrayOfIntegerUnmarshallMarshallCannotBeAnNumber() throws IOException {
		Marshall.bytesToNumber(new byte[] { 1, 2, 3 });
	}

	@Test
	public void GivenAShortNumberUnmarshallMarshallMustBeIdentity() throws IOException {
		final byte[] marshalled = Marshall.shortNumberToBytes(12);
		assertEquals(2, marshalled.length);
		assertEquals(12, Marshall.bytesToShortNumber(marshalled));
	}

	@Test(expected = IOException.class)
	public void GivenAnArrayOfIntegerUnmarshallMarshallCannotBeAnShortNumber() throws IOException {
		Marshall.bytesToShortNumber(new byte[] { 1 });
	}

	@Test
	public void GivenACharUnmarshallMarshallMustBeIdentity() throws IOException {
		final byte[] marshalled = Marshall.charToBytes('a');
		assertEquals(2, marshalled.length);
		assertEquals('a', Marshall.bytesToChar(marshalled));
	}

	@Test(expected = IOException.class)
	public void GivenAnArrayOfIntegerUnmarshallMarshallCannotBeAChar() throws IOException {
		Marshall.bytesToShortNumber(new byte[] { 1 });
	}

	@Test
	public void GivenAStringUnmarshallMarshallMustBeIdentity() throws IOException {
		final String string = "Hello, World!";
		final byte[] marshalled = Marshall.stringToBytes(string);
		assertEquals(string.length() * Marshall.SIZE_Character, marshalled.length);
		assertEquals(string, Marshall.bytesToString(marshalled, string.length()));
	}

	@Test(expected = IOException.class)
	public void GivenAnArrayOfIntegerUnmarshallMarshallCannotBeAString() throws IOException {
		Marshall.bytesToString(new byte[] { 1, 2, 3, 4 }, 5);
	}

	@Test
	public void GivenAnNumberUnmarshallWithOffsetMarshallAsAShortNumber() throws IOException {
		final byte[] marshalled = Marshall.numberToBytes(12);
		assertEquals(4, marshalled.length);
		assertEquals(12, Marshall.bytesToShortNumberWithOffset(marshalled, 2));
	}

	@Test
	public void GivenAnNumberUnmarshallWithOffsetMarshallAsAChar() throws IOException {
		final byte[] marshalled = Marshall.numberToBytes(32);
		assertEquals(4, marshalled.length);
		assertEquals(' ', Marshall.bytesToCharWithOffset(marshalled, 2));
	}

	@Test
	public void GivenAStringUnmarshallWithOffsetMarshallAsNumber() throws IOException {
		final String string = "Hello,\0 World!";
		final byte[] marshalled = Marshall.stringToBytes(string);
		assertEquals(string.length() * Marshall.SIZE_Character, marshalled.length);
		assertEquals(' ' /* 32 */, Marshall.bytesToNumberWithOffset(marshalled, 12));
	}

	@Test
	public void GivenAStringUnmarshallWithOffsetMarshallAsString() throws IOException {
		final String string = "Hello, World!";
		final byte[] marshalled = Marshall.stringToBytes(string);
		assertEquals(string.length() * Marshall.SIZE_Character, marshalled.length);
		assertEquals("World", Marshall.bytesToStringWithOffset(marshalled, 14, 5));
	}
}
