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

package org.wolfgang.contrail.connection;

import java.io.IOException;
import java.net.URI;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * <code>TestServers</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestServers extends TestCase {

	public static class MyServer implements Server {
		@Override
		public void close() throws IOException {
		}

		@Override
		public Worker bind(URI uri, ComponentFactoryListener listener) throws CannotCreateServerException {
			return null;
		}
	}

	@Test
	public void testServers01() throws IOException {
		final Servers servers = new Servers();
		try {
			servers.get("my").getClass();
			fail();
		} catch (ServerNotFoundException e) {
			// OK
		}
		servers.close();
	}
	

	@Test
	public void testServers02() throws ServerNotFoundException, IOException {
		final Servers servers = new Servers();
		servers.register("my", MyServer.class);
		assertEquals(MyServer.class, servers.get("my").getClass());
		servers.close();
	}
	
}
