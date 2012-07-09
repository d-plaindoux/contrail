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

package org.wolfgang.contrail.network.component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import junit.framework.TestCase;

import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.reference.DirectReference;
import org.wolfgang.contrail.network.reference.ReferenceEntryAlreadyExistException;
import org.wolfgang.contrail.network.reference.ReferenceEntryNotFoundException;
import org.wolfgang.contrail.network.reference.ReferenceFactory;

/**
 * <code>TestNetworkComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkComponent extends TestCase {

	private SourceComponent<NetworkEvent, NetworkEvent> getSourceComponent() {
		return new InitialComponent<NetworkEvent, NetworkEvent>(new DataReceiver<NetworkEvent>() {
			@Override
			public void close() throws IOException {
			}

			@Override
			public void receiveData(NetworkEvent data) throws DataHandlerException {
			}
		});
	}

	public void testNominal01() throws NoSuchAlgorithmException, ReferenceEntryNotFoundException, ReferenceEntryAlreadyExistException, CannotCreateComponentException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.createClientReference(identifier);
		final NetworkTable networkRouterTable = new NetworkTable();
		final SourceComponent<NetworkEvent, NetworkEvent> sourceComponent = getSourceComponent();
		final ComponentId componentId = sourceComponent.getComponentId();

		networkRouterTable.insert(new NetworkTable.Entry() {
			@Override
			public SourceComponent<NetworkEvent, NetworkEvent> create() {
				return sourceComponent;
			}

			@Override
			public DirectReference getReferenceToUse() {
				return clientReference;
			}
		}, clientReference);

		assertEquals(componentId, networkRouterTable.retrieve(clientReference).create().getComponentId());
	}

	public void testFailure01() throws NoSuchAlgorithmException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.createClientReference(identifier);
		final NetworkTable networkRouterTable = new NetworkTable();
		final SourceComponent<NetworkEvent, NetworkEvent> sourceComponent = getSourceComponent();

		try {
			networkRouterTable.insert(new NetworkTable.Entry() {
				@Override
				public SourceComponent<NetworkEvent, NetworkEvent> create() {
					return sourceComponent;
				}

				@Override
				public DirectReference getReferenceToUse() {
					return clientReference;
				}
			}, clientReference);
		} catch (ReferenceEntryAlreadyExistException e1) {
			fail();
		}

		try {
			networkRouterTable.insert(new NetworkTable.Entry() {
				@Override
				public SourceComponent<NetworkEvent, NetworkEvent> create() {
					return sourceComponent;
				}

				@Override
				public DirectReference getReferenceToUse() {
					return clientReference;
				}
			}, clientReference);
			fail();
		} catch (ReferenceEntryAlreadyExistException e) {
			// OK
		}
	}

	public void testFailure02() throws NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, CannotCreateComponentException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.createClientReference(identifier);
		final NetworkTable networkRouterTable = new NetworkTable();
		final SourceComponent<NetworkEvent, NetworkEvent> sourceComponent = getSourceComponent();
		final ComponentId componentId = sourceComponent.getComponentId();

		networkRouterTable.insert(new NetworkTable.Entry() {
			@Override
			public SourceComponent<NetworkEvent, NetworkEvent> create() {
				return sourceComponent;
			}

			@Override
			public DirectReference getReferenceToUse() {
				return clientReference;
			}
		}, clientReference);

		final DirectReference somebodyReference = ReferenceFactory.createClientReference(UUIDUtils.digestBased("Somebody"));
		try {
			assertEquals(componentId, networkRouterTable.retrieve(somebodyReference).create().getComponentId());
			fail();
		} catch (ReferenceEntryNotFoundException e) {
			// OK
		}
	}
}
