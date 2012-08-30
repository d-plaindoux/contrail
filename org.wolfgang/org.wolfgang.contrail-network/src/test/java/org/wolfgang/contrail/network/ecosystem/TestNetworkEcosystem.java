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

import org.junit.Test;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.factory.EcosystemCreationException;
import org.wolfgang.contrail.ecosystem.factory.EcosystemFactoryImpl;
import org.wolfgang.contrail.ecosystem.model.EcosystemModel;

/**
 * <code>TestNetworkEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkEcosystem extends TestCase {

	@Test
	public void testNominal01() {

		try {
			final URL resource = TestNetworkEcosystem.class.getClassLoader().getResource("sample01.xml");
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(decoded);

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

	@Test
	public void testNominal01Error() throws JAXBException, IOException, EcosystemCreationException {

		final URL resource = TestNetworkEcosystem.class.getClassLoader().getResource("sample01.xml");
		final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
		final Ecosystem ecosystem = EcosystemFactoryImpl.build(decoded);

		try {
			new Socket("localhost", 6667);
			fail();
		} catch (SocketException e) {
			// OK
		} finally {
			ecosystem.close();
		}
	}

	/** DEACTIVATED
	@Test
	public void testNominal02() {

		try {
			final URL resource01 = TestNetworkEcosystem.class.getClassLoader().getResource("sample01.xml");
			final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(EcosystemModel.decode(resource01.openStream()));

			final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02.xml");
			final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(EcosystemModel.decode(resource02.openStream()));

			// ----------------------------------------------------------------------------------------------------

			final FutureResponse<String> response = new FutureResponse<String>();
			final DownStreamDataHandler<byte[]> dataReceiver = new DownStreamDataHandlerAdapter<byte[]>() {
				@Override
				public void handleData(byte[] data) throws DataHandlerException {
					super.handleData(data);
					response.setValue(new String(data));
				}
			};

			// ----------------------------------------------------------------------------------------------------

			final UpStreamDataHandlerFactory<byte[], byte[]> binder = ecosystem02.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<byte[]> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";

			sender.handleData(message.getBytes());

			assertEquals(message, response.get(10, TimeUnit.SECONDS));

			ecosystem01.close();
			ecosystem02.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	*/
}
