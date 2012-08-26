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

import org.junit.Test;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataReceiverFactory;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestComponentEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestComponentEcosystem extends TestCase {

	@Test
	public void testNominal01() throws CannotProvideComponentException, CannotBindToComponentException, CannotCreateDataSenderException, DataHandlerException, IOException {

		final EcosystemImpl integrator = new EcosystemImpl();

		final DataReceiverFactory<String, String> dataFactory = new DataReceiverFactory<String, String>() {
			@Override
			public DataReceiver<String> create(final DataSender<String> sender) {
				return new DataReceiver<String>() {
					@Override
					public void receiveData(String data) throws DataHandlerException {
						sender.sendData(data);
					}

					@Override
					public void close() throws IOException {
						sender.close();
					}
				};
			}
		};

		final DataSenderFactory<String, String> destinationComponentFactory = new DataSenderFactory<String, String>() {
			@Override
			public DataSender<String> create(DataReceiver<String> receiver) throws CannotCreateDataSenderException {
				final InitialComponent<String, String> initialComponent = new InitialComponent<String, String>(receiver);
				final TerminalComponent<String, String> terminalComponent = new TerminalComponent<String, String>(dataFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataSenderException(e);
				}
				return initialComponent.getDataSender();
			}
		};

		integrator.addBinder(EcosystemKeyFactory.key("test", String.class, String.class), destinationComponentFactory);

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

		final UnitEcosystemKey namedKey = EcosystemKeyFactory.named("test");
		final DataSender<String> createInitial = integrator.<String, String> getBinder(namedKey).create(receiver);
		final String message = "Hello, World!";

		createInitial.sendData(message);

		assertEquals(message, stringReference.get());

		integrator.close();
	}
}
