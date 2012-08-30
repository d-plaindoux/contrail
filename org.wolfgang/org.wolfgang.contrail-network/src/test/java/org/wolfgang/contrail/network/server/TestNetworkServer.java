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
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialUpStreamDataHandler;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.net.NetServer;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerAdapter;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkServer extends TestCase {

	@Test
	public void testNominal01() throws IOException, CannotProvideComponentException, CannotCreateServerException, URISyntaxException {

		// ------------------------------------------------------------------------------------------------
		// Complex server based on ecosystem
		// ------------------------------------------------------------------------------------------------

		final EcosystemImpl serverEcosystem = new EcosystemImpl();

		// component -> new DataReceiver<byte[]>() {...};

		final UpStreamDataHandlerFactory<byte[], byte[]> dataReceiverFactory = new UpStreamDataHandlerFactory<byte[], byte[]>() {
			@Override
			public UpStreamDataHandler<byte[]> create(final DownStreamDataHandler<byte[]> sender) {
				return new UpStreamDataHandlerAdapter<byte[]>() {
					@Override
					public void handleData(byte[] data) throws DataHandlerException {
						sender.handleData(data);
					}

					@Override
					public void handleClose() throws DataHandlerCloseException {
						super.handleClose();
						sender.handleClose();
					}

					@Override
					public void handleLost() throws DataHandlerCloseException {
						super.handleLost();
						sender.handleLost();
					}
				};
			}
		};

		// () -> new TerminalComponent<byte[], byte[]>(...).getDataSender();

		final UpStreamDataHandlerFactory<byte[], byte[]> dataSenderFactory = new UpStreamDataHandlerFactory<byte[], byte[]>() {
			@Override
			public UpStreamDataHandler<byte[]> create(DownStreamDataHandler<byte[]> receiver) throws CannotCreateDataHandlerException {
				final InitialComponent<byte[], byte[]> initialComponent = new InitialComponent<byte[], byte[]>(receiver);
				final TerminalComponent<byte[], byte[]> terminalComponent = new TerminalComponent<byte[], byte[]>(dataReceiverFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataHandlerException(e);
				}
				return InitialUpStreamDataHandler.<byte[]> create(initialComponent);
			}
		};

		final RegisteredUnitEcosystemKey key = EcosystemKeyFactory.key("test", byte[].class, byte[].class);
		serverEcosystem.addBinder(key, dataSenderFactory);

		final NetServer networkServer = new NetServer();
		networkServer.bind(new URI("tcp://localhost:6666"), serverEcosystem.<byte[], byte[]> getBinder(key));

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
	}
}
