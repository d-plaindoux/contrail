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

package org.wolfgang.contrail.ecosystem.factory;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.bound.DataReceiverAdapter;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.key.NamedUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.model.EcosystemModel;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>TestEcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestEcosystemFactory extends TestCase {

	public void testSample01() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample01.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final EcosystemFactory ecosystemFactory = new EcosystemFactory();
			final Ecosystem ecosystem = ecosystemFactory.build(decoded);
			
			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DataReceiverAdapter<String> dataReceiver = new DataReceiverAdapter<String>() {
				@Override
				public void receiveData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			};

			final DataSenderFactory<String, String> binder = ecosystem.getBinder(new NamedUnitEcosystemKey("Main"));
			final DataSender<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.sendData(message);

			assertEquals(message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testSample02() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample02.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final EcosystemFactory ecosystemFactory = new EcosystemFactory();
			final Ecosystem ecosystem = ecosystemFactory.build(decoded);
			
			final FutureResponse<Bytes> futureResponse = new FutureResponse<Bytes>();
			final DataReceiverAdapter<Bytes> dataReceiver = new DataReceiverAdapter<Bytes>() {
				@Override
				public void receiveData(Bytes data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			};

			final DataSenderFactory<Bytes, Bytes> binder = ecosystem.getBinder(new NamedUnitEcosystemKey("Main"));
			final DataSender<Bytes> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			final SerializationTransducerFactory serialization = new SerializationTransducerFactory();
			final List<Bytes> transformed = serialization.getEncoder().transform(message);

			assertEquals(1, transformed.size());
			sender.sendData(transformed.get(0));

			final Bytes received = futureResponse.get(10, TimeUnit.SECONDS);
			final List<Object> response = serialization.getDecoder().transform(received);

			assertEquals(1, response.size());

			assertEquals(message, response.get(0));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testSample03() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample03.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final EcosystemFactory ecosystemFactory = new EcosystemFactory();
			final Ecosystem ecosystem = ecosystemFactory.build(decoded);
			
			final FutureResponse<byte[]> futureResponse = new FutureResponse<byte[]>();
			final DataReceiverAdapter<byte[]> dataReceiver = new DataReceiverAdapter<byte[]>() {
				@Override
				public void receiveData(byte[] data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			};

			final DataSenderFactory<byte[], byte[]> binder = ecosystem.getBinder(new NamedUnitEcosystemKey("Main"));
			final DataSender<byte[]> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			final SerializationTransducerFactory serialization = new SerializationTransducerFactory();
			final PayLoadTransducerFactory payload = new PayLoadTransducerFactory();
			final List<byte[]> transformed = payload.getEncoder().transform(serialization.getEncoder().transform(message).get(0));

			assertEquals(1, transformed.size());
			sender.sendData(transformed.get(0));

			final byte[] received = futureResponse.get(10, TimeUnit.SECONDS);
			final List<Object> response = serialization.getDecoder().transform(payload.getDecoder().transform(received).get(0));

			assertEquals(1, response.size());

			assertEquals(message, response.get(0));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
