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
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialUpStreamDataFlow;
import org.wolfgang.contrail.component.pipeline.compose.CompositionComponents;
import org.wolfgang.contrail.component.pipeline.logger.LoggerDestinationComponent;
import org.wolfgang.contrail.component.pipeline.logger.LoggerSourceComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.component.router.RouterComponent;
import org.wolfgang.contrail.component.router.RouterSourceTable;
import org.wolfgang.contrail.component.router.SourceAcceptanceComponent;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ComponentFactory;
import org.wolfgang.contrail.connection.net.NetClient;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryAlreadyExistException;

/**
 * <code>NetworkRouterServerUtils</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class RouterSourceServerUtils extends TestCase {

	static void client(final RouterComponent component, final ComponentLinkManager componentLinkManager, final URI uri, final DirectReference mainReference, final DirectReference... references)
			throws ReferenceEntryAlreadyExistException {
		final RouterSourceTable.Entry entry = new RouterSourceTable.Entry() {
			@Override
			public SourceComponent<Event, Event> create() throws CannotCreateComponentException {
				try {
					// Pay-load component
					final PayLoadTransducerFactory payLoadTransducerFactory = new PayLoadTransducerFactory();
					final TransducerComponent<byte[], byte[], Bytes, Bytes> payLoadTransducer = payLoadTransducerFactory.createComponent();

					// Serialization component
					final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();
					final TransducerComponent<Bytes, Bytes, Object, Object> serialisationTransducer = serializationTransducerFactory.createComponent();

					// Coercion component
					final CoercionTransducerFactory<Event> coercionTransducerFactory = new CoercionTransducerFactory<Event>(Event.class);
					final TransducerComponent<Object, Object, Event, Event> coercionTransducer = coercionTransducerFactory.createComponent();

					// Create the link from the client to the network
					componentLinkManager.connect(payLoadTransducer, serialisationTransducer);
					componentLinkManager.connect(serialisationTransducer, coercionTransducer);
					componentLinkManager.connect(coercionTransducer, component);

					component.filter(coercionTransducer.getComponentId(), this.getReferenceToUse());

					final ComponentFactory factory = new ComponentFactory() {
						@Override
						public Component create() throws CannotCreateComponentException {
							return payLoadTransducer;
						}

						@Override
						public ComponentLinkManager getLinkManager() {
							return componentLinkManager;
						}
					};

					final Client netClient = new NetClient();
					netClient.connect(uri, factory);

					return coercionTransducer;
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				} catch (CannotCreateClientException e) {
					throw new CannotCreateComponentException(e);
				}
			}

			@Override
			public DirectReference getReferenceToUse() {
				return mainReference;
			}
		};

		component.getRouterSourceTable().insert(entry, mainReference, references);
	}

	static ComponentFactory serverBinder(final RouterComponent component, final ComponentLinkManagerImpl linkManager) {
		return new ComponentFactory() {
			@Override
			public Component create() throws CannotCreateComponentException {
				try {
					// Pay-load component
					final Component payLoadTransducer = new PayLoadTransducerFactory().createComponent();
					// Serialization component
					final Component serialisationTransducer = new SerializationTransducerFactory().createComponent();
					// Coercion component
					final Component coercionTransducer = new CoercionTransducerFactory<Event>(Event.class).createComponent();
					// Loggers 01 & 02
					final Component log01 = CompositionComponents.compose(linkManager, new LoggerSourceComponent("01.UP"), new LoggerDestinationComponent("01.DOWN"));
					final Component log02 = CompositionComponents.compose(linkManager, new LoggerSourceComponent("02.UP"), new LoggerDestinationComponent("02.DOWN"));
					// Source acceptance
					final SourceAcceptanceComponent acceptanceComponent = new SourceAcceptanceComponent();
					// Composed components
					return CompositionComponents.compose(linkManager, payLoadTransducer, log01, serialisationTransducer, log02, coercionTransducer, acceptanceComponent, component);
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
