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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 
 * <code>UUIDUtils</code> provides basic function for uuid generation from a
 * string using MD5 digest
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class UUIDUtils {

	private static final int BYTE_SIZE = 8;
	private static final int BYTE_MASK = 0xFF;
	private static final int DIGEST_LEN = 16;

	/**
	 * Constructor
	 */
	private UUIDUtils() {
		// Prevent useless creation
	}

	/**
	 * Method used when an UUID must be generated using a string representation.
	 * This is done using the MD5 digest generated with the string argument.
	 * 
	 * @param text
	 *            The string using for the UUDI generation
	 * @return a valid UUID
	 * @throws NoSuchAlgorithmException
	 *             thrown if the MD5 digest algorithm is no available
	 */
	public static UUID digestBased(String text) throws NoSuchAlgorithmException {
		assert text != null;

		final MessageDigest algo = MessageDigest.getInstance("MD5");
		algo.reset();
		algo.update(text.getBytes());

		final byte[] digest = algo.digest();

		assert digest.length == DIGEST_LEN;

		long mostSigBits = 0L;
		long leastSigBits = 0L;

		for (int i = 0; i < BYTE_SIZE; i++) {
			mostSigBits = (mostSigBits << BYTE_SIZE) | (digest[i] & BYTE_MASK);
			leastSigBits = (leastSigBits << BYTE_SIZE) | (digest[i + BYTE_SIZE] & BYTE_MASK);
		}

		return new UUID(mostSigBits, leastSigBits);
	}
}
