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

package org.wolfgang.contrail.network.connection.web.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Test;

/**
 * <code>TestResource</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestStringResource extends TestCase {

	@Test
	public void testNominal01() {
		final Resource resource = new StringResourceImpl("This is a ${simple} test with some ${meta.variables}");
		final Collection<String> freeVariables = resource.getFreeVariables();

		assertTrue(freeVariables.contains("simple"));
		assertTrue(freeVariables.contains("meta.variables"));
	}

	@Test
	public void testNominal02() throws IOException {
		final Resource resource = new StringResourceImpl("Hello, ${who}!");
		final Map<String, String> variables = new HashMap<String, String>();
		variables.put("who", "World");

		final byte[] content = resource.getContent(variables);
		assertEquals("Hello, World!", new String(content));
	}
}
