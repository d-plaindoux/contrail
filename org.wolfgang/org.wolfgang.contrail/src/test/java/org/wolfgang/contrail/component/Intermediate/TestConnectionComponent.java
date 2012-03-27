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

package org.wolfgang.contrail.component.Intermediate;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.transducer.TransducerBasedConnectionComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;
import org.wolfgang.contrail.link.ComponentsLink;
import org.wolfgang.contrail.link.ComponentsLinkManager;

/**
 * <code>TestPipeline</code> is dedicated to transformation based pipeline test
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestConnectionComponent extends TestCase {

	public void testNominal01() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		initial.getDataSender().sendData("3");
		assertEquals("9", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testNominal02() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		initial.getDataSender().sendData("0");
		assertEquals("0", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testNominal03() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException, DataHandlerCloseException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		initial.closeUpStream();
		terminal.getDataSender().sendData(9);
		assertEquals("9", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testUpStreamClosed01() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException, DataHandlerCloseException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			initial.closeUpStream();
			initial.getDataSender().sendData("0");
			fail();
		} catch (UpStreamDataHandlerClosedException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testUpStreamClosed02() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException, DataHandlerCloseException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			connection.closeUpStream();
			initial.getDataSender().sendData("0");
			fail();
		} catch (UpStreamDataHandlerClosedException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testUpStreamClosed03() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException, DataHandlerCloseException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			terminal.closeUpStream();
			initial.getDataSender().sendData("0");
			fail();
		} catch (UpStreamDataHandlerClosedException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testUpStreamClosed04() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException, DataHandlerCloseException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			initial.closeDownStream();
			initial.getDataSender().sendData("0");
			fail();
		} catch (DownStreamDataHandlerClosedException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testUpStreamClosed05() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException, DataHandlerCloseException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			connection.closeDownStream();
			initial.getDataSender().sendData("0");
			fail();
		} catch (DownStreamDataHandlerClosedException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testUpStreamClosed06() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException, DataHandlerCloseException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			terminal.closeDownStream();
			initial.getDataSender().sendData("0");
			fail();
		} catch (DownStreamDataHandlerClosedException h) {
			// Ok
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testFailure01() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);
		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			initial.getDataSender().sendData("NaN");
			fail();
		} catch (DataHandlerException h) {
			assertTrue(h.getCause().getCause() instanceof NumberFormatException);
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testFailure02() {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		try {
			connection.getUpStreamDataHandler().handleData("123");
			fail();
		} catch (DataHandlerException e) {
			// OK
		}
	}

	public void testFailure03() {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());
		try {
			connection.getDownStreamDataHandler().handleData(123);
			fail();
		} catch (DataHandlerException e) {
			// OK
		}
	}

	public void testFailure04() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialComponent<String, String> initial = new StringSourceComponent(stringReference);
		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String, String> initialConnection = new ComponentsLinkManager().connect(initial, connection);

		try {
			initial.getDataSender().sendData("123");
			fail();
		} catch (DataHandlerException e) {
			// OK
		}

		initialConnection.dispose();
	}

	public void testFailure05() throws DataHandlerException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {
		final TransducerBasedConnectionComponent<String, String, Integer, Integer> connection = new TransducerBasedConnectionComponent<String, String, Integer, Integer>(
				new StringToInteger(), new IntegerToString());

		final TerminalComponent<Integer, Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<Integer, Integer> terminalConnection = new ComponentsLinkManager().connect(connection, terminal);

		try {
			terminal.getDataSender().sendData(123);
			fail();
		} catch (DataHandlerException e) {
			// OK
		}

		terminalConnection.dispose();
	}
}