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

import static org.wolfgang.contrail.reference.ReferenceFactory.createClientReference;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.router.RouterSourceComponent;
import org.wolfgang.contrail.component.router.RouterSourceFactory;
import org.wolfgang.contrail.component.router.event.Event;
import org.wolfgang.contrail.component.router.event.RoutedEventImpl;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.net.NetServer;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKeyFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryAlreadyExistException;

/**
 * <code>TestNetworkServer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkRouterServer extends TestCase {

	private static class Receiver implements DataReceiver<Event> {
		private final DirectReference self;
		private final AtomicReference<FutureResponse<String>> futureResponse;

		Receiver(DirectReference self, AtomicReference<FutureResponse<String>> futureResponse2) {
			super();
			this.self = self;
			this.futureResponse = futureResponse2;
		}

		@Override
		public void close() throws IOException {
			// nothing to do
		}

		@Override
		public void receiveData(Event data) throws DataHandlerException {
			try {
				System.err.println(self + " - Setting the value " + data.getContent());
				futureResponse.get().setValue(self + " - " + data.getContent());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	};

	// ---------------------------------------------------------------------------

	public void testNominal01Direct() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataHandlerException, InterruptedException, ExecutionException, TimeoutException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());

		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final RouterSourceComponent network01 = RouterSourceFactory.create(reference01);
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent01 = new TerminalComponent<Event, Event>(new Receiver(reference01, futureResponse));
		manager01.connect(network01, terminalComponent01);

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		final String content = "Hello , World from Client01!";
		final RoutedEventImpl event01 = new RoutedEventImpl(content, reference01);
		terminalComponent01.getDataSender().sendData(event01);
		assertEquals(reference01 + " - " + content, futureResponse.get().get(10, TimeUnit.SECONDS));
	}

	public void testNominal02Relay() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataHandlerException, InterruptedException, ExecutionException, TimeoutException, CannotCreateServerException, URISyntaxException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());
		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		final DirectReference reference02 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000002"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final EcosystemImpl ecosystem01 = new EcosystemImpl();
		final RouterSourceComponent network01 = RouterSourceFactory.create(reference01);
		final ComponentLinkManagerImpl manager01 = ecosystem01.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent01 = new TerminalComponent<Event, Event>(new Receiver(reference01, futureResponse));
		manager01.connect(network01, terminalComponent01);
		RouterSourceServerUtils.client(network01, manager01, new URI("tcp://localhost:6667"), reference02);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key01 = UnitEcosystemKeyFactory.getKey("01", Event.class, Event.class);
		ecosystem01.addFactory(key01, RouterSourceServerUtils.serverBinder(network01, manager01));
		// ------------------------------------------------------------------------------------------------
		final NetServer networkServer01 = new NetServer();
		networkServer01.bind(new URI("tcp://localhost:6666"), ecosystem01.<byte[], byte[]> getBinder(key01));

		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final EcosystemImpl ecosystem02 = new EcosystemImpl();
		final RouterSourceComponent network02 = RouterSourceFactory.create(reference02);
		final ComponentLinkManagerImpl manager02 = ecosystem02.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		manager02.connect(network02, new TerminalComponent<Event, Event>(new Receiver(reference02, futureResponse)));
		RouterSourceServerUtils.client(network02, manager02, new URI("tcp://localhost:6666"), reference01);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key02 = UnitEcosystemKeyFactory.getKey("02", Event.class, Event.class);
		ecosystem02.addFactory(key02, RouterSourceServerUtils.serverBinder(network02, manager02));
		// ------------------------------------------------------------------------------------------------
		final NetServer networkServer02 = new NetServer();
		networkServer02.bind(new URI("tcp://localhost:6667"), ecosystem02.<byte[], byte[]> getBinder(key02));

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		for (int i = 0; i < 2; i++) {
			final String content = "Hello , World from Client01! [" + i + "]";
			final RoutedEventImpl event01 = new RoutedEventImpl(content, reference02);
			terminalComponent01.getDataSender().sendData(event01);
			assertEquals(reference02 + " - " + content, futureResponse.get().get(10, TimeUnit.SECONDS));
			futureResponse.set(new FutureResponse<String>());
		}

		// ------------------------------------------------------------------------------------------------

		networkServer01.close();
		networkServer02.close();
	}

	public void testNominal03ComplexPath() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataHandlerException, InterruptedException, ExecutionException, TimeoutException, CannotCreateServerException, URISyntaxException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());
		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		final DirectReference reference02 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000002"));
		final DirectReference reference03 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000003"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final RouterSourceComponent network01 = RouterSourceFactory.create(reference01);
		// ------------------------------------------------------------------------------------------------
		RouterSourceServerUtils.client(network01, manager01, new URI("tcp://localhost:6667"), reference02);
		final TerminalComponent<Event, Event> terminalComponent01 = new TerminalComponent<Event, Event>(new Receiver(reference01, futureResponse));
		manager01.connect(network01, terminalComponent01);

		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final RouterSourceComponent network02 = RouterSourceFactory.create(reference02);
		final EcosystemImpl ecosystem02 = new EcosystemImpl();
		final ComponentLinkManagerImpl manager02 = ecosystem02.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		RouterSourceServerUtils.client(network02, manager02, new URI("tcp://localhost:6668"), reference03);
		manager02.connect(network02, new TerminalComponent<Event, Event>(new Receiver(reference02, futureResponse)));
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key02 = UnitEcosystemKeyFactory.getKey("02", Event.class, Event.class);
		ecosystem02.addFactory(key02, RouterSourceServerUtils.serverBinder(network02, manager02));
		// ------------------------------------------------------------------------------------------------
		final NetServer networkServer02 = new NetServer();
		networkServer02.bind(new URI("tcp://localhost:6667"), ecosystem02.<byte[], byte[]> getBinder(key02));

		// ------------------------------------------------------------------------------------------------
		// Component 03 definition
		// ------------------------------------------------------------------------------------------------
		final RouterSourceComponent network03 = RouterSourceFactory.create(reference03);
		final EcosystemImpl ecosystem03 = new EcosystemImpl();
		final ComponentLinkManagerImpl manager03 = ecosystem03.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent03 = new TerminalComponent<Event, Event>(new Receiver(reference03, futureResponse));
		manager03.connect(network03, terminalComponent03);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key03 = UnitEcosystemKeyFactory.getKey("03", Event.class, Event.class);
		ecosystem03.addFactory(key03, RouterSourceServerUtils.serverBinder(network03, manager03));
		// ------------------------------------------------------------------------------------------------
		final NetServer networkServer03 = new NetServer();
		networkServer03.bind(new URI("tcp://localhost:6668"), ecosystem03.<byte[], byte[]> getBinder(key03));

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		terminalComponent01.getDataSender().sendData(new RoutedEventImpl("Hello , World from Client01!", reference02, reference03));
		assertEquals(reference03 + " - Hello , World from Client01!", futureResponse.get().get(10, TimeUnit.SECONDS));
		futureResponse.set(new FutureResponse<String>());

		// Same but reuse opened connections ...
		terminalComponent01.getDataSender().sendData(new RoutedEventImpl("Hello , World from Client01!", reference02, reference03));
		assertEquals(reference03 + " - Hello , World from Client01!", futureResponse.get().get(10, TimeUnit.SECONDS));
		futureResponse.set(new FutureResponse<String>());

		// Reverse and reuse opened connections ...
		terminalComponent03.getDataSender().sendData(new RoutedEventImpl("Hello , World from Client03!", reference02, reference01));
		assertEquals(reference01 + " - Hello , World from Client03!", futureResponse.get().get(10, TimeUnit.SECONDS));

		// ------------------------------------------------------------------------------------------------

		networkServer02.close();
		networkServer03.close();
	}

	public void testNominal03Transitive() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataHandlerException, InterruptedException, ExecutionException, TimeoutException, CannotCreateServerException, URISyntaxException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());
		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		final DirectReference reference02 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000002"));
		final DirectReference reference03 = createClientReference(UUID.fromString("00000000-0000-0000-0000-000000000003"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManagerImpl manager01 = new ComponentLinkManagerImpl();
		final RouterSourceComponent network01 = RouterSourceFactory.create(reference01);
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent01 = new TerminalComponent<Event, Event>(new Receiver(reference01, futureResponse));
		manager01.connect(network01, terminalComponent01);
		RouterSourceServerUtils.client(network01, manager01, new URI("tcp://localhost:6667"), reference02, reference03);

		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManagerImpl manager02 = new ComponentLinkManagerImpl();
		final RouterSourceComponent network02 = RouterSourceFactory.create(reference02);
		final EcosystemImpl ecosystem02 = new EcosystemImpl();
		// ------------------------------------------------------------------------------------------------
		manager02.connect(network02, new TerminalComponent<Event, Event>(new Receiver(reference02, futureResponse)));
		RouterSourceServerUtils.client(network02, manager02, new URI("tcp://localhost:6668"), reference03);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key02 = UnitEcosystemKeyFactory.getKey("02", Event.class, Event.class);
		ecosystem02.addFactory(key02, RouterSourceServerUtils.serverBinder(network02, manager02));
		// ------------------------------------------------------------------------------------------------
		final NetServer networkServer02 = new NetServer();
		networkServer02.bind(new URI("tcp://localhost:6667"), ecosystem02.<byte[], byte[]> getBinder(key02));

		// ------------------------------------------------------------------------------------------------
		// Component 03 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManagerImpl manager03 = new ComponentLinkManagerImpl();
		final RouterSourceComponent network03 = RouterSourceFactory.create(reference03);
		final EcosystemImpl ecosystem03 = new EcosystemImpl();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent03 = new TerminalComponent<Event, Event>(new Receiver(reference03, futureResponse));
		manager03.connect(network03, terminalComponent03);
		RouterSourceServerUtils.client(network03, manager03, new URI("tcp://localhost:6667"), reference02, reference01);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key03 = UnitEcosystemKeyFactory.getKey("03", Event.class, Event.class);
		ecosystem03.addFactory(key03, RouterSourceServerUtils.serverBinder(network03, manager03));
		// ------------------------------------------------------------------------------------------------
		final NetServer networkServer03 = new NetServer();
		networkServer03.bind(new URI("tcp://localhost:6668"), ecosystem03.<byte[], byte[]> getBinder(key03));

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		terminalComponent01.getDataSender().sendData(new RoutedEventImpl("Hello , World from Client01!", reference03));
		assertEquals(reference03 + " - Hello , World from Client01!", futureResponse.get().get(10, TimeUnit.SECONDS));
		futureResponse.set(new FutureResponse<String>());

		// Same but reuse opened connections ...
		terminalComponent01.getDataSender().sendData(new RoutedEventImpl("Hello , World from Client01!", reference03));
		assertEquals(reference03 + " - Hello , World from Client01!", futureResponse.get().get(10, TimeUnit.SECONDS));
		futureResponse.set(new FutureResponse<String>());

		// Reverse and Reuse already opened connections ...

		terminalComponent03.getDataSender().sendData(new RoutedEventImpl("Hello , World from Client03!", reference01));
		assertEquals(reference01 + " - Hello , World from Client03!", futureResponse.get().get(10, TimeUnit.SECONDS));

		// ------------------------------------------------------------------------------------------------

		networkServer02.close();
		networkServer03.close();
	}
}
