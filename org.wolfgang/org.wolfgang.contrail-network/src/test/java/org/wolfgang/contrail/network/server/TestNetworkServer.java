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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataReceiverFactory;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKeyFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;
import org.wolfgang.contrail.network.connection.socket.NetServer;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkServer extends TestCase {

	public void testNominal01() throws IOException, CannotProvideComponentException {

		// ------------------------------------------------------------------------------------------------
		// Complex server based on ecosystem
		// ------------------------------------------------------------------------------------------------

		final EcosystemImpl serverEcosystem = new EcosystemImpl();

		// component -> new DataReceiver<byte[]>() {...};

		final DataReceiverFactory<byte[], byte[]> dataReceiverFactory = new DataReceiverFactory<byte[], byte[]>() {
			@Override
			public DataReceiver<byte[]> create(final DataSender<byte[]> component) {
				return new DataReceiver<byte[]>() {
					@Override
					public void receiveData(byte[] data) throws DataHandlerException {
						component.sendData(data);
					}

					@Override
					public void close() throws IOException {
						component.close();
					}
				};
			}
		};

		// () -> new TerminalComponent<byte[], byte[]>(...).getDataSender();

		final DataSenderFactory<byte[], byte[]> dataSenderFactory = new DataSenderFactory<byte[], byte[]>() {
			@Override
			public DataSender<byte[]> create(DataReceiver<byte[]> receiver) throws CannotCreateDataSenderException {
				final InitialComponent<byte[], byte[]> initialComponent = new InitialComponent<byte[], byte[]>(receiver);
				final TerminalComponent<byte[], byte[]> terminalComponent = new TerminalComponent<byte[], byte[]>(dataReceiverFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataSenderException(e);
				}
				return initialComponent.getDataSender();
			}
		};

		final RegisteredUnitEcosystemKey key = UnitEcosystemKeyFactory.getKey("test", byte[].class, byte[].class);
		serverEcosystem.addFactory(key, dataSenderFactory);

		final NetServer networkServer = new NetServer(2666, serverEcosystem.<byte[], byte[]> getBinder(key));
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(networkServer);

		// ------------------------------------------------------------------------------------------------
		// Simple socket based client
		// ------------------------------------------------------------------------------------------------

		final Socket socket = new Socket("localhost", 2666);
		final String message = "Hello, World!";

		socket.getOutputStream().write(message.getBytes());

		final byte[] buffer = new byte[1024];
		final int len = socket.getInputStream().read(buffer);

		assertEquals(message, new String(buffer, 0, len));

		socket.close();
		networkServer.close();
		executor.shutdownNow();
	}
}
