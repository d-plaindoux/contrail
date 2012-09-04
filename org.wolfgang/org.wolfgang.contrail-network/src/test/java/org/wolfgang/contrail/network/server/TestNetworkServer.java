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

package org.wolfgang.contrail.network.server;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.connection.net.NetServer;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkServer extends TestCase {

	private ComponentFactoryListener linker(final ComponentFactory factory) {
		return new ComponentFactoryListener() {
			@Override
			public void notifyCreation(Component component) throws CannotCreateComponentException {
				try {
					factory.getLinkManager().connect(component, factory.create());
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};
	}

	@Test
	public void testNominal01() throws IOException, CannotProvideComponentException, CannotCreateServerException, URISyntaxException {

		// ------------------------------------------------------------------------------------------------
		// Complex server based on ecosystem
		// ------------------------------------------------------------------------------------------------

		final EcosystemImpl serverEcosystem = new EcosystemImpl();

		// component -> new DataReceiver<byte[]>() {...};

		final UpStreamDataFlowFactory<byte[], byte[]> dataReceiverFactory = new UpStreamDataFlowFactory<byte[], byte[]>() {
			@Override
			public UpStreamDataFlow<byte[]> create(final DownStreamDataFlow<byte[]> sender) {
				return DataFlows.reverse(sender);
			}
		};

		final ComponentFactory dataSenderFactory = new ComponentFactory() {
			final ComponentLinkManagerImpl linkManager = new ComponentLinkManagerImpl();

			@Override
			public Component create() throws CannotCreateComponentException {
				try {
					return new TerminalComponent<byte[], byte[]>(dataReceiverFactory);
				} catch (CannotCreateDataFlowException e) {
					throw new CannotCreateComponentException(e);
				}
			}

			@Override
			public ComponentLinkManager getLinkManager() {
				return linkManager;
			}
		};

		final RegisteredUnitEcosystemKey key = EcosystemKeyFactory.key("test", byte[].class, byte[].class);
		serverEcosystem.addBinder(key, dataSenderFactory);

		final NetServer networkServer = new NetServer();
		networkServer.bind(new URI("tcp://localhost:6666"), linker(serverEcosystem.getFactory(key)));

		// ------------------------------------------------------------------------------------------------
		// Simple socket based client
		// ------------------------------------------------------------------------------------------------

		final Socket socket = new Socket("localhost", 6666);
		final String message = "Hello, World!";

		socket.getOutputStream().write(message.getBytes());

		final byte[] buffer = new byte[1024];
		final int len = socket.getInputStream().read(buffer);

		assertEquals(message, new String(buffer, 0, len));

		socket.close();
		networkServer.close();
		serverEcosystem.close();
	}
}
