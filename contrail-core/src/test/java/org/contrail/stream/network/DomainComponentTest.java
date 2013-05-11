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

package org.contrail.stream.network;

import junit.framework.TestCase;

import org.junit.Test;
import org.contrail.stream.component.Components;
import org.contrail.stream.component.bound.InitialComponent;
import org.contrail.stream.component.bound.TerminalComponent;
import org.contrail.stream.flow.BufferedDataFlow;
import org.contrail.stream.network.route.RouteAlreadyExistException;
import org.contrail.stream.network.component.DomainComponent;
import org.contrail.stream.network.packet.Packet;

/**
 * <code>RouterComponentTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DomainComponentTest {

	@Test
	public void shouldRetreivePacketInTransit() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);

		final DomainComponent domainComponent = givenADomainComponent();

		Components.compose(initial, domainComponent);

		initial.getUpStreamDataFlow().handleData(new Packet("b", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("a", nextData.getSourceId());
		TestCase.assertEquals("Hello, World!", nextData.getData());
	}

	@Test
	public void shouldRetreivePacketSendToDestination() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);

		final DomainComponent domainComponent = givenADomainComponent();

		Components.compose(initial, domainComponent);

		domainComponent.getDownStreamDataFlow().handleData(new Packet("b", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("a", nextData.getSourceId());
		TestCase.assertEquals("Hello, World!", nextData.getData());
	}

	@Test
	public void shouldRetreivePacketInDestination() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final TerminalComponent<Packet, Packet> terminal = Components.terminal(bufferedDataFlow);

		final DomainComponent domainComponent = givenADomainComponent();

		Components.compose(domainComponent, terminal);

		domainComponent.getUpStreamDataFlow().handleData(new Packet("a", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("a", nextData.getSourceId());
		TestCase.assertEquals("Hello, World!", nextData.getData());
	}

	@Test
	public void shouldRetreivePacketAlreadyInDestination() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final TerminalComponent<Packet, Packet> terminal = Components.terminal(bufferedDataFlow);
		final DomainComponent domainComponent = givenADomainComponent();

		Components.compose(domainComponent, terminal);

		terminal.getDownStreamDataFlow().handleData(new Packet("a", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("a", nextData.getSourceId());
		TestCase.assertEquals("Hello, World!", nextData.getData());
	}

	@Test
	public void shouldFailWhenDestinationisUndefined() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);
		final DomainComponent domainComponent = givenADomainComponent();

		Components.compose(initial, domainComponent);

		initial.getUpStreamDataFlow().handleData(new Packet("b", "c", "Hello, World!"));

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("b", nextData.getSourceId());
		TestCase.assertEquals("c", nextData.getDestinationId());
		TestCase.assertEquals("Hello, World!", nextData.getData());
	}

	// ---------------------------------------------------------------------------------------

	private DomainComponent givenADomainComponent() throws RouteAlreadyExistException {
		return new DomainComponent("a");
	}

}
