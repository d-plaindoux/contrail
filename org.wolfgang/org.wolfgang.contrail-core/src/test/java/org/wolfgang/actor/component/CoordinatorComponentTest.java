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

package org.wolfgang.actor.component;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.actor.core.ActorException;
import org.wolfgang.actor.core.Coordinator;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.data.ObjectRecord;
import org.wolfgang.network.component.TargetSelectorComponent;
import org.wolfgang.network.packet.Packet;

/**
 * <code>CoordinatorComponentTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CoordinatorComponentTest {

	public class PromiseResponse extends Promise<Object, ActorException> implements Response {
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
	public void shouldHaveResponseWithLocalActorInvokedAsRemote() throws Exception {
		final Coordinator coordinator = new Coordinator();
		coordinator.start();

		final A model = new A(42);
		coordinator.actor("A").bindToObject(model);

		final CoordinatorComponent coordinatorComponent = new CoordinatorComponent(coordinator);
		final TargetSelectorComponent routeComponent = new TargetSelectorComponent("a");

		Components.compose(routeComponent, coordinatorComponent);

		final PromiseResponse response = new PromiseResponse();
		final String responseId = coordinatorComponent.createResponseId(response);

		final ObjectRecord data = new ObjectRecord().set("identifier", "A").set("request", new Request("getValue")).set("response", responseId);
		final Packet packet = new Packet("a", "a", data, "ws://localhost:8090/a");

		routeComponent.getUpStreamDataFlow().handleData(packet);

		TestCase.assertEquals(42, response.getFuture().get(2, TimeUnit.SECONDS));
	}
}
