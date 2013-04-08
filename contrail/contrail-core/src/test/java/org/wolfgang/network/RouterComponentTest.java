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

package org.wolfgang.network;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.flow.BufferedDataFlow;
import org.wolfgang.contrail.network.route.RouteTable;
import org.wolfgang.network.component.RouterComponent;
import org.wolfgang.network.packet.Packet;

/**
 * <code>RouterComponentTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RouterComponentTest {

	@Test
	public void shouldHaveClientComponentWhenAddingActiveRoute() throws Exception {
		final String endPoint = "ws://localhost/a";
		
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);

		final RouterComponent routerComponent = givenARouterComponent();

		routerComponent.addActiveRoute(initial, endPoint);

		TestCase.assertTrue(routerComponent.hasActiveRoute("DomainA", endPoint));
	}
	
	@Test
	public void shouldRemoveClientComponentWhenClosingAddingActiveRoute() throws Exception {
		final String endPoint = "ws://localhost/a";
		
		final BufferedDataFlow<Packet> bufferedDataFlow = new BufferedDataFlow<Packet>();
		final InitialComponent<Packet, Packet> initial = Components.initial(bufferedDataFlow);

		final RouterComponent routerComponent = givenARouterComponent();

		routerComponent.addActiveRoute(initial, endPoint);

		initial.closeUpStream();
		
		TestCase.assertFalse(routerComponent.hasActiveRoute("DomainA", endPoint));		
	}

	// ---------------------------------------------------------------------------------------

	private RouterComponent givenARouterComponent() {
		return new RouterComponent(new RouteTable());
	}

}
