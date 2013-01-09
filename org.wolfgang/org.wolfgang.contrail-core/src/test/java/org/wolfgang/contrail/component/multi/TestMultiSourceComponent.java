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

package org.wolfgang.contrail.component.multi;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.flow.BufferedDataFlow;
import org.wolfgang.contrail.flow.DataFlowFactory;
import org.wolfgang.contrail.flow.FilteredDataFlow.Acceptor;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.link.ComponentManager;

/**
 * <code>TestMultiDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestMultiSourceComponent {

	@Test
	public void shouldRetrieveAllDataWhenUsingMultiSource() throws ComponentConnectionRejectedException, DataFlowException {
		final String message = "Hello, World!";

		final MultiSourceComponent<String, String> multiComponent = new MultiSourceComponent<String, String>();

		final BufferedDataFlow<String> flow01 = new BufferedDataFlow<String>();
		final InitialComponent<String, String> initial01 = Components.initial(flow01);
		ComponentManager.connect(initial01, multiComponent);

		final BufferedDataFlow<String> flow02 = new BufferedDataFlow<String>();
		final InitialComponent<String, String> initial02 = Components.initial(flow02);
		ComponentManager.connect(initial02, multiComponent);

		multiComponent.getDownStreamDataFlow().handleData(message);

		TestCase.assertEquals(true, flow01.hasNextData());
		TestCase.assertEquals(message, flow01.getNextData());

		TestCase.assertEquals(true, flow02.hasNextData());
		TestCase.assertEquals(message, flow02.getNextData());
	}

	@Test
	public void shouldRetrieveOneDataWhenUsingFilteredMultiDestination() throws ComponentConnectionRejectedException, DataFlowException {
		final String message = "Hello, World !";

		final MultiSourceComponent<String, String> multiComponent = new MultiSourceComponent<String, String>();

		final Acceptor<String> filter01 = new Acceptor<String>() {
			@Override
			public boolean accept(String data) {
				return true;
			}
		};
		final BufferedDataFlow<String> flow01 = new BufferedDataFlow<String>();
		final InitialComponent<String, String> initial01 = Components.initial(DataFlowFactory.filtered(filter01, flow01));
		ComponentManager.connect(initial01, multiComponent);

		final Acceptor<String> filter02 = new Acceptor<String>() {
			@Override
			public boolean accept(String data) {
				return false;
			}
		};
		final BufferedDataFlow<String> flow02 = new BufferedDataFlow<String>();
		final InitialComponent<String, String> initial02 = Components.initial(DataFlowFactory.filtered(filter02, flow02));
		ComponentManager.connect(initial02, multiComponent);

		multiComponent.getDownStreamDataFlow().handleData(message);

		TestCase.assertEquals(true, flow01.hasNextData());
		TestCase.assertEquals(message, flow01.getNextData());

		TestCase.assertEquals(false, flow02.hasNextData());
	}
}
