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

package org.wolfgang.contrail.network.ecosystem;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.wolfgang.contrail.connection.net.NetServer;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.factory.EcosystemCreationException;
import org.wolfgang.contrail.ecosystem.factory.EcosystemFactory;
import org.wolfgang.contrail.ecosystem.model.EcosystemModel;

/**
 * <code>TestNetworkEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkEcosystem extends TestCase {

	public void testNominal01() {
		final URL resource = TestNetworkEcosystem.class.getClassLoader().getResource("sample01.xml");

		assertNotNull(resource);

		try {
			final EcosystemFactory ecosystemFactory = new EcosystemFactory();
			ecosystemFactory.getServerFactory().declareScheme("tcp", NetServer.class.getCanonicalName());

			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = ecosystemFactory.build(decoded);

			final Socket socket = new Socket("localhost", 6666);
			final String message = "Hello, World!";

			socket.getOutputStream().write(message.getBytes());

			final byte[] buffer = new byte[1024];
			final int len = socket.getInputStream().read(buffer);

			assertEquals(message, new String(buffer, 0, len));

			socket.close();
			
			ecosystem.close();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testNominal02() throws JAXBException, IOException, EcosystemCreationException {
		final URL resource = TestNetworkEcosystem.class.getClassLoader().getResource("sample01.xml");

		assertNotNull(resource);

			final EcosystemFactory ecosystemFactory = new EcosystemFactory();
			ecosystemFactory.getServerFactory().declareScheme("tcp", NetServer.class.getCanonicalName());

			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = ecosystemFactory.build(decoded);

			
		try {
			new Socket("localhost", 6667);
			fail();
		} catch (SocketException e) {
			// OK			
		} finally {
			ecosystem.close();
		}
	}

}
