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

package org.wolfgang.contrail.network.reference;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import junit.framework.TestCase;

import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.IndirectReference;
import org.wolfgang.contrail.reference.ReferenceFactory;

/**
 * <code>TestReference</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestReference extends TestCase {

	public void testNominal01() throws NoSuchAlgorithmException {
		final UUID identifier = UUIDUtils.digestBased("Client");

		final DirectReference reference01 = ReferenceFactory.createClientReference(identifier);
		final DirectReference reference02 = ReferenceFactory.createClientReference(identifier);

		assertEquals(reference01, reference02);
	}

	public void testNominal02() throws NoSuchAlgorithmException {
		final UUID identifier = UUIDUtils.digestBased("Client");

		final DirectReference reference01 = ReferenceFactory.createServerReference(identifier);
		final DirectReference reference02 = ReferenceFactory.createServerReference(identifier);

		assertEquals(reference01, reference02);
	}

	public void testFailure01() throws NoSuchAlgorithmException {
		final UUID identifier = UUIDUtils.digestBased("Client");

		final DirectReference reference01 = ReferenceFactory.createServerReference(identifier);
		final DirectReference reference02 = ReferenceFactory.createClientReference(identifier);

		assertNotSame(reference01, reference02);
	}

	public void testNominal03() throws NoSuchAlgorithmException {
		final DirectReference reference01 = ReferenceFactory.createServerReference(UUID.randomUUID());
		final DirectReference reference02 = ReferenceFactory.createServerReference(UUID.randomUUID());
		final IndirectReference indirectReference = ReferenceFactory.emptyIndirectReference();

		indirectReference.addFirst(reference02);
		indirectReference.addFirst(reference01);
		
		assertEquals(reference01, indirectReference.getNext());
		indirectReference.removeNext();
		
		assertEquals(reference02, indirectReference.getNext());
	}
}
