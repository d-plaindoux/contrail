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

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;
import org.wolfgang.contrail.link.ComponentsLink;
import org.wolfgang.contrail.link.ComponentsLinkManagerImpl;

/**
 * <code>TestByteRelay</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestByteRelay extends TestCase {

	public void testNominal01() throws DataHandlerException, IOException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);

			source.getDataSender().sendData("Hello,".getBytes());
			source.getDataSender().sendData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));
	}

	public void testNominal02() throws DataHandlerException, IOException, DataHandlerCloseException,
			ComponentDisconnectionRejectedException, ComponentConnectionRejectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);

			source.closeUpStream();
			destination.getDataSender().sendData("Hello,".getBytes());
			destination.getDataSender().sendData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));
	}

	public void testUpStreamClosed01() throws DataHandlerException, IOException, DataHandlerCloseException,
			ComponentConnectionRejectedException, ComponentDisconnectionRejectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);

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

	public void testUpStreamClosed02() throws DataHandlerException, IOException, ComponentNotConnectedException,
			DataHandlerCloseException, ComponentConnectionRejectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			new ComponentsLinkManagerImpl().connect(source, destination);

			destination.closeUpStream();
			source.getDataSender().sendData("Hello,".getBytes());

			fail();
		} catch (UpStreamDataHandlerClosedException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void tesDownStreamClosed01() throws DataHandlerException, IOException, ComponentNotConnectedException,
			DataHandlerCloseException, ComponentConnectionRejectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			new ComponentsLinkManagerImpl().connect(source, destination);

			source.closeDownStream();
			source.getDataSender().sendData("Hello,".getBytes());

			fail();
		} catch (DownStreamDataHandlerClosedException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void tesDownStreamClosed02() throws DataHandlerException, IOException, ComponentNotConnectedException,
			DataHandlerCloseException, ComponentConnectionRejectedException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			new ComponentsLinkManagerImpl().connect(source, destination);

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

	public void testAlreadyConnected01() throws DataHandlerException, IOException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);

		final ByteArraySourceComponent source1 = new ByteArraySourceComponent(null);

		try {
			new ComponentsLinkManagerImpl().connect(source1, destination);
			fail();
		} catch (ComponentConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	public void testAlreadyConnected02() throws DataHandlerException, IOException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);

		final ByteArrayDestinationComponent destination1 = new ByteArrayDestinationComponent();

		try {
			new ComponentsLinkManagerImpl().connect(source, destination1);
			fail();
		} catch (ComponentConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	public void testNotConnected01() throws DataHandlerException, IOException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);
		interconnection.dispose();

		try {
			source.disconnect(destination);
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}
	}

	public void testNotConnected02() throws DataHandlerException, IOException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);
		interconnection.dispose();

		try {
			destination.disconnect(source);
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}

	}

	public void testNotConnected03() throws DataHandlerException, IOException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ByteArrayDestinationComponent destination1 = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);

		try {
			source.disconnect(destination1);
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	public void testNotConnected04() throws DataHandlerException, IOException, ComponentConnectionRejectedException,
			ComponentDisconnectionRejectedException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArraySourceComponent source1 = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentsLink<byte[], byte[]> interconnection = new ComponentsLinkManagerImpl().connect(source, destination);

		try {
			destination.disconnect(source1);
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}
}
