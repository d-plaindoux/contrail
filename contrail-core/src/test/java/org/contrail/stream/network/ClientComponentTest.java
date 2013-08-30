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

import org.contrail.stream.component.Components;
import org.contrail.stream.component.bound.InitialComponent;
import org.contrail.stream.component.bound.TerminalComponent;
import org.contrail.stream.flow.BufferedDataFlow;
import org.contrail.stream.network.component.ClientComponent;
import org.contrail.stream.network.packet.Packet;
import org.junit.Test;

/**
 * <code>RouterComponentTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClientComponentTest {

	@Test
	public void shouldAcceptDestinationWhenPacketIsSent() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);

		final ClientComponent clientComponent = givenAClientComponent();

		Components.compose(initial, clientComponent);

		clientComponent.getDownStreamDataFlow().handleData(new Packet("a", "b", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("Hello, World!", nextData.getData());

		TestCase.assertEquals(true, clientComponent.acceptDestinationId("b"));
	}

	@Test
	public void shouldAcceptSourceWhenPacketIsReceived() throws Exception {
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final TerminalComponent<Packet, Packet> terminal = Components.terminal(bufferedDataFlow);

		final ClientComponent clientComponent = givenAClientComponent();

		Components.compose(clientComponent, terminal);

		clientComponent.getUpStreamDataFlow().handleData(new Packet("a", "b", "Hello, World!"));

		TestCase.assertEquals(true, bufferedDataFlow.hasNextData());

		final Packet nextData = bufferedDataFlow.getNextData();
		TestCase.assertEquals("Hello, World!", nextData.getData());

		TestCase.assertEquals(true, clientComponent.acceptDestinationId("a"));
	}

	// ---------------------------------------------------------------------------------------

	private ClientComponent givenAClientComponent() {
		return new ClientComponent("ws://localhost/a");
	}

}
