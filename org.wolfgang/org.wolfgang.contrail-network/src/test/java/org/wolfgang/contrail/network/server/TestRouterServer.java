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

import static org.junit.Assert.assertEquals;
import static org.wolfgang.contrail.reference.ReferenceFactory.directReference;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.component.router.DataFlowStationFactory;
import org.wolfgang.contrail.component.router.RouterComponent;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.connection.net.NetServer;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.event.EventImpl;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
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
public class TestRouterServer {

	private static class Receiver extends UpStreamDataFlowAdapter<Event> {
		private final DirectReference self;
		private final AtomicReference<FutureResponse<String>> futureResponse;

		Receiver(DirectReference self, AtomicReference<FutureResponse<String>> futureResponse2) {
			super();
			this.self = self;
			this.futureResponse = futureResponse2;
		}

		@Override
		public void handleData(Event data) throws DataFlowException {
			try {
				futureResponse.get().setValue(self + " - " + data.getContent());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	};

	// ---------------------------------------------------------------------------

	@Test
	public void testNominal01Direct() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataFlowException, InterruptedException, ExecutionException, TimeoutException, ComponentNotConnectedException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());

		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final RouterComponent router01 = new RouterComponent(DataFlowStationFactory.forRouter(reference01));
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent01 = Components.terminal(new Receiver(reference01, futureResponse));
		manager01.connect(router01, terminalComponent01);
		router01.filter(terminalComponent01.getComponentId(), reference01);

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		final String content = "Hello , World from Client01!";
		final EventImpl event01 = new EventImpl(content, reference01);
		terminalComponent01.getDownStreamDataHandler().handleData(event01);
		assertEquals(reference01 + " - " + content, futureResponse.get().get(10, TimeUnit.SECONDS));
	}

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
	public void testNominal02Relay() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataFlowException, InterruptedException, ExecutionException, TimeoutException, CannotCreateServerException, URISyntaxException, ComponentNotConnectedException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());
		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		final DirectReference reference02 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000002"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final EcosystemImpl ecosystem01 = new EcosystemImpl();
		final RouterComponent router01 = new RouterComponent(DataFlowStationFactory.forRouter(reference01));
		final ComponentLinkManager manager01 = ecosystem01.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent01 = Components.terminal(new Receiver(reference01, futureResponse));
		manager01.connect(router01, terminalComponent01);
		RouterUtils.client(router01, manager01, new URI("tcp://localhost:6667"), reference02);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key01 = EcosystemKeyFactory.key("01");
		ecosystem01.addBinder(key01, RouterUtils.serverBinder(router01, manager01));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer01 = new NetServer();
		routerServer01.bind(new URI("tcp://localhost:6666"), linker(ecosystem01.getFactory(key01)));

		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final EcosystemImpl ecosystem02 = new EcosystemImpl();
		final RouterComponent router02 = new RouterComponent(DataFlowStationFactory.forRouter(reference02));
		final ComponentLinkManager manager02 = ecosystem02.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent02 = Components.terminal(new Receiver(reference02, futureResponse));
		manager02.connect(router02, terminalComponent02);
		router02.filter(terminalComponent02.getComponentId(), reference02);
		RouterUtils.client(router02, manager02, new URI("tcp://localhost:6666"), reference01);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key02 = EcosystemKeyFactory.key("02");
		ecosystem02.addBinder(key02, RouterUtils.serverBinder(router02, manager02));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer02 = new NetServer();
		routerServer02.bind(new URI("tcp://localhost:6667"), linker(ecosystem02.getFactory(key02)));

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		for (int i = 0; i < 2; i++) {
			final String content = "Hello , World from Client01! [" + i + "]";
			final EventImpl event01 = new EventImpl(content, reference02);
			terminalComponent01.getDownStreamDataHandler().handleData(event01);
			assertEquals(reference02 + " - " + content, futureResponse.get().get(10, TimeUnit.SECONDS));
			futureResponse.set(new FutureResponse<String>());
		}

		// ------------------------------------------------------------------------------------------------

		routerServer01.close();
		routerServer02.close();
		ecosystem01.close();
		ecosystem02.close();
	}

	@Test
	public void testNominal02ComplexPath() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataFlowException, InterruptedException, ExecutionException, TimeoutException, CannotCreateServerException, URISyntaxException, ComponentNotConnectedException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());
		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		final DirectReference reference02 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000002"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final EcosystemImpl ecosystem01 = new EcosystemImpl();
		final RouterComponent router01 = new RouterComponent(DataFlowStationFactory.forRouter(reference01));
		final ComponentLinkManager manager01 = ecosystem01.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent01 = Components.terminal(new Receiver(reference01, futureResponse));
		manager01.connect(router01, terminalComponent01);
		RouterUtils.client(router01, manager01, new URI("tcp://localhost:6667"), reference02);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key01 = EcosystemKeyFactory.key("01");
		ecosystem01.addBinder(key01, RouterUtils.serverBinder(router01, manager01));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer01 = new NetServer();
		routerServer01.bind(new URI("tcp://localhost:6666"), linker(ecosystem01.getFactory(key01)));

		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final EcosystemImpl ecosystem02 = new EcosystemImpl();
		final RouterComponent router02 = new RouterComponent(DataFlowStationFactory.forRouter(reference02));
		final ComponentLinkManager manager02 = ecosystem02.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent02 = Components.terminal(new Receiver(reference02, futureResponse));
		manager02.connect(router02, terminalComponent02);
		router02.filter(terminalComponent02.getComponentId(), reference02);
		RouterUtils.client(router02, manager02, new URI("tcp://localhost:6666"), reference01);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key02 = EcosystemKeyFactory.key("02");
		ecosystem02.addBinder(key02, RouterUtils.serverBinder(router02, manager02));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer02 = new NetServer();
		routerServer02.bind(new URI("tcp://localhost:6667"), linker(ecosystem02.getFactory(key02)));

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		for (int i = 0; i < 2; i++) {
			final String content = "Hello , World from Client01! [" + i + "]";
			final EventImpl event01 = new EventImpl(content, reference01, reference02);
			terminalComponent01.getDownStreamDataHandler().handleData(event01);
			assertEquals(reference02 + " - " + content, futureResponse.get().get(10, TimeUnit.SECONDS));
			futureResponse.set(new FutureResponse<String>());
		}

		// ------------------------------------------------------------------------------------------------

		routerServer01.close();
		routerServer02.close();
		ecosystem01.close();
		ecosystem02.close();
	}

	@Test
	public void testNominal03ComplexPath() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataFlowException, InterruptedException, ExecutionException, TimeoutException, CannotCreateServerException, URISyntaxException, ComponentNotConnectedException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>(new FutureResponse<String>());
		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		final DirectReference reference02 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000002"));
		final DirectReference reference03 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000003"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final RouterComponent router01 = new RouterComponent(DataFlowStationFactory.forRouter(reference01));
		// ------------------------------------------------------------------------------------------------
		RouterUtils.client(router01, manager01, new URI("tcp://localhost:6667"), reference02);
		final TerminalComponent<Event, Event> terminalComponent01 = Components.terminal(new Receiver(reference01, futureResponse));
		manager01.connect(router01, terminalComponent01);
		router01.filter(terminalComponent01.getComponentId(), reference01);
		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final RouterComponent router02 = new RouterComponent(DataFlowStationFactory.forRouter(reference02));
		final EcosystemImpl ecosystem02 = new EcosystemImpl();
		final ComponentLinkManager manager02 = ecosystem02.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		RouterUtils.client(router02, manager02, new URI("tcp://localhost:6668"), reference03);
		manager02.connect(router02, Components.terminal(new Receiver(reference02, futureResponse)));
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key02 = EcosystemKeyFactory.key("02");
		ecosystem02.addBinder(key02, RouterUtils.serverBinder(router02, manager02));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer02 = new NetServer();
		routerServer02.bind(new URI("tcp://localhost:6667"), linker(ecosystem02.getFactory(key02)));

		// ------------------------------------------------------------------------------------------------
		// Component 03 definition
		// ------------------------------------------------------------------------------------------------
		final RouterComponent router03 = new RouterComponent(DataFlowStationFactory.forRouter(reference03));
		final EcosystemImpl ecosystem03 = new EcosystemImpl();
		final ComponentLinkManager manager03 = ecosystem03.getLinkManager();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent03 = Components.terminal(new Receiver(reference03, futureResponse));
		manager03.connect(router03, terminalComponent03);
		router03.filter(terminalComponent03.getComponentId(), reference03);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key03 = EcosystemKeyFactory.key("03");
		ecosystem03.addBinder(key03, RouterUtils.serverBinder(router03, manager03));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer03 = new NetServer();
		routerServer03.bind(new URI("tcp://localhost:6668"), linker(ecosystem03.getFactory(key03)));

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		terminalComponent01.getDownStreamDataHandler().handleData(new EventImpl("Hello , World from Client01!", reference02, reference03));
		assertEquals(reference03 + " - Hello , World from Client01!", futureResponse.get().get(10, TimeUnit.SECONDS));
		futureResponse.set(new FutureResponse<String>());

		// Same but reuse opened connections ...
		terminalComponent01.getDownStreamDataHandler().handleData(new EventImpl("Hello , World from Client01!", reference02, reference03));
		assertEquals(reference03 + " - Hello , World from Client01!", futureResponse.get().get(10, TimeUnit.SECONDS));
		futureResponse.set(new FutureResponse<String>());

		// Reverse and reuse opened connections ...
		terminalComponent03.getDownStreamDataHandler().handleData(new EventImpl("Hello , World from Client03!", reference02, reference01));
		assertEquals(reference01 + " - Hello , World from Client03!", futureResponse.get().get(10, TimeUnit.SECONDS));

		// ------------------------------------------------------------------------------------------------

		routerServer02.close();
		routerServer03.close();
		ecosystem02.close();
		ecosystem03.close();
	}

	@Test
	public void testNominal03Transitive() throws IOException, CannotProvideComponentException, NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, ComponentConnectionRejectedException,
			DataFlowException, InterruptedException, ExecutionException, TimeoutException, CannotCreateServerException, URISyntaxException, ComponentNotConnectedException {

		final AtomicReference<FutureResponse<String>> futureResponse = new AtomicReference<FutureResponse<String>>();
		// ------------------------------------------------------------------------------------------------
		final DirectReference reference01 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		final DirectReference reference02 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000002"));
		final DirectReference reference03 = directReference(UUID.fromString("00000000-0000-0000-0000-000000000003"));

		// ------------------------------------------------------------------------------------------------
		// Component 01 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager01 = new ComponentLinkManagerImpl();
		final RouterComponent router01 = new RouterComponent(DataFlowStationFactory.forRouter(reference01));
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent01 = Components.terminal(new Receiver(reference01, futureResponse));
		manager01.connect(router01, terminalComponent01);
		router01.filter(terminalComponent01.getComponentId(), reference01);
		RouterUtils.client(router01, manager01, new URI("tcp://localhost:6667"), reference02, reference03);

		// ------------------------------------------------------------------------------------------------
		// Component 02 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager02 = new ComponentLinkManagerImpl();
		final RouterComponent router02 = new RouterComponent(DataFlowStationFactory.forRouter(reference02));
		final EcosystemImpl ecosystem02 = new EcosystemImpl();
		// ------------------------------------------------------------------------------------------------
		manager02.connect(router02, Components.terminal(new Receiver(reference02, futureResponse)));
		RouterUtils.client(router02, manager02, new URI("tcp://localhost:6668"), reference03);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key02 = EcosystemKeyFactory.key("02");
		ecosystem02.addBinder(key02, RouterUtils.serverBinder(router02, manager02));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer02 = new NetServer();
		routerServer02.bind(new URI("tcp://localhost:6667"), linker(ecosystem02.getFactory(key02)));

		// ------------------------------------------------------------------------------------------------
		// Component 03 definition
		// ------------------------------------------------------------------------------------------------
		final ComponentLinkManager manager03 = new ComponentLinkManagerImpl();
		final RouterComponent router03 = new RouterComponent(DataFlowStationFactory.forRouter(reference03));
		final EcosystemImpl ecosystem03 = new EcosystemImpl();
		// ------------------------------------------------------------------------------------------------
		final TerminalComponent<Event, Event> terminalComponent03 = Components.terminal(new Receiver(reference03, futureResponse));
		manager03.connect(router03, terminalComponent03);
		router03.filter(terminalComponent03.getComponentId(), reference03);
		RouterUtils.client(router03, manager03, new URI("tcp://localhost:6667"), reference02, reference01);
		// ------------------------------------------------------------------------------------------------
		final RegisteredUnitEcosystemKey key03 = EcosystemKeyFactory.key("03");
		ecosystem03.addBinder(key03, RouterUtils.serverBinder(router03, manager03));
		// ------------------------------------------------------------------------------------------------
		final NetServer routerServer03 = new NetServer();
		routerServer03.bind(new URI("tcp://localhost:6668"), linker(ecosystem03.getFactory(key03)));

		// ------------------------------------------------------------------------------------------------
		// Send simple events
		// ------------------------------------------------------------------------------------------------

		futureResponse.set(new FutureResponse<String>());
		terminalComponent01.getDownStreamDataHandler().handleData(new EventImpl("Hello , World from Client01!", reference03));
		assertEquals(reference03 + " - Hello , World from Client01!", futureResponse.get().get(10, TimeUnit.SECONDS));

		// Same but reuse opened connections ...
		futureResponse.set(new FutureResponse<String>());
		terminalComponent01.getDownStreamDataHandler().handleData(new EventImpl("Hello , World from Client01! again", reference03));
		assertEquals(reference03 + " - Hello , World from Client01! again", futureResponse.get().get(10, TimeUnit.SECONDS));

		// Reverse and Reuse already opened connections ...
		futureResponse.set(new FutureResponse<String>());
		terminalComponent03.getDownStreamDataHandler().handleData(new EventImpl("Hello , World from Client03!", reference01));
		assertEquals(reference01 + " - Hello , World from Client03!", futureResponse.get().get(10, TimeUnit.SECONDS));

		// ------------------------------------------------------------------------------------------------

		routerServer02.close();
		routerServer03.close();
		ecosystem02.close();
		ecosystem03.close();
	}
}
