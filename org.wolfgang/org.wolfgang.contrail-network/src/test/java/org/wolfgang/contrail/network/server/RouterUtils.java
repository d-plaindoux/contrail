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

package org.wolfgang.contrail.network.server;

import java.net.URI;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.component.router.OnReceiveAcceptanceComponent;
import org.wolfgang.contrail.component.router.RouterComponent;
import org.wolfgang.contrail.component.router.RouterSourceTable;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.net.NetClient;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryAlreadyExistException;

/**
 * <code>NetworkRouterServerUtils</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class RouterUtils extends TestCase {

	static void client(final RouterComponent router, final ComponentLinkManager linkManager, final URI uri, final DirectReference mainReference, final DirectReference... references)
			throws ReferenceEntryAlreadyExistException {
		final RouterSourceTable.Entry entry = new RouterSourceTable.Entry() {
			@SuppressWarnings({ "resource" })
			@Override
			public SourceComponent<Event, Event> create() throws CannotCreateComponentException {
				try {
					// Pay-load component
					final Component payLoadTransducer = new PayLoadTransducerFactory().createComponent();
					// Serialization component
					final Component serialisationTransducer = new SerializationTransducerFactory().createComponent();
					// Coercion component
					final SourceComponent<Event, Event> coercionTransducer = new CoercionTransducerFactory<Event>(Event.class).createComponent();
					// Create the link from the client to the network
					final Component compose = Components.compose(linkManager, payLoadTransducer, serialisationTransducer, coercionTransducer, router);

					router.filter(coercionTransducer.getComponentId(), mainReference);

					final Component component = new NetClient().connect(uri);
					linkManager.connect(component, compose);

					return coercionTransducer;
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				} catch (CannotCreateClientException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		router.getRouterSourceTable().insert(entry, mainReference, references);
	}

	static ComponentFactory serverBinder(final RouterComponent router, final ComponentLinkManager linkManager) {
		return new ComponentFactory() {
			@Override
			public Component create(Object... arguments) throws CannotCreateComponentException {
				try {
					// Pay-load component
					final Component payLoadTransducer = new PayLoadTransducerFactory().createComponent();
					// Serialization component
					final Component serialisationTransducer = new SerializationTransducerFactory().createComponent();
					// Coercion component
					final Component coercionTransducer = new CoercionTransducerFactory<Event>(Event.class).createComponent();
					// Source acceptance
					final OnReceiveAcceptanceComponent acceptanceComponent = new OnReceiveAcceptanceComponent();
					// Composed components
					return Components.compose(linkManager, payLoadTransducer, serialisationTransducer, coercionTransducer, acceptanceComponent, router);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				} catch (Exception e) {
					throw new CannotCreateComponentException(e);
				}
			}

			@Override
			public ComponentLinkManager getLinkManager() {
				return linkManager;
			}
		};
	}
}
