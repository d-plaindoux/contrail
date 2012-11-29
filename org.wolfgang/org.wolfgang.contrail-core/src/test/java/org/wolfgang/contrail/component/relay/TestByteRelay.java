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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.link.ComponentManager;
import org.wolfgang.contrail.link.DisposableLink;

/**
 * <code>TestByteRelay</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestByteRelay {

	@Test
	public void testNominal01() throws DataFlowException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final DisposableLink interconnection = ComponentManager.connect(source, destination);

			source.getUpStreamDataFlow().handleData("Hello,".getBytes());
			source.getUpStreamDataFlow().handleData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));
	}

	@Test
	public void testNominal02() throws DataFlowException, IOException, DataFlowCloseException, ComponentDisconnectionRejectedException, ComponentConnectionRejectedException,
			CannotCreateDataFlowException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final DisposableLink interconnection = ComponentManager.connect(source, destination);

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
	public void testUpStreamClosed01() throws DataFlowException, IOException, DataFlowCloseException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException,
			CannotCreateDataFlowException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final DisposableLink interconnection = ComponentManager.connect(source, destination);

			source.closeUpStream();
			source.getUpStreamDataFlow().handleData("Hello,".getBytes());

			interconnection.dispose();

			fail();
		} catch (DataFlowCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	@Test
	public void testUpStreamClosed02() throws DataFlowException, IOException, ComponentNotConnectedException, DataFlowCloseException, ComponentConnectionRejectedException,
			CannotCreateDataFlowException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			ComponentManager.connect(source, destination);

			destination.closeUpStream();
			source.getUpStreamDataFlow().handleData("Hello,".getBytes());

			fail();
		} catch (DataFlowCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	public void tesDownStreamClosed01() throws DataFlowException, IOException, ComponentNotConnectedException, DataFlowCloseException, ComponentConnectionRejectedException,
			CannotCreateDataFlowException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			ComponentManager.connect(source, destination);

			source.closeDownStream();
			source.getUpStreamDataFlow().handleData("Hello,".getBytes());

			fail();
		} catch (DataFlowCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	@Test
	public void testDownStreamClosed02() throws DataFlowException, IOException, ComponentNotConnectedException, DataFlowCloseException, ComponentConnectionRejectedException,
			CannotCreateDataFlowException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			ComponentManager.connect(source, destination);

			destination.closeDownStream();
			source.getUpStreamDataFlow().handleData("Hello,".getBytes());

			fail();
		} catch (DataFlowCloseException e) {
			// Ok
		} finally {
			output.close();
		}
	}

	@Test
	public void testSourceNotConnected() throws IOException, DataFlowException, CannotCreateDataFlowException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(output);

			source.getUpStreamDataFlow().handleData("Hello,".getBytes());

			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		} finally {
			output.close();
		}
	}

	@Test
	public void testDestinationNotConnected() throws IOException, CannotCreateDataFlowException {
		try {
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();

			destination.getUpStreamDataFlow().handleData("Hello,".getBytes());

			fail();
		} catch (DataFlowException e) {
			// OK
		}
	}

	@Test
	public void testAlreadyConnected01() throws DataFlowException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {

		final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final DisposableLink interconnection = ComponentManager.connect(source, destination);

		final InitialComponent<byte[], byte[]> source1 = ByteArraySourceComponent.create(null);

		try {
			ComponentManager.connect(source1, destination);
			fail();
		} catch (ComponentConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	@Test
	public void testAlreadyConnected02() throws DataFlowException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {

		final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final DisposableLink interconnection = ComponentManager.connect(source, destination);

		final ByteArrayDestinationComponent destination1 = new ByteArrayDestinationComponent();

		try {
			ComponentManager.connect(source, destination1);
			fail();
		} catch (ComponentConnectedException e) {
			// OK
		}

		interconnection.dispose();
	}

	@Test
	public void testNotConnected01() throws DataFlowException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {

		final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final DisposableLink interconnection = ComponentManager.connect(source, destination);
		interconnection.dispose();

		try {
			interconnection.dispose();
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}
	}

	@Test
	public void testNotConnected02() throws DataFlowException, IOException, ComponentConnectionRejectedException, ComponentDisconnectionRejectedException, CannotCreateDataFlowException {

		final InitialComponent<byte[], byte[]> source = ByteArraySourceComponent.create(null);
		final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
		final DisposableLink interconnection = ComponentManager.connect(source, destination);
		interconnection.dispose();

		try {
			interconnection.dispose();
			fail();
		} catch (ComponentNotConnectedException e) {
			// OK
		}

	}
}
