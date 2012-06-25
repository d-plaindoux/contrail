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

import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.core.ComponentIdImpl;
import org.wolgang.contrail.network.reference.DirectReference;
import org.wolgang.contrail.network.reference.ReferenceEntryAlreadyExistException;
import org.wolgang.contrail.network.reference.ReferenceEntryNotFoundException;
import org.wolgang.contrail.network.reference.ReferenceFactory;

/**
 * <code>TestNetworkComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkComponent extends TestCase {

	public void testNominal01() throws NoSuchAlgorithmException, ReferenceEntryNotFoundException,
			ReferenceEntryAlreadyExistException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.createClientReference(identifier);
		final NetworkTable networkRouterTable = new NetworkTable();
		final ComponentIdImpl componentId = new ComponentIdImpl(identifier);

		networkRouterTable.insert(clientReference, new NetworkTable.Entry() {
			@Override
			public ComponentId createDataHandler() {
				return componentId;
			}
		});

		assertEquals(componentId, networkRouterTable.retrieve(clientReference).createDataHandler());
	}

	public void testFailure01() throws NoSuchAlgorithmException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.createClientReference(identifier);
		final NetworkTable networkRouterTable = new NetworkTable();
		final ComponentIdImpl componentId = new ComponentIdImpl(identifier);

		try {
			networkRouterTable.insert(clientReference, new NetworkTable.Entry() {
				@Override
				public ComponentId createDataHandler() {
					return componentId;
				}
			});
		} catch (ReferenceEntryAlreadyExistException e1) {
			fail();
		}

		try {
			networkRouterTable.insert(clientReference, new NetworkTable.Entry() {
				@Override
				public ComponentId createDataHandler() {
					return componentId;
				}
			});
			fail();
		} catch (ReferenceEntryAlreadyExistException e) {
			// OK
		}
	}

	public void testFailure02() throws NoSuchAlgorithmException, ReferenceEntryAlreadyExistException {
		final UUID identifier = UUIDUtils.digestBased("Client");
		final DirectReference clientReference = ReferenceFactory.createClientReference(identifier);
		final NetworkTable networkRouterTable = new NetworkTable();
		final ComponentIdImpl componentId = new ComponentIdImpl(identifier);

		networkRouterTable.insert(clientReference, new NetworkTable.Entry() {
			@Override
			public ComponentId createDataHandler() {
				return componentId;
			}
		});

		final DirectReference somebodyReference = ReferenceFactory.createClientReference(UUIDUtils.digestBased("Somebody"));
		try {
			assertEquals(componentId, networkRouterTable.retrieve(somebodyReference).createDataHandler());
			fail();
		} catch (ReferenceEntryNotFoundException e) {
			// OK
		}
	}
}
