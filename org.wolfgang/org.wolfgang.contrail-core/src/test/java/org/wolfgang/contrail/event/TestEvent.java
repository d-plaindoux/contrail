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

package org.wolfgang.contrail.event;

import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.IndirectReference;
import org.wolfgang.contrail.reference.ReferenceFactory;

/**
 * <code>TestEvent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestEvent extends TestCase {

	@Test
	public void testEvent01() {
		final String message = "Hello,  World!";
		final EventImpl event = new EventImpl(message);
		assertEquals(message, event.getContent());
	}

	@Test
	public void testEvent02() throws NoSuchAlgorithmException {
		final String message = "Hello,  World!";
		final EventImpl event = new EventImpl(message);
		final DirectReference reference = ReferenceFactory.directReference(UUIDUtils.digestBased("test"));
		event.sentBy(reference);
		assertEquals(reference, event.getSender());
	}

	@Test
	public void testEvent03() throws NoSuchAlgorithmException {
		final String message = "Hello,  World!";
		final DirectReference step0 = ReferenceFactory.directReference(UUIDUtils.digestBased("step0"));
		final DirectReference step1 = ReferenceFactory.directReference(UUIDUtils.digestBased("step1"));
		final IndirectReference path = ReferenceFactory.indirectReference(step0, step1);
		final EventImpl event = new EventImpl(message, step0, step1);
		assertEquals(path, event.getReferenceToDestination());
	}

	@Test
	public void testMessage01() {
		final String message = "Hello,  World!";
		final EventImpl event = new MessageImpl(message);
		assertEquals(message, event.getContent());
	}

	@Test
	public void testMessage02() throws NoSuchAlgorithmException {
		final String message = "Hello,  World!";
		final EventImpl event = new MessageImpl(message);
		final DirectReference reference = ReferenceFactory.directReference(UUIDUtils.digestBased("test"));
		event.sentBy(reference);
		assertEquals(reference, event.getSender());
	}

	@Test
	public void testMessage03() throws NoSuchAlgorithmException {
		final String message = "Hello,  World!";
		final DirectReference step0 = ReferenceFactory.directReference(UUIDUtils.digestBased("step0"));
		final DirectReference step1 = ReferenceFactory.directReference(UUIDUtils.digestBased("step1"));
		final IndirectReference path = ReferenceFactory.indirectReference(step0, step1);
		final EventImpl event = new MessageImpl(message, step0, step1);
		assertEquals(path, event.getReferenceToDestination());
	}

	@Test
	public void testMessage04() throws NoSuchAlgorithmException {
		final String message = "Hello,  World!";
		final DirectReference step0 = ReferenceFactory.directReference(UUIDUtils.digestBased("step0"));
		final DirectReference step1 = ReferenceFactory.directReference(UUIDUtils.digestBased("step1"));
		final IndirectReference path = ReferenceFactory.indirectReference(step0, step1);
		final MessageImpl event = new MessageImpl(message, step0, step1);
		event.handledBy(step1);
		event.handledBy(step0);
		assertEquals(path, event.getReferenceToSource());
	}
}
