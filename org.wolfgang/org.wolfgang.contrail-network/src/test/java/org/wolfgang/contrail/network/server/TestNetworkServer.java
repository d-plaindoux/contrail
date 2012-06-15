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
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataReceiverFactory;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.CannotProvideInitialComponentException;
import org.wolfgang.contrail.ecosystem.DestinationComponentFactory;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.network.connection.socket.NetServer;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkServer extends TestCase {

	public void testNominal01() throws IOException, CannotProvideInitialComponentException {
		//
		// Complex server based on ecosystem
		//

		final EcosystemImpl serverEcosystem = new EcosystemImpl();

		// In Java 8: dataFactory = component -> new DataReceiver<byte[]>() {
		// ... };

		final DataReceiverFactory<byte[], byte[]> dataFactory = new DataReceiverFactory<byte[], byte[]>() {
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

		// In Java 8: destinationComponentFactory = () -> new
		// TerminalComponent<byte[], byte[]>(dataFactory);

		final DestinationComponentFactory<byte[], byte[]> destinationComponentFactory = new DestinationComponentFactory<byte[], byte[]>() {
			@Override
			public DestinationComponent<byte[], byte[]> create() {
				return new TerminalComponent<byte[], byte[]>(dataFactory);
			}
		};

		final RegisteredUnitEcosystemKey key = UnitEcosystemKey.getKey("test", byte[].class, byte[].class);
		serverEcosystem.addDestinationFactory(key, destinationComponentFactory);

		final NetServer networkServer = new NetServer(InetAddress.getLocalHost(), 2666,
				serverEcosystem.<byte[], byte[]> getInitialBinder(key));
		final ExecutorService executor = Executors.newSingleThreadExecutor();

		executor.submit(networkServer);

		//
		// Simple socket based client
		//

		final Socket socket = new Socket(InetAddress.getLocalHost(), 2666);
		final String message = "Hello, World!";

		socket.getOutputStream().write(message.getBytes());

		final byte[] buffer = new byte[1024];
		final int len = socket.getInputStream().read(buffer);

		assertEquals(message, new String(buffer, 0, len));

		socket.close();
		executor.shutdown();
		networkServer.close();
	}
}
