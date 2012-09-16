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
import org.wolfgang.contrail.component.Component;

/**
 * <code>TestServers</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestClients extends TestCase {

	public static class MyClient implements Client {
		@Override
		public void close() throws IOException {
		}

		@Override
		public Component connect(URI uri) throws CannotCreateClientException {
			return null;
		}
	}

	@Test
	public void testClients01() throws IOException {
		final Clients clients = new Clients();
		try {
			clients.get("my");
			fail();
		} catch (ClientNotFoundException e) {
			// OK
		}
		clients.close();
	}

	@Test
	public void testClients02() throws ClientNotFoundException, IOException {
		final Clients clients = new Clients();
		clients.declareScheme("my", MyClient.class);
		assertEquals(MyClient.class, clients.get("my").getClass());
		clients.close();
	}

}
