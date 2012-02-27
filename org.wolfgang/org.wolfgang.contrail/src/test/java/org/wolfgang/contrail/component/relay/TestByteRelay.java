/*
 * WolfGang Copyright (C)2012 D. Plaindoux.
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

import org.wolfgang.contrail.connector.impl.ComponentInterconnectionImpl;
import org.wolfgang.contrail.exception.ComponentAlreadyConnected;
import org.wolfgang.contrail.exception.ComponentNotYetConnected;
import org.wolfgang.contrail.handler.HandleDataException;

/**
 * <code>TestByteRelay</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestByteRelay extends TestCase {

	public void testNominal() throws ComponentAlreadyConnected, HandleDataException, IOException,
			ComponentNotYetConnected {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();
			final ComponentInterconnectionImpl<byte[]> interconnection = new ComponentInterconnectionImpl<byte[]>(
					source, destination);

			source.emitData("Hello,".getBytes());
			source.emitData(" World!".getBytes());

			interconnection.dispose();
		} finally {
			output.close();
		}

		assertEquals("Hello, World!", new String(output.toByteArray()));

	}

	public void testSourceNotConnected() throws IOException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			final ByteArraySourceComponent source = new ByteArraySourceComponent(output);

			source.emitData("Hello,".getBytes());

			fail();
		} catch (HandleDataException e) {
			// OK
		} finally {
			output.close();
		}
	}

	public void testDestinationNotConnected() throws IOException {

		try {
			final ByteArrayDestinationComponent destination = new ByteArrayDestinationComponent();

			destination.handleData(null, "Hello,".getBytes());

			fail();
		} catch (HandleDataException e) {
			// OK
		}
	}
		
}
