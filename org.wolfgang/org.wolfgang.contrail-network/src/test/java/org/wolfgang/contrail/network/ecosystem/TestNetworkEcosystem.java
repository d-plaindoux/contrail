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
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.factory.BoundComponents;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.connection.net.NetServer;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.lang.EcosystemFactoryImpl;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestNetworkEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkEcosystem extends TestCase {
	
	@Test
	public void testCLientConnection() throws JAXBException, IOException, Exception {
		final NetServer netServer = new NetServer();
		netServer.bind(new URI("tcp://0.0.0.0:6666"), new ComponentFactoryListener() {
			@Override
			public void notifyCreation(Component client) throws CannotCreateComponentException {
				final ComponentLinkManager manager = new ComponentLinkManagerImpl();
				try {
					final Component terminalComponent = BoundComponents.terminal(new UpStreamDataFlowFactory<byte[], byte[]>() {
						@Override
						public UpStreamDataFlow<byte[]> create(DownStreamDataFlow<byte[]> component) throws CannotCreateDataFlowException {
							return DataFlows.reverse(component);
						}
					});
					manager.connect(client, terminalComponent);
				} catch (CannotCreateDataFlowException e) {
					throw new CannotCreateComponentException(e);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		});
		
		// ----------------------------------------------------------------------------------------------------
		
		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));
		
		// ----------------------------------------------------------------------------------------------------

		final FutureResponse<String> response = new FutureResponse<String>();
		final TerminalComponent<byte[], byte[]> sender = new TerminalComponent<byte[],byte[]>(new UpStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				response.setValue(new String(data));
			}
		});

		// ----------------------------------------------------------------------------------------------------
		
		final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
		factory.getLinkManager().connect(factory.create(), sender);
		
		final String message = "Hello, World!";
		sender.getDownStreamDataHandler().handleData(message.getBytes());
		assertEquals(message, response.get(10, TimeUnit.SECONDS));
		
		netServer.close();
		ecosystem02.close();
}
	
	/** DEACTIVATED
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
