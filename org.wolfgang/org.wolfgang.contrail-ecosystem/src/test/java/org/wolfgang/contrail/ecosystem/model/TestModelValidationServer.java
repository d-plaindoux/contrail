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

package org.wolfgang.contrail.ecosystem.model;

import junit.framework.TestCase;

/**
 * <code>TestModel</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestModelValidationServer extends TestCase {

	public void testNominal01() {
		final ServerModel server = new ServerModel();
		try {
			server.validate();
			fail();
		} catch (ValidationException e) {
			// OK
		}
	}

	public void testNominal02() {
		final ServerModel server = new ServerModel();
		try {
			server.setFlow("A");
			server.validate();
			fail();
		} catch (ValidationException e) {
			// OK
		}
	}

	public void testNominal03() {
		final ServerModel server = new ServerModel();
		try {
			server.setFlow("A");
			server.setEndpoint("ws://localhost:2666");
			server.validate();
			fail();
		} catch (ValidationException e) {
			// OK
		}
	}

	public void testNominal04() {
		final ServerModel server = new ServerModel();
		try {
			server.setFactory("a.b.c");
			server.setFlow("A");
			server.setEndpoint("ws://localhost:2666");
			server.validate();
			// OK
		} catch (ValidationException e) {
			fail();
		}
	}
}
