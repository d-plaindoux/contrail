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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <code>StreamReader</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
final class StreamReader {

	private final DataInputStream stream;

	/**
	 * Constructor
	 * 
	 * @param stream
	 */
	StreamReader(InputStream stream) {
		super();
		this.stream = new DataInputStream(stream);
	}

	byte[] getNextBytes(int len) throws IOException {
		final byte[] buffer = new byte[len];
		this.stream.read(buffer);
		return buffer;
	}

	int getNextUnsignedByte() throws IOException {
		return this.stream.readUnsignedByte();
	}

	char[] getChars(int len) throws IOException {
		final char[] value = new char[len];
		for (int i = 0; i < len; i++) {
			value[i] = (char) getNextUnsignedByte();
		}
		return value;
	}

	int getNextShort() throws IOException {
		return this.stream.readShort();
	}

	int getNextInt() throws IOException {
		return this.stream.readInt();
	}

	float getNextFloat() throws IOException {
		return this.stream.readFloat();
	}

	long getNextLong() throws IOException {
		return this.stream.readLong();
	}

	double getNextDouble() throws IOException {
		return this.stream.readDouble();
	}

	void close() throws IOException {
		this.stream.close();
	}
}
