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

import java.io.IOException;

/**
 * The <code>Marshall</code> module is in charge of object to/from bytes
 * transformations.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class Marshall {

	// Types

	public static final byte TYPE_Array = 0x1;
	public static final byte TYPE_Object = 0x2;
	public static final byte TYPE_Character = 0x3;
	public static final byte TYPE_String = 0x4;
	public static final byte TYPE_BooleanTrue = 0x5;
	public static final byte TYPE_BooleanFalse = 0x6;
	public static final byte TYPE_Undefined = 0x7;
	public static final byte TYPE_Null = 0x8;
	
	public static final byte TYPE_ShortNumber = 0x9;
	public static final byte TYPE_Number = 0x10;
	public static final byte TYPE_Float = 0x11;

	// Sizes

	public static final byte SIZE_Character = 2;
	public static final byte SIZE_Number = 4;
	public static final byte SIZE_ShortNumber = 2;
	public static final byte SIZE_Boolean = 1;

	/**
	 * Constructor
	 */
	private Marshall() {
		super();
	}

	// -------------------------------------------------------------------------------------------------------------
	// Decoding ...
	// -------------------------------------------------------------------------------------------------------------

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static int bytesToNumberWithOffset(byte[] bytes, int offset) throws IOException {
		if (bytes.length < offset + SIZE_Number) {
			throw new IOException();
		}

		return bytes[offset] << 24 | bytes[offset + 1] << 16 | bytes[offset + 2] << 8 | (bytes[offset + 3] & 0xFF);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static int bytesToNumber(byte[] bytes) throws IOException {
		return bytesToNumberWithOffset(bytes, 0);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static float bytesToFloatWithOffset(byte[] bytes, int offset) throws IOException {
		return Float.intBitsToFloat(bytesToNumberWithOffset(bytes, offset));
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static float bytesToFloat(byte[] bytes) throws IOException {
		return bytesToFloatWithOffset(bytes, 0);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static int bytesToShortNumberWithOffset(byte[] bytes, int offset) throws IOException {
		if (bytes.length < offset + SIZE_ShortNumber) {
			throw new IOException();
		}

		return bytes[offset] << 8 | (bytes[offset + 1] & 0xFF);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static int bytesToShortNumber(byte[] bytes) throws IOException {
		return bytesToShortNumberWithOffset(bytes, 0);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static char bytesToCharWithOffset(byte[] bytes, int offset) throws IOException {
		if (bytes.length < offset + SIZE_Character) {
			throw new IOException();
		}

		return (char) (bytes[offset] << 8 | (bytes[offset + 1] & 0xFF));
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static char bytesToChar(byte[] bytes) throws IOException {
		return (char) bytesToCharWithOffset(bytes, 0);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static String bytesToStringWithOffset(byte[] bytes, int offset, int length) throws IOException {
		if (bytes.length < offset + length * SIZE_Character) {
			throw new IOException();
		}

		final char[] chars = new char[length];

		for (int i = 0; i < length; i += 1) {
			chars[i] = bytesToCharWithOffset(bytes, i * SIZE_Character + offset);
		}

		return String.valueOf(chars);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static String bytesToString(byte[] bytes, int length) throws IOException {
		return bytesToStringWithOffset(bytes, 0, length);
	}

	/**
	 * Convert an array of bytes to an integer
	 * 
	 * @param bytes
	 *            The source
	 * @param offset
	 *            The initial position
	 * @return {*}
	 * @throws IOException
	 */
	public static String charsToString(byte[] bytes) {
		final char[] chars = new char[bytes.length];

		for (int i = 0; i < bytes.length; i += 1) {
			chars[i] = (char)(bytes[i] & 0xFF);
		}

		return String.valueOf(chars);
	}
	
	// Encoding

	/**
	 * Method called to encode an integer
	 * 
	 * @param i
	 *            The integer to encode
	 * @return the encoding result
	 * @throws IOException
	 */
	public static byte[] numberToBytes(int i) {
		return new byte[] { (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i };
	}

	/**
	 * Method called to encode an integer
	 * 
	 * @param i
	 *            The float to encode
	 * @return the encoding result
	 * @throws IOException
	 */
	public static byte[] floatToBytes(float i) {
		return numberToBytes(Float.floatToIntBits(i));
	}

	/**
	 * Method called to encode an integer
	 * 
	 * @param i
	 *            The int to encode
	 * @return the encoding result
	 * @throws IOException
	 */
	public static byte[] shortNumberToBytes(int i) {
		return new byte[] { (byte) (i >>> 8), (byte) i };
	}

	/**
	 * Method called to encode an integer
	 * 
	 * @param i
	 *            The char to encode
	 * @return the encoding result
	 * @throws IOException
	 */
	public static byte[] charToBytes(char c) {
		return new byte[] { (byte) (c >>> 8), (byte) c };
	}

	/**
	 * Method called to encode an integer
	 * 
	 * @param i
	 *            The char to encode
	 * @return the encoding result
	 * @throws IOException
	 */
	public static byte[] stringToBytes(String s) {
		byte[] bytes = new byte[s.length() * SIZE_Character];

		for (int i = 0; i < s.length(); i += 1) {
			final byte[] charToBytes = charToBytes(s.charAt(i));
			bytes[i * SIZE_Character] = charToBytes[0];
			bytes[i * SIZE_Character + 1] = charToBytes[1];
		}

		return bytes;
	}

	/**
	 * @param source
	 * @return
	 */
	public static byte[] stringToChars(String source) {
		return source.getBytes();
	}
}
