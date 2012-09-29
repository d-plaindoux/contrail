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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.Test;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.IndirectReference;
import org.wolfgang.contrail.reference.ReferenceFactory;
import org.wolfgang.contrail.reference.ReferenceVisitor;

/**
 * <code>TestReference</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestReference {

	@Test
	public void testEquals01() throws NoSuchAlgorithmException {
		final UUID identifier = UUIDUtils.digestBased("Reference");

		final DirectReference reference01 = ReferenceFactory.directReference(identifier);
		final DirectReference reference02 = ReferenceFactory.directReference(identifier);

		assertEquals(reference01.hashCode(), reference02.hashCode());
		assertEquals(reference01, reference02);
	}

	@Test
	public void testEquals02() throws NoSuchAlgorithmException {
		final DirectReference reference01 = ReferenceFactory.directReference(UUID.randomUUID());
		final DirectReference reference02 = ReferenceFactory.directReference(UUID.randomUUID());
		final IndirectReference indirectReference1 = ReferenceFactory.indirectReference(reference01, reference02);
		final IndirectReference indirectReference2 = ReferenceFactory.indirectReference(reference01, reference02);

		assertEquals(indirectReference1.hashCode(), indirectReference2.hashCode());
		assertEquals(indirectReference1, indirectReference2);
	}

	@Test
	public void testEquals03() throws NoSuchAlgorithmException {
		final DirectReference reference01 = ReferenceFactory.directReference(UUID.randomUUID());
		final DirectReference reference02 = ReferenceFactory.directReference(UUID.randomUUID());
		final IndirectReference indirectReference = ReferenceFactory.indirectReference();

		indirectReference.addFirst(reference02);
		indirectReference.addFirst(reference01);

		assertEquals(reference01, indirectReference.getCurrent());
		indirectReference.removeCurrent();

		assertEquals(reference02, indirectReference.getCurrent());
	}

	@Test
	public void testChainedReference01() throws NoSuchAlgorithmException {
		final DirectReference reference01 = ReferenceFactory.directReference(UUIDUtils.digestBased("Reference"));
		final IndirectReference reference = ReferenceFactory.indirectReference();
		reference.addFirst(reference01).addFirst(reference01);
		assertFalse(reference.hasNext());
	}

	@Test
	public void testVisitor01() throws Exception {
		final DirectReference reference01 = ReferenceFactory.directReference(UUIDUtils.digestBased("Reference"));
		final ReferenceVisitor<Boolean, Exception> referenceVisitor = new ReferenceVisitor<Boolean, Exception>() {
			@Override
			public Boolean visit(DirectReference reference) throws Exception {
				return reference.equals(reference01);
			}

			@Override
			public Boolean visit(IndirectReference reference) throws Exception {
				return false;
			}
		};

		assertTrue(reference01.visit(referenceVisitor));
	}

	@Test
	public void testVisitor02() throws Exception {
		final DirectReference reference01 = ReferenceFactory.directReference(UUIDUtils.digestBased("Reference"));
		final IndirectReference reference02 = ReferenceFactory.indirectReference(reference01);
		final ReferenceVisitor<Boolean, Exception> referenceVisitor = new ReferenceVisitor<Boolean, Exception>() {
			@Override
			public Boolean visit(DirectReference reference) throws Exception {
				return false;
			}

			@Override
			public Boolean visit(IndirectReference reference) throws Exception {
				return reference.equals(reference02);
			}
		};

		assertTrue(reference02.visit(referenceVisitor));
	}
}
