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

package org.wolfgang.actor.core;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.concurrent.Promise;

/**
 * <code>CoordinatorTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CoordinatorTest {

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
	}

	@Test
	public void shouldHaveResponseWithLocalActorInvoked() throws Exception {
		final Coordinator coordinator = new Coordinator();

		final A model = new A(42);
		coordinator.actor("A").bindToObject(model);

		final PromiseResponse response = new PromiseResponse();
		final Request request = new Request("getValue");

		coordinator.invoke("A", request, response);

		TestCase.assertEquals(42, response.getFuture().get(2, TimeUnit.SECONDS));
	}

	@Test
	public void shouldHaveResponseWithSentMessageToLocalActor() throws Exception {
		final Coordinator coordinator = new Coordinator();
		coordinator.start();

		final A model = new A(42);
		coordinator.actor("A").bindToObject(model);

		final PromiseResponse response = new PromiseResponse();
		final Request request = new Request("getValue");

		coordinator.send("A", request, response);

		TestCase.assertEquals(42, response.getFuture().get(2, TimeUnit.SECONDS));
	}
}
