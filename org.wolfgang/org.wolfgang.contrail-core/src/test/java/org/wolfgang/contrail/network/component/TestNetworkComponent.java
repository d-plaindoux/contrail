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

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.component.router.RouterSourceTable;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryAlreadyExistException;
import org.wolfgang.contrail.reference.ReferenceEntryNotFoundException;
import org.wolfgang.contrail.reference.ReferenceFactory;

/**
 * <code>TestNetworkComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkComponent extends TestCase {

	private SourceComponent<Event, Event> getSourceComponent() {
		return Components.initial(new DownStreamDataFlowAdapter<Event>());
	}

	@Test
	public void testNominal01() throws NoSuchAlgorithmException, ReferenceEntryNotFoundException, ReferenceEntryAlreadyExistException, CannotCreateComponentException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.directReference(identifier);
		final RouterSourceTable networkRouterTable = new RouterSourceTable();
		final SourceComponent<Event, Event> sourceComponent = getSourceComponent();
		final ComponentId componentId = sourceComponent.getComponentId();

		networkRouterTable.insert(new RouterSourceTable.Entry() {
			@Override
			public SourceComponent<Event, Event> create() {
				return sourceComponent;
			}
		}, clientReference);

		assertEquals(componentId, networkRouterTable.retrieve(clientReference).create().getComponentId());
	}

	@Test
	public void testFailure01() throws NoSuchAlgorithmException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.directReference(identifier);
		final RouterSourceTable networkRouterTable = new RouterSourceTable();
		final SourceComponent<Event, Event> sourceComponent = getSourceComponent();

		try {
			networkRouterTable.insert(new RouterSourceTable.Entry() {
				@Override
				public SourceComponent<Event, Event> create() {
					return sourceComponent;
				}
			}, clientReference);
		} catch (ReferenceEntryAlreadyExistException e1) {
			fail();
		}

		try {
			networkRouterTable.insert(new RouterSourceTable.Entry() {
				@Override
				public SourceComponent<Event, Event> create() {
					return sourceComponent;
				}
			}, clientReference);
			fail();
		} catch (ReferenceEntryAlreadyExistException e) {
			// OK
		}
	}

	@Test
	public void testFailure02() throws NoSuchAlgorithmException, ReferenceEntryAlreadyExistException, CannotCreateComponentException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.directReference(identifier);
		final RouterSourceTable networkRouterTable = new RouterSourceTable();
		final SourceComponent<Event, Event> sourceComponent = getSourceComponent();
		final ComponentId componentId = sourceComponent.getComponentId();

		networkRouterTable.insert(new RouterSourceTable.Entry() {
			@Override
			public SourceComponent<Event, Event> create() {
				return sourceComponent;
			}
		}, clientReference);

		final DirectReference somebodyReference = ReferenceFactory.directReference(UUIDUtils.digestBased("Somebody"));
		try {
			assertEquals(componentId, networkRouterTable.retrieve(somebodyReference).create().getComponentId());
			fail();
		} catch (ReferenceEntryNotFoundException e) {
			// OK
		}
	}
}
