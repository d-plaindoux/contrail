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

package org.wolfgang.contrail.component.relay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.connector.ComponentsLink;
import org.wolfgang.contrail.connector.ComponentsLinkFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;

/**
 * <code>TestByteRelay</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestByteRelay extends TestCase {

	public void testNominal01() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);

			source.getDataSender().sendData("Hello,".getBytes());
			source.getDataSender().sendData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));
	}

	public void testNominal02() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);

			source.closeUpStream();
			destination.getDataSender().sendData("Hello,".getBytes());
			destination.getDataSender().sendData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));
	}

	public void testUpStreamClosed01() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);

			source.closeUpStream();
			source.getDataSender().sendData("Hello,".getBytes());

			interconnection.dispose();

			fail();
		} catch (UpStreamDataHandlerClosedException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void testUpStreamClosed02() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			ComponentsLinkFactory.connect(source, destination);

			destination.closeUpStream();
			source.getDataSender().sendData("Hello,".getBytes());

			fail();
		} catch (UpStreamDataHandlerClosedException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void tesDownStreamClosed01() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			ComponentsLinkFactory.connect(source, destination);

			source.closeDownStream();
			source.getDataSender().sendData("Hello,".getBytes());

			fail();
		} catch (DownStreamDataHandlerClosedException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void tesDownStreamClosed02() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			ComponentsLinkFactory.connect(source, destination);

			destination.closeDownStream();
			source.getDataSender().sendData("Hello,".getBytes());

			fail();
		} catch (DownStreamDataHandlerClosedException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void testSourceNotConnected() throws IOException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);

			source.getDataSender().sendData("Hello,".getBytes());

			fail();
		} catch (DataHandlerException e) {
			// OK
		} finally {
			output.close();
		}
	}

	public void testDestinationNotConnected() throws IOException {
		try {
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();

			destination.getUpStreamDataHandler().handleData("Hello,".getBytes());

			fail();
		} catch (DataHandlerException e) {
			// OK
		}
	}

	public void testAlreadyConnected01() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);

		final ByteArraySourceComponent source1 = new ByteArraySourceComponent(null);

		try {
			ComponentsLinkFactory.connect(source1, destination);
			fail();
		} catch (ComponentAlreadyConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	public void testAlreadyConnected02() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);

		final ByteArrayDestinationComponent destination1 = new ByteArrayDestinationComponent();

		try {
			ComponentsLinkFactory.connect(source, destination1);
			fail();
		} catch (ComponentAlreadyConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	public void testNotConnected01() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);
		interconnection.dispose();

		try {
			source.disconnect(destination);
			fail();
		} catch (ComponentNotYetConnectedException e) {
			// OK
		}
	}

	public void testNotConnected02() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);
		interconnection.dispose();

		try {
			destination.disconnect(source);
			fail();
		} catch (ComponentNotYetConnectedException e) {
			// OK
		}

	}

	public void testNotConnected03() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ByteArrayDestinationComponent destination1 = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);

		try {
			source.disconnect(destination1);
			fail();
		} catch (ComponentNotYetConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	public void testNotConnected04() throws ComponentAlreadyConnectedException, DataHandlerException, IOException,
			ComponentNotYetConnectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArraySourceComponent source1 = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[]> interconnection = ComponentsLinkFactory.connect(source, destination);

		try {
			destination.disconnect(source1);
			fail();
		} catch (ComponentNotYetConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}
}
