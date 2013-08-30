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

package org.contrail.stream.component.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicReference;

import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.ComponentDisconnectionRejectedException;
import org.contrail.stream.component.bound.InitialComponent;
import org.contrail.stream.component.bound.TerminalComponent;
import org.contrail.stream.component.pipeline.transducer.TransducerComponent;
import org.contrail.stream.flow.exception.CannotCreateDataFlowException;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.flow.exception.DataFlowException;
import org.contrail.stream.link.ComponentManager;
import org.contrail.stream.link.DisposableLink;
import org.junit.Test;

/**
 * <code>TestPipeline</code> is dedicated to transformation based pipeline test
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestConnectionComponent {

	@Test
	public void testNominal01() throws DataFlowException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {
		final TransducerComponent<String, String, Integer, Integer> connection = new TransducerComponent<String, String, Integer, Integer>(new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = StringSourceComponent.create(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

		initial.closeUpStream();
		terminal.getDownStreamDataFlow().handleData(9);
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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);
		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

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

		final DisposableLink initialConnection = ComponentManager.connect(initial, connection);

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

		final DisposableLink terminalConnection = ComponentManager.connect(connection, terminal);

		try {
			terminal.getDownStreamDataFlow().handleData(123);
			fail();
		} catch (DataFlowException e) {
			// OK
		}

		terminalConnection.dispose();
	}
}