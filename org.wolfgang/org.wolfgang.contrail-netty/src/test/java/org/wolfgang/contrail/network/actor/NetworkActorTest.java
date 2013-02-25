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

package org.wolfgang.contrail.network.actor;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.actor.component.CoordinatorComponent;
import org.wolfgang.actor.core.Coordinator;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.pipeline.transducer.factory.ObjectTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.factory.SerializationTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.factory.BytesStringifierTransducerFactory;
import org.wolfgang.contrail.contrail.ComponentSourceManager;
import org.wolfgang.contrail.data.JSonifier;
import org.wolfgang.contrail.network.connection.web.client.WebClient;
import org.wolfgang.contrail.network.connection.web.client.WebClient.Instance;
import org.wolfgang.contrail.network.connection.web.server.WebServer;
import org.wolfgang.contrail.network.route.RouteTable;
import org.wolfgang.network.component.RouteComponent;
import org.wolfgang.network.packet.Packet;

/**
 * <code>NetworkActorTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkActorTest {

	public class PromiseResponse extends Promise<Object> implements Response {
		// Nothing to be done
	}

	public static class A {
		private int value;

		public A(int value) {
			super();
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}

	@Test
	public void shouldReceiveResponseWithRemoteActors() throws Exception {

		final RouteTable routeTable = new RouteTable();
		routeTable.addRoute("1", "ws://localhost:8090/websocket/1");
		routeTable.addRoute("2", "ws://localhost:8090/websocket/2");

		// Factories
		final BytesStringifierTransducerFactory stringifyFactory = new BytesStringifierTransducerFactory();
		final SerializationTransducerFactory serializationFactory = new SerializationTransducerFactory();
		final ObjectTransducerFactory objectFactory = new ObjectTransducerFactory(new HashMap<String, JSonifier>() {
			{
				this.put(Packet.class.getName(), Packet.jSonifable());
				this.put(Request.class.getName(), Request.jSonifable());
			}
		});

		// Prepare domain "1"

		final Coordinator coordinator1 = new Coordinator().start();
		final CoordinatorComponent coordinatorComponent1 = new CoordinatorComponent(coordinator1);
		final RouteComponent routeComponent1 = new RouteComponent(routeTable, "1");
		final Component compose1 = Components.compose(
				stringifyFactory.createComponent(), serializationFactory.createComponent(), 
				objectFactory.createComponent(), routeComponent1, coordinatorComponent1);
		
		// Prepare domain "2"

		final Coordinator coordinator2 = new Coordinator().start();
		final CoordinatorComponent coordinatorComponent2 = new CoordinatorComponent(coordinator2);
		final RouteComponent routeComponent2 = new RouteComponent(routeTable, "2");
		final Component compose2 = Components.compose(
				stringifyFactory.createComponent(), serializationFactory.createComponent(), 
				objectFactory.createComponent(), routeComponent2, coordinatorComponent2);

		// Prepare server connection manager

		final ComponentSourceManager serverSourceManager = new ComponentSourceManager() {
			@Override
			public void attach(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, compose1); // Filter ?
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};
		final WebServer server = WebServer.create(serverSourceManager).bind(8090);

		// Prepare client connection manager

		final ComponentSourceManager clientSourceManager = new ComponentSourceManager() {
			@Override
			public void attach(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, compose2); // Filter ?
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebClient client = WebClient.create(clientSourceManager);
		final Instance connect = client.instance(new URI("ws://localhost:8090/websocket")).connect().awaitEstablishment();

		// Actor performance

		final A model = new A(42);
		coordinator1.actor("A").bindToObject(model);
		coordinator2.actor("A").bindToRemote("1");

		final PromiseResponse response = new PromiseResponse();
		coordinator2.send("A", new Request("getValue"), response);

		TestCase.assertEquals(42, response.getFuture().get(200, TimeUnit.SECONDS));

		connect.close();
		server.close();
	}

}
