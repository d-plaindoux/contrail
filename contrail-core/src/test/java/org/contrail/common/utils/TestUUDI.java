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

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.Test;

/**
 * <code>TestUUDI</code>
 * 
 * @author Didier Plaindoux
 * @verision 1.0
 */
public class TestUUDI {

	@Test
	public void GivenTwoIdenticalUUIDEqualityMustSucceed() throws NoSuchAlgorithmException {
		final UUID uuid1 = UUIDUtils.digestBased("test");
		final UUID uuid2 = UUIDUtils.digestBased("test");

		assertEquals(uuid1, uuid2);
	}

	@Test
	public void GivenTwoIdenticalUUIDHashCodingMustBeTheSame() throws NoSuchAlgorithmException {
		final UUID uuid1 = UUIDUtils.digestBased("test");
		final UUID uuid2 = UUIDUtils.digestBased("test");

		assertEquals(uuid1.hashCode(), uuid2.hashCode());
	}

}
