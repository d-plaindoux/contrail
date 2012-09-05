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

package org.wolfgang.contrail.component.pipeline;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestPipeline</code> is dedicated to transformation based pipeline test
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestConnectionComponent extends TestCase {

	@Test
	public void testNominal01() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		initial.getUpStreamDataFlow().handleData("3");
		assertEquals("9", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testNominal02() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		initial.getUpStreamDataFlow().handleData("0");
		assertEquals("0", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testNominal03() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, DataFlowCloseException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		initial.closeUpStream();
		terminal.getDownStreamDataHandler().handleData(9);
		assertEquals("9", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testUpStreamClosed01() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, DataFlowCloseException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			initial.closeUpStream();
			initial.getUpStreamDataFlow().handleData("0");
			fail();
		} catch (DataFlowCloseException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testUpStreamClosed02() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, DataFlowCloseException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			connection.closeUpStream();
			initial.getUpStreamDataFlow().handleData("0");
			fail();
		} catch (DataFlowCloseException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testUpStreamClosed03() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, DataFlowCloseException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			terminal.closeUpStream();
			initial.getUpStreamDataFlow().handleData("0");
			fail();
		} catch (DataFlowCloseException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testUpStreamClosed04() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, DataFlowCloseException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			initial.closeDownStream();
			initial.getUpStreamDataFlow().handleData("0");
			fail();
		} catch (DataFlowCloseException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testUpStreamClosed05() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, DataFlowCloseException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			connection.closeDownStream();
			initial.getUpStreamDataFlow().handleData("0");
			fail();
		} catch (DataFlowCloseException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testUpStreamClosed06() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, DataFlowCloseException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			terminal.closeDownStream();
			initial.getUpStreamDataFlow().handleData("0");
			fail();
		} catch (DataFlowCloseException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testFailure01() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);
		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			initial.getUpStreamDataFlow().handleData("NaN");
			fail();
		} catch (DataFlowException h) {
			assertTrue(h.getCause().getCause() instanceof NumberFormatException);
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	@Test
	public void testFailure02() {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		try {
			connection.getUpStreamDataFlow().handleData("123");
			fail();
		} catch (DataFlowException e) {
			// OK
		}
	}

	@Test
	public void testFailure03() {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());
		try {
			connection.getDownStreamDataFlow().handleData(123);
			fail();
		} catch (DataFlowException e) {
			// OK
		}
	}

	@Test
	public void testFailure04() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);

		final ComponentLink initialConnection = new ComponentLinkManagerImpl().connect(initial, connection);

		try {
			initial.getUpStreamDataFlow().handleData("123");
			fail();
		} catch (DataFlowException e) {
			// OK
		}

		initialConnection.dispose();
	}

	@Test
	public void testFailure05() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentLink terminalConnection = new ComponentLinkManagerImpl().connect(connection, terminal);

		try {
			terminal.getDownStreamDataHandler().handleData(123);
			fail();
		} catch (DataFlowException e) {
			// OK
		}

		terminalConnection.dispose();
	}
}