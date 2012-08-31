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
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialUpStreamDataFlow;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestComponentEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestComponentEcosystem extends TestCase {

	@Test
	public void testNominal01() throws CannotProvideComponentException, CannotBindToComponentException, CannotCreateDataFlowException, DataFlowException, IOException {

		final EcosystemImpl integrator = new EcosystemImpl();

		final UpStreamDataFlowFactory<String, String> dataFactory = new UpStreamDataFlowFactory<String, String>() {
			@Override
			public UpStreamDataFlow<String> create(final DownStreamDataFlow<String> sender) {
				return DataFlows.<String> closable(new UpStreamDataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						sender.handleData(data);
					}

					@Override
					public void handleClose() throws DataFlowCloseException {
						sender.handleClose();
					}

					@Override
					public void handleLost() throws DataFlowCloseException {
						super.handleLost();
						sender.handleLost();
					}
				});
			}
		};

		final UpStreamDataFlowFactory<String, String> destinationComponentFactory = new UpStreamDataFlowFactory<String, String>() {
			@Override
			public UpStreamDataFlow<String> create(DownStreamDataFlow<String> receiver) throws CannotCreateDataFlowException {
				final InitialComponent<String, String> initialComponent = new InitialComponent<String, String>(receiver);
				final TerminalComponent<String, String> terminalComponent = new TerminalComponent<String, String>(dataFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataFlowException(e);
				}
				return InitialUpStreamDataFlow.<String> create(initialComponent);
			}
		};

		integrator.addBinder(EcosystemKeyFactory.key("test", String.class, String.class), destinationComponentFactory);

		final AtomicReference<String> stringReference = new AtomicReference<String>();

		final DownStreamDataFlowAdapter<String> receiver = new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				super.handleData(data);
				stringReference.set(data);
			}
		};

		final UnitEcosystemKey namedKey = EcosystemKeyFactory.named("test");
		final UpStreamDataFlow<String> createInitial = integrator.<String, String> getBinder(namedKey).create(receiver);
		final String message = "Hello, World!";

		createInitial.handleData(message);

		assertEquals(message, stringReference.get());

		integrator.close();
	}
}
