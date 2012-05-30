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
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.ecosystem.ComponentEcosystemImpl;
import org.wolfgang.contrail.ecosystem.DestinationComponentFactory;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.network.connection.socket.NetworkServer;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkServer extends TestCase {

	public void testNominal01() throws IOException {
		final ComponentEcosystemImpl ecosystem = new ComponentEcosystemImpl();

		// A little bit too complex ... isn't it ?
		final TerminalDataReceiverFactory<byte[], byte[]> dataFactory = new TerminalDataReceiverFactory<byte[], byte[]>() {
			@Override
			public DataReceiver<byte[]> create(final TerminalComponent<byte[], byte[]> component) {
				return new DataReceiver<byte[]>() {
					@Override
					public void receiveData(byte[] data) throws DataHandlerException {
						component.getDataSender().sendData(data);
					}

					@Override
					public void close() throws IOException {
						component.getDataSender().close();
					}
				};
			}
		};

		final DestinationComponentFactory<byte[], byte[]> destinationComponentFactory = new DestinationComponentFactory<byte[], byte[]>() {
			@Override
			public DestinationComponent<byte[], byte[]> create() {
				return new TerminalComponent<byte[], byte[]>(dataFactory);
			}
		};

		ecosystem.addDestinationFactory(UnitEcosystemKey.getKey("test", byte[].class, byte[].class), destinationComponentFactory);

		final NetworkServer networkServer = new NetworkServer(InetAddress.getLocalHost(), 2666,	UnitEcosystemKey.allwaysTrue(), ecosystem);
		final ExecutorService executor = Executors.newSingleThreadExecutor();

		executor.submit(networkServer);

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
