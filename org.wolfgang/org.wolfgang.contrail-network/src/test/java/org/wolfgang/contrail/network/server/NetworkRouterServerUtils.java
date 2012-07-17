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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.network.NetworkAcceptanceComponent;
import org.wolfgang.contrail.component.network.NetworkComponent;
import org.wolfgang.contrail.component.network.NetworkTable;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.connection.net.NetClient;
import org.wolfgang.contrail.event.NetworkEvent;
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
class NetworkRouterServerUtils extends TestCase {

	static void client(final NetworkComponent component, final ComponentLinkManager componentLinkManager, final String host, final int port, final DirectReference mainReference,
			final DirectReference... references) throws ReferenceEntryAlreadyExistException {
		final NetworkTable.Entry entry = new NetworkTable.Entry() {
			@Override
			public SourceComponent<NetworkEvent, NetworkEvent> create() throws CannotCreateComponentException {

				System.err.println(component.getSelfReference() + " - Opening a client to " + this.getReferenceToUse() + " [endpoint=" + host + ":" + port + "]");
				try {
					// Pay-load component
					final PayLoadTransducerFactory payLoadTransducerFactory = new PayLoadTransducerFactory();
					final TransducerComponent<byte[], byte[], Bytes, Bytes> payLoadTransducer = payLoadTransducerFactory.createComponent();

					// Serialization component
					final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();
					final TransducerComponent<Bytes, Bytes, Object, Object> serialisationTransducer = serializationTransducerFactory.createComponent();

					// Coercion component
					final CoercionTransducerFactory<NetworkEvent> coercionTransducerFactory = new CoercionTransducerFactory<NetworkEvent>(NetworkEvent.class);
					final TransducerComponent<Object, Object, NetworkEvent, NetworkEvent> coercionTransducer = coercionTransducerFactory.createComponent();

					// Create the link from the client to the network
					componentLinkManager.connect(payLoadTransducer, serialisationTransducer);
					componentLinkManager.connect(serialisationTransducer, coercionTransducer);
					componentLinkManager.connect(coercionTransducer, component);

					component.filterSource(coercionTransducer.getComponentId(), this.getReferenceToUse());

					final NetClient netClient = new NetClient(new DataSenderFactory<byte[], byte[]>() {
						@Override
						public DataSender<byte[]> create(DataReceiver<byte[]> component) throws CannotCreateDataSenderException {
							// Initial component
							final InitialComponent<byte[], byte[]> initial = new InitialComponent<byte[], byte[]>(component);
							try {
								componentLinkManager.connect(initial, payLoadTransducer);
								return initial.getDataSender();
							} catch (ComponentConnectionRejectedException e) {
								throw new CannotCreateDataSenderException(e);
							}
						}
					});

					netClient.connect(InetAddress.getByName(host), port);

					return coercionTransducer;
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				} catch (UnknownHostException e) {
					throw new CannotCreateComponentException(e);
				} catch (IOException e) {
					throw new CannotCreateComponentException(e);
				} catch (CannotCreateDataSenderException e) {
					throw new CannotCreateComponentException(e);
				}
			}

			@Override
			public DirectReference getReferenceToUse() {
				return mainReference;
			}
		};

		component.getNetworkTable().insert(entry, mainReference, references);
	}

	static DataSenderFactory<byte[], byte[]> serverBinder(final NetworkComponent component, final ComponentLinkManagerImpl componentLinkManager) {
		return new DataSenderFactory<byte[], byte[]>() {
			@Override
			public DataSender<byte[]> create(DataReceiver<byte[]> receiver) throws CannotCreateDataSenderException {

				System.err.println(component.getSelfReference() + " - Accept a client [Entering handshake stage]");

				try {
					// Payload component
					// Pay-load component
					final PayLoadTransducerFactory payLoadTransducerFactory = new PayLoadTransducerFactory();
					final TransducerComponent<byte[], byte[], Bytes, Bytes> payLoadTransducer = payLoadTransducerFactory.createComponent();

					// Serialization component
					final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();
					final TransducerComponent<Bytes, Bytes, Object, Object> serialisationTransducer = serializationTransducerFactory.createComponent();

					// Coercion component
					final CoercionTransducerFactory<NetworkEvent> coercionTransducerFactory = new CoercionTransducerFactory<NetworkEvent>(NetworkEvent.class);
					final TransducerComponent<Object, Object, NetworkEvent, NetworkEvent> coercionTransducer = coercionTransducerFactory.createComponent();

					componentLinkManager.connect(payLoadTransducer, serialisationTransducer);
					componentLinkManager.connect(serialisationTransducer, coercionTransducer);

					final NetworkAcceptanceComponent networkAcceptanceComponent = new NetworkAcceptanceComponent();

					componentLinkManager.connect(coercionTransducer, networkAcceptanceComponent);
					componentLinkManager.connect(networkAcceptanceComponent, component);

					// Initial component

					final InitialComponent<byte[], byte[]> initial = new InitialComponent<byte[], byte[]>(receiver);
					componentLinkManager.connect(initial, payLoadTransducer);
					return initial.getDataSender();
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataSenderException(e);
				} catch (Exception e) {
					throw new CannotCreateDataSenderException(e);
				}
			}
		};
	}
}
