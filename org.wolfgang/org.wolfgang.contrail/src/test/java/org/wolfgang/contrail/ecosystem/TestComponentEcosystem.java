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

package org.wolfgang.contrail.ecosystem;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>TestComponentIntegrator</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestComponentEcosystem extends TestCase {

	public void testNominal01() throws CannotProvideInitialComponentException, CannotIntegrateInitialComponentException {

		final ComponentEcosystemImpl integrator = new ComponentEcosystemImpl();

		final DestinationComponentFactory<String, String> destinationComponentFactory = new DestinationComponentFactory<String, String>() {
			@Override
			public DestinationComponent<String, String> create() {
				return new TerminalComponent<String, String>(new TerminalDataReceiverFactory<String, String>() {
					@Override
					public DataReceiver<String> create(final TerminalComponent<String, String> component) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws DataHandlerException {
								component.getDataSender().sendData(data);
							}

							@Override
							public void close() throws IOException {
								component.getDataSender().close();
							}
						};
					}
				});
			}
		};

		integrator.addDestinationFactory(String.class, String.class, destinationComponentFactory);

		final AtomicReference<String> stringReference = new AtomicReference<String>();

		final DataReceiver<String> receiver = new DataReceiver<String>() {
			@Override
			public void receiveData(String data) throws DataHandlerException {
				stringReference.set(data);
			}

			@Override
			public void close() throws IOException {
				// Nothing
			}
		};

		final DataSender<String> createInitial = integrator.createInitial(receiver, String.class, String.class);
		final String message = "Hello, World!";

		try {
			createInitial.sendData(message);
		} catch (DataHandlerException e) {
			fail();
		}

		assertEquals(message, stringReference.get());
	}
}
