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

package org.contrail.web.actor;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.contrail.actor.component.CoordinatorComponent;
import org.contrail.actor.core.ActorException;
import org.contrail.actor.core.Coordinator;
import org.contrail.actor.core.Coordinator.Proxy.Actor;
import org.contrail.actor.event.Request;
import org.contrail.common.concurrent.Promise;
import org.contrail.stream.component.CannotCreateComponentException;
import org.contrail.stream.component.Component;
import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.Components;
import org.contrail.stream.component.SourceComponent;
import org.contrail.web.notifier.SourceComponentNotifier;
import org.contrail.stream.component.pipeline.transducer.factory.BytesStringifierTransducerFactory;
import org.contrail.stream.component.pipeline.transducer.factory.ObjectTransducerFactory;
import org.contrail.stream.component.pipeline.transducer.factory.SerializationTransducerFactory;
import org.contrail.stream.data.JSonifier;
import org.contrail.web.connection.web.client.WebClient;
import org.contrail.web.connection.web.client.WebClientFactory;
import org.contrail.web.connection.web.content.ResourceWebContentProvider;
import org.contrail.web.connection.web.content.WebContentProvider;
import org.contrail.web.connection.web.server.WebServer;
import org.contrail.stream.network.component.DomainComponent;
import org.contrail.stream.network.packet.Packet;

/**
 * <code>NetworkActorTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkActorTest {

	@Test
	public void shouldReceiveResponseWithRemoteActorAndCorrectMessage() throws Exception {

		// Prepare domain "1"

		final Coordinator coordinator1 = new Coordinator().start();
		final CoordinatorComponent coordinatorComponent1 = new CoordinatorComponent(coordinator1,"1");
		final DomainComponent routeComponent1 = new DomainComponent(coordinatorComponent1.getDomainId());
		final Component compose1 = givenComposedComponent(coordinatorComponent1, routeComponent1);

		// Prepare domain "2"

		final Coordinator coordinator2 = new Coordinator().start();
		final CoordinatorComponent coordinatorComponent2 = new CoordinatorComponent(coordinator2,"2");
		final DomainComponent routeComponent2 = new DomainComponent(coordinatorComponent2.getDomainId());
		final Component compose2 = givenComposedComponent(coordinatorComponent2, routeComponent2);

		// Prepare server connection manager

		final SourceComponentNotifier serverSourceManager = new SourceComponentNotifier() {
			@Override
			public void accept(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, compose1); // Filter ?
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};
		final WebContentProvider contentProvider = new ResourceWebContentProvider();
		final WebServer server = WebServer.create(serverSourceManager, contentProvider).bind(8090);

		// Prepare client connection manager

		final Promise<SourceComponent<String, String>, Exception> clientSource = new Promise<SourceComponent<String, String>, Exception>() {
			@Override
			public void success(SourceComponent<String, String> source) {
				try {
					Components.compose(source, compose2); // Filter ?
				} catch (ComponentConnectionRejectedException e) {
					this.failure(e);
				}
			}

			public void failure(Exception e) {
				// TODO
			}
		};

		final WebClientFactory clientFactory = WebClientFactory.create();
		final WebClient connect = clientFactory.client(new URI("ws://localhost:8090/websocket")).connect(clientSource).awaitEstablishment();

		// Actor performance

		final A model = new A(42);
		coordinator1.actor("A").bindToObject(model);
		
		final Actor remoteA = coordinator2.domain("1").actor("A");

		final PromiseResponse response = new PromiseResponse();
		remoteA.ask(new Request("getValue"), response);

		TestCase.assertEquals(42, response.getFuture().get(10, TimeUnit.SECONDS));

		connect.close();
		server.close();
	}

	@Test(expected = ActorException.class)
	public void shouldReceiveErrorWithRemoteActorAndWrongMessage() throws Throwable {

		// Prepare domain "1"

		final Coordinator coordinator1 = new Coordinator().start();
		final CoordinatorComponent coordinatorComponent1 = new CoordinatorComponent(coordinator1,"1");
		final DomainComponent routeComponent1 = new DomainComponent(coordinatorComponent1.getDomainId());

		final Component compose1 = givenComposedComponent(coordinatorComponent1, routeComponent1);

		// Prepare domain "2"

		final Coordinator coordinator2 = new Coordinator().start();
		final CoordinatorComponent coordinatorComponent2 = new CoordinatorComponent(coordinator2,"2");
		final DomainComponent routeComponent2 = new DomainComponent(coordinatorComponent2.getDomainId());

		final Component compose2 = givenComposedComponent(coordinatorComponent2, routeComponent2);

		// Prepare server connection manager

		final SourceComponentNotifier serverSourceManager = new SourceComponentNotifier() {
			@Override
			public void accept(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, compose1); // Filter ?
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};
		final WebContentProvider contentProvider = new ResourceWebContentProvider();
		final WebServer server = WebServer.create(serverSourceManager, contentProvider).bind(8091);

		// Prepare client connection manager

		final Promise<SourceComponent<String, String>, Exception> clientSource = new Promise<SourceComponent<String, String>, Exception>() {
			@Override
			public void success(SourceComponent<String, String> source) {
				try {
					super.success(source);
					Components.compose(source, compose2); // Filter ?
				} catch (ComponentConnectionRejectedException e) {
					this.failure(e);
				}
			}
		};

		final WebClientFactory clientFactory = WebClientFactory.create();
		final WebClient connect = clientFactory.client(new URI("ws://localhost:8091/websocket")).connect(clientSource).awaitEstablishment();

		// Actor performance

		final A model = new A(42);
		coordinator1.actor("A").bindToObject(model);
		
		final Actor remoteA = coordinator2.domain("1").actor("A");

		final PromiseResponse response = new PromiseResponse();
		remoteA.ask(new Request("getWrongValue"), response);

		try {
			TestCase.assertEquals(42, response.getFuture().get(10, TimeUnit.SECONDS));
		} catch (ExecutionException e) {
			throw e.getCause();
		} finally {
			connect.close();
			server.close();
		}
	}

	// Private corner
	
	private Component givenComposedComponent(final CoordinatorComponent coordinatorComponent1,final DomainComponent routeComponent1) throws ComponentConnectionRejectedException {
		// Factories
		final BytesStringifierTransducerFactory stringifyFactory = new BytesStringifierTransducerFactory();
		final SerializationTransducerFactory serializationFactory = new SerializationTransducerFactory();
		final ObjectTransducerFactory objectFactory = new ObjectTransducerFactory(new ArrayList<JSonifier>() {
			private static final long serialVersionUID = 2065999814340836186L;
			{
				this.add(Packet.jSonifable());
			}
		});

		return Components.compose(stringifyFactory.createComponent(), serializationFactory.createComponent(), objectFactory.createComponent(), routeComponent1, coordinatorComponent1);
	}

}
