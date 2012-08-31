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

package org.wolfgang.contrail.ecosystem.lang;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerAdapter;
import org.wolfgang.contrail.handler.StreamDataHandlerFactory;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>TestEcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestEcosystemFactory extends TestCase {

	@Test
	public void testSample01() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample01.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DownStreamDataHandler<String> dataReceiver = StreamDataHandlerFactory.<String> create(new DownStreamDataHandlerAdapter<String>() {
				@Override
				public void handleData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.handleData(message);

			assertEquals(message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample02() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample02.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DownStreamDataHandler<String> dataReceiver = StreamDataHandlerFactory.<String> create(new DownStreamDataHandlerAdapter<String>() {
				@Override
				public void handleData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.handleData(message);

			assertEquals(message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample03() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample03.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DownStreamDataHandler<String> dataReceiver = StreamDataHandlerFactory.<String> create(new DownStreamDataHandlerAdapter<String>() {
				@Override
				public void handleData(String data) throws DataHandlerException {
					super.handleData(data);
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.handleData(message);

			assertEquals(message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample04() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample04.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DownStreamDataHandler<String> dataReceiver = StreamDataHandlerFactory.<String> create(new DownStreamDataHandlerAdapter<String>() {
				@Override
				public void handleData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.handleData(message);

			assertEquals("RESENT " + message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample05() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample05.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<Bytes> futureResponse = new FutureResponse<Bytes>();
			final DownStreamDataHandler<Bytes> dataReceiver = StreamDataHandlerFactory.<Bytes> create(new DownStreamDataHandlerAdapter<Bytes>() {
				@Override
				public void handleData(Bytes data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<Bytes, Bytes> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<Bytes> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			final SerializationTransducerFactory serialization = new SerializationTransducerFactory();
			final List<Bytes> transformed = serialization.getEncoder().transform(message);

			assertEquals(1, transformed.size());
			sender.handleData(transformed.get(0));

			final Bytes received = futureResponse.get(10, TimeUnit.SECONDS);
			final List<Object> response = serialization.getDecoder().transform(received);

			assertEquals(1, response.size());

			assertEquals(message, response.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample6() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample06.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<Bytes> futureResponse = new FutureResponse<Bytes>();
			final DownStreamDataHandler<Bytes> dataReceiver = StreamDataHandlerFactory.<Bytes> create(new DownStreamDataHandlerAdapter<Bytes>() {
				@Override
				public void handleData(Bytes data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<Bytes, Bytes> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<Bytes> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			final SerializationTransducerFactory serialization = new SerializationTransducerFactory();
			final List<Bytes> transformed = serialization.getEncoder().transform(message);

			assertEquals(1, transformed.size());
			sender.handleData(transformed.get(0));

			final Bytes received = futureResponse.get(10, TimeUnit.SECONDS);
			final List<Object> response = serialization.getDecoder().transform(received);

			assertEquals(1, response.size());

			assertEquals("RESENT " + message, response.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample07() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample07.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<Bytes> futureResponse = new FutureResponse<Bytes>();
			final DownStreamDataHandler<Bytes> dataReceiver = StreamDataHandlerFactory.<Bytes> create(new DownStreamDataHandlerAdapter<Bytes>() {
				@Override
				public void handleData(Bytes data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<Bytes, Bytes> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<Bytes> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			final SerializationTransducerFactory serialization = new SerializationTransducerFactory();
			final List<Bytes> transformed = serialization.getEncoder().transform(message);

			assertEquals(1, transformed.size());
			sender.handleData(transformed.get(0));

			final Bytes received = futureResponse.get(10, TimeUnit.SECONDS);
			final List<Object> response = serialization.getDecoder().transform(received);

			assertEquals(1, response.size());

			assertEquals("RESENT-3 " + message, response.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample08() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample08.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<byte[]> futureResponse = new FutureResponse<byte[]>();
			final DownStreamDataHandler<byte[]> dataReceiver = StreamDataHandlerFactory.<byte[]> create(new DownStreamDataHandlerAdapter<byte[]>() {
				@Override
				public void handleData(byte[] data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			});

			final UpStreamDataHandlerFactory<byte[], byte[]> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<byte[]> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			final SerializationTransducerFactory serialization = new SerializationTransducerFactory();
			final PayLoadTransducerFactory payload = new PayLoadTransducerFactory();
			final List<byte[]> transformed = payload.getEncoder().transform(serialization.getEncoder().transform(message).get(0));

			assertEquals(1, transformed.size());
			sender.handleData(transformed.get(0));

			final byte[] received = futureResponse.get(10, TimeUnit.SECONDS);
			final List<Object> response = serialization.getDecoder().transform(payload.getDecoder().transform(received).get(0));

			assertEquals(1, response.size());

			assertEquals(message, response.get(0));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample09() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample09.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final int nbEventSent = 250000;
			final FutureResponse<Integer> response = new FutureResponse<Integer>();
			final AtomicInteger futureReference = new AtomicInteger();

			final DownStreamDataHandler<byte[]> dataReceiver = StreamDataHandlerFactory.<byte[]> create(new DownStreamDataHandlerAdapter<byte[]>() {
				@Override
				public void handleData(byte[] data) throws DataHandlerException {
					if (futureReference.incrementAndGet() == nbEventSent) {
						response.setValue(nbEventSent);
					}
				}
			});

			final UpStreamDataHandlerFactory<byte[], byte[]> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final UpStreamDataHandler<byte[]> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			final SerializationTransducerFactory serialization = new SerializationTransducerFactory();
			final PayLoadTransducerFactory payload = new PayLoadTransducerFactory();
			final List<byte[]> transformed = payload.getEncoder().transform(serialization.getEncoder().transform(message).get(0));

			long t0 = System.currentTimeMillis();

			for (int i = 0; i < nbEventSent; i++) {
				assertEquals(1, transformed.size());
				sender.handleData(transformed.get(0));
			}

			System.err.println("Sending " + nbEventSent + " events in " + (System.currentTimeMillis() - t0) + "ms");

			assertEquals(new Integer(nbEventSent), response.get());

			System.err.println("Receiving " + nbEventSent + " events in " + (System.currentTimeMillis() - t0) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
