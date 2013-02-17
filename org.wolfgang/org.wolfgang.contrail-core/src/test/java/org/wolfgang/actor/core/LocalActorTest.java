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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;

/**
 * <code>LocalActorTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class LocalActorTest {

	public static class CA {
		private int value;

		private CA(int value) {
			super();
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}

	@Test
	public void shouldRetrieveAValueWhenInvokingActorMethod() throws Exception {
		final Coordinator coordinator = new Coordinator();
		final Actor actor = coordinator.actor("A").bindToObject(new CA(42));
		
		final Response response = new Response();
		actor.invoke(new Request("getValue"), response);
		TestCase.assertEquals(42, response.getFuture().get(1, TimeUnit.SECONDS));
	}

	@Test
	public void shouldRetrieveSetValueAValueWhenInvokingActorMethod() throws Exception {
		final Coordinator coordinator = new Coordinator();
		final Actor actor = coordinator.actor("A").bindToObject(new CA(0));

		final Response responseSet = new Response();
		actor.invoke(new Request("setValue", 42), responseSet);
		TestCase.assertEquals(null, responseSet.getFuture().get(1, TimeUnit.SECONDS));

		final Response responseGet = new Response();
		actor.invoke(new Request("getValue"), responseGet);
		TestCase.assertEquals(42, responseGet.getFuture().get(1, TimeUnit.SECONDS));
	}

	@Test(expected = ExecutionException.class)
	public void shouldRetrieveAnErrorWhenInvokingIncorrectActorMethod() throws Exception {
		final Coordinator coordinator = new Coordinator();
		final Actor actor = coordinator.actor("A").bindToObject(new CA(42));
		
		final Response response = new Response();
		actor.invoke(new Request("getWrongValue"), response);
		TestCase.assertEquals(42, response.getFuture().get(1, TimeUnit.SECONDS));
	}
}
