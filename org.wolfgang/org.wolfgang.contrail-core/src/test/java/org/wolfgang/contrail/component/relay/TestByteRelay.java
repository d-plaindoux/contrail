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

import org.junit.Test;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandlerCloseException;
import org.wolfgang.contrail.handler.UpStreamDataHandlerCloseException;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestByteRelay</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestByteRelay extends TestCase {

	@Test
	public void testNominal01() throws DataHandlerException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataHandlerException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentLink interconnection = new ComponentLinkManagerImpl().connect(source, destination);

			source.getUpStreamDataHandler().handleData("Hello,".getBytes());
			source.getUpStreamDataHandler().handleData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));
	}

	@Test
	public void testNominal02() throws DataHandlerException, IOException, DataHandlerCloseException, ComponentDisconnectionRejectedException, ComponentConnectionRejectedException,
			CannotCreateDataHandlerException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentLink interconnection = new ComponentLinkManagerImpl().connect(source, destination);

			source.closeUpStream();
			destination.getDownStreamDataHandler().handleData("Hello,".getBytes());
			destination.getDownStreamDataHandler().handleData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));
	}

	@Test
	public void testUpStreamClosed01() throws DataHandlerException, IOException, DataHandlerCloseException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException,
			CannotCreateDataHandlerException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentLink interconnection = new ComponentLinkManagerImpl().connect(source, destination);

			source.closeUpStream();
			source.getUpStreamDataHandler().handleData("Hello,".getBytes());

			interconnection.dispose();

			fail();
		} catch (UpStreamDataHandlerCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	@Test
	public void testUpStreamClosed02() throws DataHandlerException, IOException, ComponentNotConnectedException, DataHandlerCloseException, ComponentConnectionRejectedException,
			CannotCreateDataHandlerException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			new ComponentLinkManagerImpl().connect(source, destination);

			destination.closeUpStream();
			source.getUpStreamDataHandler().handleData("Hello,".getBytes());

			fail();
		} catch (UpStreamDataHandlerCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void tesDownStreamClosed01() throws DataHandlerException, IOException, ComponentNotConnectedException, DataHandlerCloseException, ComponentConnectionRejectedException,
			CannotCreateDataHandlerException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			new ComponentLinkManagerImpl().connect(source, destination);

			source.closeDownStream();
			source.getUpStreamDataHandler().handleData("Hello,".getBytes());

			fail();
		} catch (DownStreamDataHandlerCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	@Test
	public void testDownStreamClosed02() throws DataHandlerException, IOException, ComponentNotConnectedException, DataHandlerCloseException, ComponentConnectionRejectedException,
			CannotCreateDataHandlerException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			new ComponentLinkManagerImpl().connect(source, destination);

			destination.closeDownStream();
			source.getUpStreamDataHandler().handleData("Hello,".getBytes());

			fail();
		} catch (DownStreamDataHandlerCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	@Test
	public void testSourceNotConnected() throws IOException, DataHandlerException, CannotCreateDataHandlerException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);

			source.getUpStreamDataHandler().handleData("Hello,".getBytes());

			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		} finally {
			output.close();
		}
	}

	@Test
	public void testDestinationNotConnected() throws IOException, CannotCreateDataHandlerException {
		try {
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();

			destination.getUpStreamDataHandler().handleData("Hello,".getBytes());

			fail();
		} catch (DataHandlerException e) {
			// OK
		}
	}

	@Test
	public void testAlreadyConnected01() throws DataHandlerException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataHandlerException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentLink interconnection = new ComponentLinkManagerImpl().connect(source, destination);

		final ByteArraySourceComponent source1 = new ByteArraySourceComponent(null);

		try {
			new ComponentLinkManagerImpl().connect(source1, destination);
			fail();
		} catch (ComponentConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	@Test
	public void testAlreadyConnected02() throws DataHandlerException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataHandlerException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentLink interconnection = new ComponentLinkManagerImpl().connect(source, destination);

		final ByteArrayDestinationComponent destination1 = new ByteArrayDestinationComponent();

		try {
			new ComponentLinkManagerImpl().connect(source, destination1);
			fail();
		} catch (ComponentConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	@Test
	public void testNotConnected01() throws DataHandlerException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataHandlerException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentLink interconnection = new ComponentLinkManagerImpl().connect(source, destination);
		interconnection.dispose();

		try {
			interconnection.dispose();
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}
	}

	@Test
	public void testNotConnected02() throws DataHandlerException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataHandlerException {

		final ByteArraySourceComponent source = new ByteArraySourceComponent(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final ComponentLink interconnection = new ComponentLinkManagerImpl().connect(source, destination);
		interconnection.dispose();

		try {
			interconnection.dispose();
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}

	}
}
