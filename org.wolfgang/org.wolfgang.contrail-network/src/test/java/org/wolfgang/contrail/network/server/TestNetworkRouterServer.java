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

import static org.wolfgang.contrail.network.reference.ReferenceFactory.createClientReference;

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
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.event.NetworkEventImpl;
import org.wolfgang.contrail.network.reference.DirectReference;
import org.wolfgang.contrail.network.reference.ReferenceEntryAlreadyExistException;
import org.wolfgang.contrail.network.reference.ReferenceFilterFactory;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkRouterServer extends TestCase {

	public void testNominal01Direct() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException,
			ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException, DataHandlerException,
			InterruptedException, ExecutionException {

		final FutureResponse<String> futureResponse = new FutureResponse<String>();

		final DirectReference reference01 = createClientReference(UUIDUtils.digestBased("Client1"));
		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final NetworkComponent network01 = NetworkFactory.create(reference01);

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

		for (int i = 0; i < 1; i++) {
			final String content = "Hello , World from Client01! [" + i + "]";
			final NetworkEventImpl event01 = new NetworkEventImpl(reference01, content);
			terminalComponent01.getDataSender().sendData(event01);
			assertEquals("RECV01| " + content, futureResponse.get());
			futureResponse.reset();
		}
	}

	public void testNominal02Relay() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException,
			ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException, DataHandlerException,
			InterruptedException, ExecutionException {

		final FutureResponse<String> futureResponse = new FutureResponse<String>();

		final DirectReference reference01 = createClientReference(UUIDUtils.digestBased("Client1"));
		final DirectReference reference02 = createClientReference(UUIDUtils.digestBased("Client2"));
		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final NetworkComponent network01 = NetworkFactory.create(reference01);
		final EcosystemImpl ecosystem01 = new EcosystemImpl();

		// ------------------------------------------------------------------------------------------------
		// Populate component 01
		// ------------------------------------------------------------------------------------------------
		network01.getNetworkTable().insert(ReferenceFilterFactory.memberOf(reference02),
				NetworkRouterServerUtils.clientBinder(network01, manager01, "localhost", 6667));

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
		ecosystem01.addFactory(key01, NetworkRouterServerUtils.serverBinder(network01, manager01));

		// ------------------------------------------------------------------------------------------------

		final NetServer networkServer01 = new NetServer(6666, ecosystem01.<byte[], byte[]> getBinder(key01));
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
		network02.getNetworkTable().insert(ReferenceFilterFactory.memberOf(reference01),
				NetworkRouterServerUtils.clientBinder(network02, manager02, "localhost", 6666));

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
		ecosystem02.addFactory(key02, NetworkRouterServerUtils.serverBinder(network02, manager02));

		// ------------------------------------------------------------------------------------------------

		final NetServer networkServer02 = new NetServer(6667, ecosystem02.<byte[], byte[]> getBinder(key02));
		final ExecutorService executor02 = Executors.newSingleThreadExecutor();
		executor02.submit(networkServer02);

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		for (int i = 0; i < 1; i++) {
			final String content = "Hello , World from Client01! [" + i + "]";
			final NetworkEventImpl event01 = new NetworkEventImpl(reference02, content);
			terminalComponent01.getDataSender().sendData(event01);
			assertEquals("RECV02| " + content, futureResponse.get());
			futureResponse.reset();
		}

		// ------------------------------------------------------------------------------------------------

		networkServer01.close();
		networkServer02.close();

		executor01.shutdownNow();
		executor02.shutdownNow();
	}

	public void testNominal03Transitive() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException,
			ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException, DataHandlerException,
			InterruptedException, ExecutionException {

		final FutureResponse<String> futureResponse = new FutureResponse<String>();

		final DirectReference reference01 = createClientReference(UUIDUtils.digestBased("Client1"));
		final DirectReference reference02 = createClientReference(UUIDUtils.digestBased("Client2"));
		final DirectReference reference03 = createClientReference(UUIDUtils.digestBased("Client3"));
		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final NetworkComponent network01 = NetworkFactory.create(reference01);

		// ------------------------------------------------------------------------------------------------
		// Populate component 01
		// ------------------------------------------------------------------------------------------------
		network01.getNetworkTable().insert(ReferenceFilterFactory.memberOf(reference02, reference03),
				NetworkRouterServerUtils.clientBinder(network01, manager01, "localhost", 6667));

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
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager02 = new ComponentLinkManagerImpl();
		final NetworkComponent network02 = NetworkFactory.create(reference02);
		final EcosystemImpl ecosystem02 = new EcosystemImpl();

		// ------------------------------------------------------------------------------------------------
		// Populate component 02
		// ------------------------------------------------------------------------------------------------
		network02.getNetworkTable().insert(ReferenceFilterFactory.memberOf(reference03),
				NetworkRouterServerUtils.clientBinder(network02, manager02, "localhost", 6668));

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
		ecosystem02.addFactory(key02, NetworkRouterServerUtils.serverBinder(network02, manager02));

		// ------------------------------------------------------------------------------------------------

		final NetServer networkServer02 = new NetServer(6667, ecosystem02.<byte[], byte[]> getBinder(key02));
		final ExecutorService executor02 = Executors.newSingleThreadExecutor();
		executor02.submit(networkServer02);

		// ------------------------------------------------------------------------------------------------
		// Component 03 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager03 = new ComponentLinkManagerImpl();
		final NetworkComponent network03 = NetworkFactory.create(reference03);
		final EcosystemImpl ecosystem03 = new EcosystemImpl();

		// ------------------------------------------------------------------------------------------------
		// Populate component 03
		// ------------------------------------------------------------------------------------------------
		network03.getNetworkTable().insert(ReferenceFilterFactory.memberOf(reference01, reference02),
				NetworkRouterServerUtils.clientBinder(network03, manager03, "localhost", 6667));

		final TerminalComponent<NetworkEvent, NetworkEvent> terminalComponent03 = new TerminalComponent<NetworkEvent, NetworkEvent>(
				new DataReceiver<NetworkEvent>() {
					@Override
					public void close() throws IOException {
						// nothing to do
					}

					@Override
					public void receiveData(NetworkEvent data) throws DataHandlerException {
						futureResponse.setValue("RECV03| " + data.getContent());
					}
				});

		manager03.connect(network03, terminalComponent03);

		// ------------------------------------------------------------------------------------------------

		final RegisteredUnitEcosystemKey key03 = UnitEcosystemKeyFactory.getKey("03", NetworkEvent.class, NetworkEvent.class);
		ecosystem03.addFactory(key03, NetworkRouterServerUtils.serverBinder(network03, manager03));

		// ------------------------------------------------------------------------------------------------

		final NetServer networkServer03 = new NetServer(6668, ecosystem03.<byte[], byte[]> getBinder(key03));
		final ExecutorService executor03 = Executors.newSingleThreadExecutor();
		executor03.submit(networkServer03);

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		final NetworkEventImpl event01 = new NetworkEventImpl(reference03, "Hello , World from Client01!");
		terminalComponent01.getDataSender().sendData(event01);
		assertEquals("RECV03| Hello , World from Client01!", futureResponse.get());
		futureResponse.reset();

		// Reuse opened connections ...

		final NetworkEventImpl event02 = new NetworkEventImpl(reference01, "Hello , World from Client03!");
		terminalComponent01.getDataSender().sendData(event02);
		assertEquals("RECV01| Hello , World from Client03!", futureResponse.get());

		// ------------------------------------------------------------------------------------------------

		networkServer02.close();
		networkServer03.close();

		executor02.shutdownNow();
		executor03.shutdownNow();
	}
}
