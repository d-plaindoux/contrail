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
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.bound.DataReceiverAdapter;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>TestEcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestEcosystemFactory extends TestCase {

	@Test
	public void testSample01() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample01_2.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DataReceiverAdapter<String> dataReceiver = new DataReceiverAdapter<String>() {
				@Override
				public void receiveData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			};

			final DataSenderFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final DataSender<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.sendData(message);

			assertEquals(message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample01bis() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample01bis_2.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DataReceiverAdapter<String> dataReceiver = new DataReceiverAdapter<String>() {
				@Override
				public void receiveData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			};

			final DataSenderFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final DataSender<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.sendData(message);

			assertEquals(message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample01ter() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample01ter_2.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DataReceiverAdapter<String> dataReceiver = new DataReceiverAdapter<String>() {
				@Override
				public void receiveData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			};

			final DataSenderFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final DataSender<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.sendData(message);

			assertEquals(message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSample02() {
		final URL resource = TestEcosystemFactory.class.getClassLoader().getResource("sample02_2.xml");

		assertNotNull(resource);

		try {
			final EcosystemModel decoded = EcosystemModel.decode(resource.openStream());
			final Ecosystem ecosystem = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), decoded);

			final FutureResponse<String> futureResponse = new FutureResponse<String>();
			final DataReceiverAdapter<String> dataReceiver = new DataReceiverAdapter<String>() {
				@Override
				public void receiveData(String data) throws DataHandlerException {
					futureResponse.setValue(data);
				}
			};

			final DataSenderFactory<String, String> binder = ecosystem.getBinder(EcosystemKeyFactory.named("Main"));
			final DataSender<String> sender = binder.create(dataReceiver);

			final String message = "Hello, World!";
			sender.sendData(message);

			assertEquals("RESENT|" + message, futureResponse.get(10, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
