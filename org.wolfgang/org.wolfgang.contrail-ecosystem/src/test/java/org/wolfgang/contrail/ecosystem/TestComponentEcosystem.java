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
import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialUpStreamDataHandler;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerAdapter;
import org.wolfgang.contrail.handler.StreamDataHandlerFactory;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerAdapter;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestComponentEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestComponentEcosystem extends TestCase {

	@Test
	public void testNominal01() throws CannotProvideComponentException, CannotBindToComponentException, CannotCreateDataHandlerException, DataHandlerException, IOException {

		final EcosystemImpl integrator = new EcosystemImpl();

		final UpStreamDataHandlerFactory<String, String> dataFactory = new UpStreamDataHandlerFactory<String, String>() {
			@Override
			public UpStreamDataHandler<String> create(final DownStreamDataHandler<String> sender) {
				return StreamDataHandlerFactory.<String> closable(new UpStreamDataHandlerAdapter<String>() {
					@Override
					public void handleData(String data) throws DataHandlerException {
						sender.handleData(data);
					}

					@Override
					public void handleClose() throws DataHandlerCloseException {
						sender.handleClose();
					}

					@Override
					public void handleLost() throws DataHandlerCloseException {
						super.handleLost();
						sender.handleLost();
					}
				});
			}
		};

		final UpStreamDataHandlerFactory<String, String> destinationComponentFactory = new UpStreamDataHandlerFactory<String, String>() {
			@Override
			public UpStreamDataHandler<String> create(DownStreamDataHandler<String> receiver) throws CannotCreateDataHandlerException {
				final InitialComponent<String, String> initialComponent = new InitialComponent<String, String>(receiver);
				final TerminalComponent<String, String> terminalComponent = new TerminalComponent<String, String>(dataFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataHandlerException(e);
				}
				return InitialUpStreamDataHandler.<String> create(initialComponent);
			}
		};

		integrator.addBinder(EcosystemKeyFactory.key("test", String.class, String.class), destinationComponentFactory);

		final AtomicReference<String> stringReference = new AtomicReference<String>();

		final DownStreamDataHandlerAdapter<String> receiver = new DownStreamDataHandlerAdapter<String>() {
			@Override
			public void handleData(String data) throws DataHandlerException {
				super.handleData(data);
				stringReference.set(data);
			}
		};

		final UnitEcosystemKey namedKey = EcosystemKeyFactory.named("test");
		final UpStreamDataHandler<String> createInitial = integrator.<String, String> getBinder(namedKey).create(receiver);
		final String message = "Hello, World!";

		createInitial.handleData(message);

		assertEquals(message, stringReference.get());

		integrator.close();
	}
}
