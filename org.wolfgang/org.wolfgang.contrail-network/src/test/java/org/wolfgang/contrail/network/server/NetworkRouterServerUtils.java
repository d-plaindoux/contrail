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
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.codec.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.codec.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.transducer.TransducerComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.network.component.CannotCreateComponentException;
import org.wolfgang.contrail.network.component.NetworkComponent;
import org.wolfgang.contrail.network.component.NetworkTable;
import org.wolfgang.contrail.network.connection.socket.NetClient;
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.reference.DirectReference;

/**
 * <code>NetworkRouterServerUtils</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class NetworkRouterServerUtils extends TestCase {

	static NetworkTable.Entry clientBinder(final NetworkComponent component, final ComponentLinkManager componentLinkManager,
			final String host, final int port) {
		return new NetworkTable.Entry() {
			@Override
			public SourceComponent<NetworkEvent, NetworkEvent> create(DirectReference reference)
					throws CannotCreateComponentException {
				Logger.getAnonymousLogger().log(
						Level.INFO,
						component.getSelfReference() + " - Opening a client binder at " + host + ":" + port + " for "
								+ reference);
				try {
					// Payload component
					final PayLoadTransducerFactory payLoadTransducerFactory = new PayLoadTransducerFactory();
					final TransducerComponent<byte[], byte[], Bytes, Bytes> payLoadTransducer = new TransducerComponent<byte[], byte[], Bytes, Bytes>(
							payLoadTransducerFactory.getDecoder(), payLoadTransducerFactory.getEncoder());

					// Serialization component
					final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();
					final TransducerComponent<Bytes, Bytes, Object, Object> serialisationTransducer = new TransducerComponent<Bytes, Bytes, Object, Object>(
							serializationTransducerFactory.getDecoder(), serializationTransducerFactory.getEncoder());

					// Coercion component
					final CoercionTransducerFactory<NetworkEvent> coercionTransducerFactory = new CoercionTransducerFactory<NetworkEvent>(
							NetworkEvent.class);
					final TransducerComponent<Object, Object, NetworkEvent, NetworkEvent> coercionTransducer = new TransducerComponent<Object, Object, NetworkEvent, NetworkEvent>(
							coercionTransducerFactory.getDecoder(), coercionTransducerFactory.getEncoder());

					// Create the link from the client to the network
					componentLinkManager.connect(payLoadTransducer, serialisationTransducer);
					componentLinkManager.connect(serialisationTransducer, coercionTransducer);
					componentLinkManager.connect(coercionTransducer, component);

					component.filterSource(coercionTransducer.getComponentId(), reference);

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
		};
	}

	static DataSenderFactory<byte[], byte[]> serverBinder(final NetworkComponent component,
			final ComponentLinkManager componentLinkManager) {
		return new DataSenderFactory<byte[], byte[]>() {
			@Override
			public DataSender<byte[]> create(DataReceiver<byte[]> receiver) throws CannotCreateDataSenderException {
				Logger.getAnonymousLogger().log(Level.INFO, "Opening a server binder [Handshake]");
				// Payload component
				try {
					final PayLoadTransducerFactory payLoadTransducerFactory = new PayLoadTransducerFactory();
					final TransducerComponent<byte[], byte[], Bytes, Bytes> payLoadTransducer = new TransducerComponent<byte[], byte[], Bytes, Bytes>(
							payLoadTransducerFactory.getDecoder(), payLoadTransducerFactory.getEncoder());

					// Serialization component
					final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();
					final TransducerComponent<Bytes, Bytes, Object, Object> serialisationTransducer = new TransducerComponent<Bytes, Bytes, Object, Object>(
							serializationTransducerFactory.getDecoder(), serializationTransducerFactory.getEncoder());

					// Coercion component
					final CoercionTransducerFactory<NetworkEvent> coercionTransducerFactory = new CoercionTransducerFactory<NetworkEvent>(
							NetworkEvent.class);
					final TransducerComponent<Object, Object, NetworkEvent, NetworkEvent> coercionTransducer = new TransducerComponent<Object, Object, NetworkEvent, NetworkEvent>(
							coercionTransducerFactory.getDecoder(), coercionTransducerFactory.getEncoder());

					componentLinkManager.connect(payLoadTransducer, serialisationTransducer);
					componentLinkManager.connect(serialisationTransducer, coercionTransducer);

					final FutureResponse<ComponentLink> futureResponse = new FutureResponse<ComponentLink>();
					final TerminalComponent<NetworkEvent, NetworkEvent> handshake = new TerminalComponent<NetworkEvent, NetworkEvent>(
							new DataReceiver<NetworkEvent>() {
								@Override
								public void close() throws IOException {
									// TODO
								}

								@Override
								public void receiveData(NetworkEvent data) throws DataHandlerException {
									try {
										// Retrieve the component reference
										final DirectReference reference = data.getSender();
										Logger.getAnonymousLogger().log(Level.INFO,
												component.getSelfReference() + " - Accepting a client [" + reference + "]");
										// Re-set the established link
										futureResponse.get().dispose();
										componentLinkManager.connect(coercionTransducer, component);
										if (reference == null || reference.equals(component.getSelfReference())) {
											// Do not add a corresponding filter
											// Close the emit capable layer
											coercionTransducer.closeDownStream();
										} else {
											component.filterSource(coercionTransducer.getComponentId(), reference);
										}
										// Re-send the event now
										component.getDownStreamDataHandler().handleData(data);
									} catch (ComponentDisconnectionRejectedException e) {
										throw new DataHandlerException(e);
									} catch (ComponentConnectionRejectedException e) {
										throw new DataHandlerException(e);
									} catch (InterruptedException e) {
										throw new DataHandlerException(e);
									} catch (ExecutionException e) {
										throw new DataHandlerException(e.getCause());
									} catch (DataHandlerCloseException e) {
										throw new DataHandlerException(e.getCause());
									}
								}
							});

					futureResponse.setValue(componentLinkManager.connect(coercionTransducer, handshake));

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
