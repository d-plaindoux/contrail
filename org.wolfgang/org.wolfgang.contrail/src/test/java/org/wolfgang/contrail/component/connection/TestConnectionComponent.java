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

package org.wolfgang.contrail.component.connection;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.component.core.InitialUpStreamSourceComponent;
import org.wolfgang.contrail.component.core.TerminalUpStreamDestinationComponent;
import org.wolfgang.contrail.component.core.TransformationBasedConnectionComponent;
import org.wolfgang.contrail.connector.ComponentsLink;
import org.wolfgang.contrail.connector.ComponentsLinkFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;

/**
 * <code>TestPipeline</code> is dedicated to transformation based pipeline test
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestConnectionComponent extends TestCase {

	public void testNominal01() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

		initial.getDataSender().sendData("3");
		assertEquals("9", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testNominal02() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

		initial.getDataSender().sendData("0");
		assertEquals("0", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testNominal03() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

		initial.closeUpStream();
		terminal.getDataSender().sendData(9);
		assertEquals("9", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testUpStreamClosed01() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

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

	public void testUpStreamClosed02() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

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

	public void testUpStreamClosed03() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

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

	public void testUpStreamClosed04() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

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

	public void testUpStreamClosed05() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

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

	public void testUpStreamClosed06() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

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

	public void testFailure() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException,
			DataHandlerException {
		final TransformationBasedConnectionComponent<String, Integer> connection = new TransformationBasedConnectionComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new StringSourceComponent(stringReference);
		final TerminalUpStreamDestinationComponent<Integer> terminal = new IntegerDestinationComponent();

		final ComponentsLink<String> initialConnection = ComponentsLinkFactory.connect(initial, connection);
		final ComponentsLink<Integer> terminalConnection = ComponentsLinkFactory.connect(connection, terminal);

		try {
			initial.getDataSender().sendData("NaN");
			fail();
		} catch (DataHandlerException h) {
			assertTrue(h.getCause().getCause() instanceof NumberFormatException);
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

}