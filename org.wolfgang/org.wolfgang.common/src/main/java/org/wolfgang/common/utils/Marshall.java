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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.wolfgang.common.message.MessagesProvider;

/**
 * The <code>Marshall</code> module is in charge of object to/from bytes
 * transformations.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class Marshall {

	private static final int INT_LENGTH = 4;

	private static final int BYTES_3 = 24;
	private static final int BYTES_2 = 16;
	private static final int BYTES_1 = 8;

	private static final int BYTE_MASK = 0xFF;

	/**
	 * Constructor
	 */
	private Marshall() {
		super();
	}

	/**
	 * Method called whether an object must be translated to a byte array
	 * 
	 * @param object
	 *            The object to externalize
	 * @return a byte array
	 * @throws IOException
	 *             Thrown if the externalization fails
	 */
	public static byte[] objectToBytes(final Object object) throws IOException {
		final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		final ObjectOutputStream outputStream = new ObjectOutputStream(arrayOutputStream);
		try {
			outputStream.writeObject(object);
		} finally {
			arrayOutputStream.close();
			outputStream.close();
		}
		return arrayOutputStream.toByteArray();
	}

	/**
	 * Method called whether an array must be translated to an object
	 * 
	 * @param bytes
	 *            The object to internalize
	 * @return an object
	 * @throws IOException
	 *             Thrown if the internalization fails
	 * @throws ClassNotFoundException
	 *             if the object is not consistent with existing classes
	 */
	@SuppressWarnings("unchecked")
	public static <T> T bytesToObject(final byte[] bytes) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
		final ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
		try {
			return (T) inputStream.readObject();
		} finally {
			arrayInputStream.close();
			inputStream.close();
		}
	}

	/**
	 * Method called to encode an integer
	 * 
	 * @param i
	 *            The int to encode
	 * @return the encoding result
	 * @throws IOException
	 */
	public static byte[] intToBytes(int i) {
		return new byte[] { (byte) (i >>> BYTES_3), (byte) (i >>> BYTES_2), (byte) (i >>> BYTES_1), (byte) i };
	}

	/**
	 * Method called to decode an integer
	 * 
	 * @param inBuffer
	 *            The encoded int
	 * @throws IOException
	 * @returnt an integer
	 */
	public static int bytesToInt(byte[] inBuffer) throws IOException {
		if (inBuffer.length < INT_LENGTH) {
			throw new IOException(MessagesProvider.get("org.wolfgang.common.message", "cannot.decode.int").format());
		} else {
			int value = 0;
			for (int i = 0; i < INT_LENGTH; i++) {
				value = (value << BYTES_1) + (inBuffer[i] & BYTE_MASK);
			}
			return value;
		}
	}
}
