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
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKeyFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;
import org.wolfgang.contrail.network.component.NetworkComponent;
import org.wolfgang.contrail.network.component.NetworkFactory;
import org.wolfgang.contrail.network.connection.socket.NetServer;
import org.wolgang.contrail.network.event.NetworkEvent;
import org.wolgang.contrail.network.event.NetworkEventImpl;
import org.wolgang.contrail.network.reference.DirectReference;
import org.wolgang.contrail.network.reference.ReferenceEntryAlreadyExistException;
import org.wolgang.contrail.network.reference.ReferenceFactory;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkRouterServer extends TestCase {

	public void testNominal01() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException,
			ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException, DataHandlerException,
			InterruptedException, ExecutionException {
		final DirectReference reference01 = ReferenceFactory.createClientReference(UUIDUtils.digestBased("Client1"));
		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final NetworkComponent network01 = NetworkFactory.create(reference01);

		final FutureResponse<String> futureResponse = new FutureResponse<String>();

		// ------------------------------------------------------------------------------------------------
		// Populate component 01
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<NetworkEvent, NetworkEvent> terminalComponent01 = new TerminalComponent<NetworkEvent, NetworkEvent>(
				new DataReceiver<NetworkEvent>() {
					@Override
					public void close() throws IOException {
						// nothing to do
					}

					@Override
					public void receiveData(NetworkEvent data) throws DataHandlerException {
						futureResponse.setValue("RECV01| " + data.getContent());
					}
				});

		manager01.connect(network01, terminalComponent01);

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		final NetworkEventImpl event01 = new NetworkEventImpl(reference01, reference01, "Hello , World from Client01!");
		terminalComponent01.getDataSender().sendData(event01);

		assertEquals("RECV01| Hello , World from Client01!", futureResponse.get());
	}

	public void testNominal02() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException,
			ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException, DataHandlerException,
			InterruptedException, ExecutionException {
		final DirectReference reference01 = ReferenceFactory.createClientReference(UUIDUtils.digestBased("Client1"));
		final DirectReference reference02 = ReferenceFactory.createClientReference(UUIDUtils.digestBased("Client2"));
		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final NetworkComponent network01 = NetworkFactory.create(reference01);
		final EcosystemImpl ecosystem01 = new EcosystemImpl();

		// ------------------------------------------------------------------------------------------------
		// Populate component 01
		// ------------------------------------------------------------------------------------------------
		network01.getNetworkTable().insert(reference02,
				NetworkRouterServerUtils.clientBinder(network01, manager01, reference02, "localhost", 6666));

		final FutureResponse<String> futureResponse = new FutureResponse<String>();

		final TerminalComponent<NetworkEvent, NetworkEvent> terminalComponent01 = new TerminalComponent<NetworkEvent, NetworkEvent>(
				new DataReceiver<NetworkEvent>() {
					@Override
					public void close() throws IOException {
						// nothing to do
					}

					@Override
					public void receiveData(NetworkEvent data) throws DataHandlerException {
						futureResponse.setValue("RECV01| " + data.getContent());
					}
				});

		manager01.connect(network01, terminalComponent01);

		// ------------------------------------------------------------------------------------------------

		final RegisteredUnitEcosystemKey key01 = UnitEcosystemKeyFactory.getKey("01", NetworkEvent.class, NetworkEvent.class);
		ecosystem01.addFactory(key01, NetworkRouterServerUtils.serverBinder(network01, manager01, reference02));

		// ------------------------------------------------------------------------------------------------

		final NetServer networkServer01 = new NetServer(6667, ecosystem01.<byte[], byte[]> getBinder(key01));
		final ExecutorService executor01 = Executors.newSingleThreadExecutor();
		executor01.submit(networkServer01);

		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager02 = new ComponentLinkManagerImpl();
		final NetworkComponent network02 = NetworkFactory.create(reference02);
		final EcosystemImpl ecosystem02 = new EcosystemImpl();

		// ------------------------------------------------------------------------------------------------
		// Populate component 02
		// ------------------------------------------------------------------------------------------------
		network02.getNetworkTable().insert(reference01,
				NetworkRouterServerUtils.clientBinder(network02, manager02, reference01, "localhost", 6667));

		final TerminalComponent<NetworkEvent, NetworkEvent> terminalComponent02 = new TerminalComponent<NetworkEvent, NetworkEvent>(
				new DataReceiver<NetworkEvent>() {
					@Override
					public void close() throws IOException {
						// nothing to do
					}

					@Override
					public void receiveData(NetworkEvent data) throws DataHandlerException {
						futureResponse.setValue("RECV02| " + data.getContent());
					}
				});

		manager02.connect(network02, terminalComponent02);

		// ------------------------------------------------------------------------------------------------

		final RegisteredUnitEcosystemKey key02 = UnitEcosystemKeyFactory.getKey("02", NetworkEvent.class, NetworkEvent.class);
		ecosystem02.addFactory(key02, NetworkRouterServerUtils.serverBinder(network02, manager02, reference01));

		// ------------------------------------------------------------------------------------------------

		final NetServer networkServer02 = new NetServer(6666, ecosystem02.<byte[], byte[]> getBinder(key02));
		final ExecutorService executor02 = Executors.newSingleThreadExecutor();
		executor02.submit(networkServer02);

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		final NetworkEventImpl event01 = new NetworkEventImpl(reference01, reference02, "Hello , World from Client01!");
		terminalComponent01.getDataSender().sendData(event01);

		assertEquals("RECV02| Hello , World from Client01!", futureResponse.get());
	}
}
