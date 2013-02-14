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

package org.wolfgang.contrail.network;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.flow.BufferedDataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.network.component.RouteComponent;
import org.wolfgang.contrail.network.packet.Packet;
import org.wolfgang.contrail.network.route.RouteAlreadyExistException;
import org.wolfgang.contrail.network.route.RouteNotFoundException;
import org.wolfgang.contrail.network.route.RouteTable;

/**
 * <code>RouterComponentTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RouterComponentTest {

	@Test
	public void shouldRetreivePacketInTransit() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);

		final RouteComponent routeComponent = givenARouteComponent();

		Components.compose(initial, routeComponent);

		initial.getUpStreamDataFlow().handleData(new Packet("b", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("Hello, World!", nextData.getData());
		TestCase.assertEquals("ws://localhost/b", nextData.getEndPoint());
	}

	@Test
	public void shouldRetreivePacketSendToDestination() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);

		final RouteComponent routeComponent = givenARouteComponent();

		Components.compose(initial, routeComponent);

		routeComponent.getDownStreamDataFlow().handleData(new Packet("b", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("Hello, World!", nextData.getData());
		TestCase.assertEquals("ws://localhost/b", nextData.getEndPoint());
	}

	@Test
	public void shouldRetreivePacketInDestination() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final TerminalComponent<Packet, Packet> terminal = Components.terminal(bufferedDataFlow);

		final RouteComponent routeComponent = givenARouteComponent();

		Components.compose(routeComponent, terminal);

		routeComponent.getUpStreamDataFlow().handleData(new Packet("a", "Hello, World!").sendTo("ws://localhost/a"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("Hello, World!", nextData.getData());
		TestCase.assertEquals("ws://localhost/a", nextData.getEndPoint());
	}

	@Test
	public void shouldRetreivePacketAlreadyInDestination() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final TerminalComponent<Packet, Packet> terminal = Components.terminal(bufferedDataFlow);
		final RouteComponent routeComponent = givenARouteComponent();

		Components.compose(routeComponent, terminal);

		terminal.getDownStreamDataFlow().handleData(new Packet("a", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("Hello, World!", nextData.getData());
		TestCase.assertNull(nextData.getEndPoint());
	}

	@Test(expected = DataFlowException.class)
	public void shouldFailWhenDestinationisUndefined() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);
		final RouteComponent routeComponent = givenARouteComponent();

		Components.compose(initial, routeComponent);

		initial.getUpStreamDataFlow().handleData(new Packet("c", "Hello, World!"));
	}

	@Test(expected = RouteNotFoundException.class)
	public void shouldFailWhenDestinationisUndefinedWithRealCause() throws Throwable {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);
		final RouteComponent routeComponent = givenARouteComponent();

		Components.compose(initial, routeComponent);

		try {
			initial.getUpStreamDataFlow().handleData(new Packet("c", "Hello, World!"));
		} catch (DataFlowException e) {
			throw e.getCause();
		}
	}

	// ---------------------------------------------------------------------------------------

	private RouteComponent givenARouteComponent() throws RouteAlreadyExistException {
		final RouteTable routeTable = new RouteTable();
		routeTable.addRoute("b", "ws://localhost/b");
		final RouteComponent routeComponent = new RouteComponent(routeTable, "a");
		return routeComponent;
	}

}
