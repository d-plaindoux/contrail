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

package org.wolfgang.contrail.network.ecosystem;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.connection.net.NetServer;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.lang.EcosystemFactoryImpl;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestNetworkEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkEcosystem extends TestCase {

	@Test
	public void testSimpleCLientServer() throws JAXBException, IOException, Exception {
		final NetServer netServer = new NetServer();

		try {
			netServer.bind(new URI("tcp://0.0.0.0:6666"), new ComponentFactoryListener() {
				@Override
				public void notifyCreation(Component client) throws CannotCreateComponentException {
					final ComponentLinkManager manager = new ComponentLinkManagerImpl();
					try {
						final Component terminalComponent = Components.terminal(new UpStreamDataFlowFactory<byte[], byte[]>() {
							@Override
							public UpStreamDataFlow<byte[]> create(DownStreamDataFlow<byte[]> component) throws CannotCreateDataFlowException {
								return DataFlows.echoFrom(component);
							}
						});
						manager.connect(client, terminalComponent);
					} catch (CannotCreateDataFlowException e) {
						throw new CannotCreateComponentException(e);
					} catch (ComponentConnectionRejectedException e) {
						throw new CannotCreateComponentException(e);
					}
				}
			});

			// ----------------------------------------------------------------------------------------------------

			final Socket socket = new Socket("localhost", 6666);
			final String message = "Hello, World!";

			socket.getOutputStream().write(message.getBytes());
			socket.getOutputStream().flush();

			final byte[] buffer = new byte[1024];
			final int len = socket.getInputStream().read(buffer);

			assertEquals(message, new String(buffer, 0, len));

			socket.close();
		} finally {
			netServer.close();
		}
	}

	@Test
	public void testClient() throws JAXBException, IOException, Exception {
		final NetServer netServer = new NetServer();

		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02_0.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));

		try {
			netServer.bind(new URI("tcp://0.0.0.0:6666"), new ComponentFactoryListener() {
				@Override
				public void notifyCreation(Component client) throws CannotCreateComponentException {
					final ComponentLinkManager manager = new ComponentLinkManagerImpl();
					try {
						final Component terminalComponent = Components.terminal(new UpStreamDataFlowFactory<byte[], byte[]>() {
							@Override
							public UpStreamDataFlow<byte[]> create(DownStreamDataFlow<byte[]> component) throws CannotCreateDataFlowException {
								return DataFlows.echoFrom(component);
							}
						});
						manager.connect(client, terminalComponent);
					} catch (CannotCreateDataFlowException e) {
						throw new CannotCreateComponentException(e);
					} catch (ComponentConnectionRejectedException e) {
						throw new CannotCreateComponentException(e);
					}
				}
			});

			// ----------------------------------------------------------------------------------------------------

			final FutureResponse<String> response = new FutureResponse<String>();
			final TerminalComponent<byte[], byte[]> sender = Components.terminal(new UpStreamDataFlowAdapter<byte[]>() {
				@Override
				public void handleData(byte[] data) throws DataFlowException {
					response.setValue(new String(data));
				}
			});

			final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
			factory.getLinkManager().connect(factory.create(), sender);

			final String message = "Hello, World!";
			sender.getDownStreamDataHandler().handleData(message.getBytes());
			assertEquals(message, response.get(10, TimeUnit.SECONDS));
		} finally {
			netServer.close();
			ecosystem02.close();
		}
	}

	@Test
	public void testClient_Error() throws Exception {
		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02_0.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));

		try {
			final FutureResponse<String> response = new FutureResponse<String>();
			final TerminalComponent<String, String> sender = Components.terminal(new UpStreamDataFlowAdapter<String>() {
				@Override
				public void handleData(String data) throws DataFlowException {
					response.setValue(new String(data));
				}
			});

			try {
				final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
				factory.getLinkManager().connect(factory.create(), sender);
				fail();
			} catch (ComponentConnectionRejectedException e) {
				// OK
			} catch (CannotCreateComponentException e) {
				fail();
			}
		} finally {
			ecosystem02.close();
		}
	}

	@Test
	public void testServer() throws Exception {
		final URL resource = TestNetworkEcosystem.class.getClassLoader().getResource("sample01_0.xml");
		final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource.openStream()));

		try {
			final Socket socket = new Socket("localhost", 6666);
			final String message = "Hello, World!";

			socket.getOutputStream().write(message.getBytes());
			socket.getOutputStream().flush();

			final byte[] buffer = new byte[1024];
			final int len = socket.getInputStream().read(buffer);

			assertEquals(message, new String(buffer, 0, len));

			socket.close();

		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			ecosystem.close();
		}
	}

	@Test
	public void testClientServer() throws Exception {

		final URL resource01 = TestNetworkEcosystem.class.getClassLoader().getResource("sample01_0.xml");
		final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource01.openStream()));

		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02_0.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));

		try {
			final FutureResponse<String> response = new FutureResponse<String>();
			final TerminalComponent<byte[], byte[]> sender = Components.terminal(new UpStreamDataFlowAdapter<byte[]>() {
				@Override
				public void handleData(byte[] data) throws DataFlowException {
					response.setValue(new String(data));
				}
			});

			final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
			factory.getLinkManager().connect(factory.create(), sender);

			final String message = "Hello, World!";
			sender.getDownStreamDataHandler().handleData(message.getBytes());
			assertEquals(message, response.get(10, TimeUnit.SECONDS));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			ecosystem01.close();
			ecosystem02.close();
		}
	}

	@Test
	public void testClientServer_1() throws Exception {

		final URL resource01 = TestNetworkEcosystem.class.getClassLoader().getResource("sample01_1.xml");
		final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource01.openStream()));

		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02_1.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));

		try {
			final FutureResponse<String> response = new FutureResponse<String>();
			final TerminalComponent<String, String> sender = Components.terminal(new UpStreamDataFlowAdapter<String>() {
				@Override
				public void handleData(String data) throws DataFlowException {
					response.setValue(data);
				}
			});

			final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
			factory.getLinkManager().connect(factory.create(), sender);

			final String message = "Hello, World!";
			sender.getDownStreamDataHandler().handleData(message);
			assertEquals(message, response.get(10, TimeUnit.SECONDS));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			ecosystem01.close();
			ecosystem02.close();
		}
	}

	@Test
	public void testClientsServer_1() throws Exception {

		final URL resource01 = TestNetworkEcosystem.class.getClassLoader().getResource("sample01_1.xml");
		final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource01.openStream()));

		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02_1.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));

		try {
			final FutureResponse<String>[] responses = new FutureResponse[100];

			for (int i = 0; i < responses.length; i++) {
				responses[i] = new FutureResponse<String>();
				final FutureResponse<String> response1 = responses[i];
				final TerminalComponent<String, String> sender1 = Components.terminal(new UpStreamDataFlowFactory<String, String>() {
					@Override
					public UpStreamDataFlow<String> create(final DownStreamDataFlow<String> component) throws CannotCreateDataFlowException {
						return new UpStreamDataFlowAdapter<String>() {
							@Override
							public void handleData(String data) throws DataFlowException {
								response1.setValue(data);
								component.handleClose();
							}
						};
					}
				});

				final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
				factory.getLinkManager().connect(factory.create(), sender1);

				final String message1 = "Hello, World! [" + i + "]";
				sender1.getDownStreamDataHandler().handleData(message1);
			}

			for (int i = 0; i < responses.length; i++) {
				final String message1 = "Hello, World! [" + i + "]";
				assertEquals(message1, responses[i].get(10, TimeUnit.SECONDS));
			}
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			ecosystem01.close();
			ecosystem02.close();
		}
	}

	@Test
	public void testClientServer_parallel_1() throws Exception {

		final URL resource01 = TestNetworkEcosystem.class.getClassLoader().getResource("sample01_1.xml");
		final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource01.openStream()));

		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02_1.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));

		try {
			final int nbEventSent = 50000;
			final FutureResponse<Integer> response = new FutureResponse<Integer>();
			final AtomicInteger futureReference = new AtomicInteger();

			final TerminalComponent<String, String> sender = Components.terminal(new UpStreamDataFlowAdapter<String>() {
				@Override
				public void handleData(String data) throws DataFlowException {
					if (futureReference.incrementAndGet() == nbEventSent) {
						response.setValue(nbEventSent);
					}
				}
			});

			final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
			factory.getLinkManager().connect(factory.create(), sender);

			final String message = "Hello, World!";

			long t0 = System.currentTimeMillis();

			for (int i = 0; i < nbEventSent; i++) {
				sender.getDownStreamDataHandler().handleData(message);
			}

			System.err.println("Sending " + nbEventSent + " events in " + (System.currentTimeMillis() - t0) + "ms");

			assertEquals(new Integer(nbEventSent), response.get());

			System.err.println("Sending+Receiving " + nbEventSent + " events in " + (System.currentTimeMillis() - t0) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			ecosystem01.close();
			ecosystem02.close();
		}
	}

	@Test
	public void testClientServer_parallel_2() throws Exception {

		final URL resource01 = TestNetworkEcosystem.class.getClassLoader().getResource("sample01_1.xml");
		final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource01.openStream()));

		final URL resource02 = TestNetworkEcosystem.class.getClassLoader().getResource("sample02_1.xml");
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), EcosystemModel.decode(resource02.openStream()));

		try {

			final int nbEventSent = 10000;
			final FutureResponse<Integer> response = new FutureResponse<Integer>();
			final AtomicInteger futureReference = new AtomicInteger();

			final UpStreamDataFlowFactory<String, String> senderFactory = new UpStreamDataFlowFactory<String, String>() {
				@Override
				public UpStreamDataFlow<String> create(final DownStreamDataFlow<String> downStream) throws CannotCreateDataFlowException {
					return new UpStreamDataFlowAdapter<String>() {
						@Override
						public void handleData(String data) throws DataFlowException {
							final int value = futureReference.incrementAndGet();
							if (value == nbEventSent) {
								response.setValue(nbEventSent);
							}
							downStream.handleClose();
						}
					};
				}
			};

			final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
			final String message = "Hello, World!";
			long t0 = System.currentTimeMillis();

			for (int i = 0; i < nbEventSent; i++) {
				final TerminalComponent<String, String> sender = Components.terminal(senderFactory);
				factory.getLinkManager().connect(factory.create(), sender);
				try {
					sender.getDownStreamDataHandler().handleData(message);
				} catch (DataFlowException e) {
					System.err.println("Error -- > " + e.getMessage());
				}
			}

			System.err.println("Sending " + nbEventSent + " events in " + (System.currentTimeMillis() - t0) + "ms");

			assertEquals(new Integer(nbEventSent), response.get());

			System.err.println("Sending+Receiving " + nbEventSent + " events in " + (System.currentTimeMillis() - t0) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			ecosystem01.close();
			ecosystem02.close();
		}
	}
}
