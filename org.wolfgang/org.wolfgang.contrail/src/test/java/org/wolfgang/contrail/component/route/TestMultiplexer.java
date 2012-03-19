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

package org.wolfgang.contrail.component.route;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.bound.InitialSourceComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.component.bound.TerminalDestinationComponent;
import org.wolfgang.contrail.component.multiple.DeMultiplexerComponent;
import org.wolfgang.contrail.data.DataInformation;
import org.wolfgang.contrail.data.DataInformationFactory;
import org.wolfgang.contrail.data.DataInformationValueAlreadyDefinedException;
import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentsLinkManager;

/**
 * <code>TestMultiplexer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestMultiplexer extends TestCase {

	public void testNominal01() {
		final InitialSourceComponent<DataWithInformation<String>, Void> source = new InitialSourceComponent<DataWithInformation<String>, Void>(
				new InitialDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<Void> create(InitialSourceComponent<DataWithInformation<String>, Void> component) {
						// TODO Auto-generated method stub
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// TODO
							}
						};
					}
				});

		final DataListener listener1 = new DataListener("queue1",
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalDestinationComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final DataListener listener2 = new DataListener("queue2",
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalDestinationComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								fail();
							}
						};
					}
				});

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>();

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(source, deMultiplexer);

			manager.connect(deMultiplexer, listener1);
			manager.connect(deMultiplexer, listener2);

			final DataInformation information = DataInformationFactory.createDataInformation();
			information.setValue("queue1", new Object());

			source.getDataSender().sendData(DataInformationFactory.createDataWithInformation(information, "Hello, World!"));

		} catch (ComponentConnectionRejectedException e) {
			fail();
		} catch (DataHandlerException e) {
			fail();
		} catch (DataInformationValueAlreadyDefinedException e) {
			fail();
		}
	}
}
