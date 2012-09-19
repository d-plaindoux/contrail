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

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryAlreadyExistException;
import org.wolfgang.contrail.reference.ReferenceEntryNotFoundException;
import org.wolfgang.contrail.reference.ReferenceFactory;
import org.wolfgang.contrail.reference.ReferenceTableImpl;

/**
 * <code>TestReferenceTable</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestReferenceTable extends TestCase {

	@Test
	public void testNominal01() throws NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ReferenceEntryNotFoundException {
		final String string = "Hello";

		final ReferenceTableImpl<String> table = new ReferenceTableImpl<String>();
		final DirectReference reference = ReferenceFactory.directReference(UUIDUtils.digestBased(string));

		table.insert(string, reference);

		assertEquals(string, table.retrieve(reference));
	}

	@Test
	public void testFailure01() throws NoSuchAlgorithmException, ReferenceEntryAlreadyExistException {
		final String string = "Hello";

		final ReferenceTableImpl<String> table = new ReferenceTableImpl<String>();
		final DirectReference reference = ReferenceFactory.directReference(UUIDUtils.digestBased(string));

		table.insert(string, reference);

		try {
			table.insert(string, reference);
			fail();
		} catch (ReferenceEntryAlreadyExistException e) {
			// OK
		}
	}

	@Test
	public void testFailure02() throws NoSuchAlgorithmException, ReferenceEntryAlreadyExistException {
		final String string = "Hello";

		final ReferenceTableImpl<String> table = new ReferenceTableImpl<String>();
		final DirectReference reference = ReferenceFactory.directReference(UUIDUtils.digestBased(string));

		try {
			table.retrieve(reference);
			fail();
		} catch (ReferenceEntryNotFoundException e) {
			// OK
		}
	}
}
