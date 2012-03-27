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

package org.wolfgang.contrail.component.multiple;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DestinationDataReceiverFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.SourceDataReceiverFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.data.DataInformation;
import org.wolfgang.contrail.data.DataInformationFactory;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataInformationValueAlreadyDefinedException;
import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentsLink;
import org.wolfgang.contrail.link.ComponentsLinkManager;

/**
 * <code>TestDeMultiplexer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestDeMultiplexer extends TestCase {

	public void testDeMultiplexer01() {
		final InitialComponent<DataWithInformation<String>, Void> source = new InitialComponent<DataWithInformation<String>, Void>(
				new SourceDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<Void> create(InitialComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// Nothing
							}
						};
					}
				});

		final DestinationComponent<DataWithInformation<String>, Void> listener1 = new TerminalComponent<DataWithInformation<String>, Void>(
				new DestinationDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final DestinationComponent<DataWithInformation<String>, Void> listener2 = new TerminalComponent<DataWithInformation<String>, Void>(
				new DestinationDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								fail();
							}
						};
					}
				});

		final DeMultiplexerDataHandlerFactory<String> deMultiplexerFactory = new DeMultiplexerDataHandlerFactory<String>() {
			@Override
			public UpStreamDataHandler<DataWithInformation<String>> create(
					FilteredDestinationComponents<String> filteredDestination) {
				return new FilteredDeMultiplexerDataHandler<String>(filteredDestination);
			}
		};
		
		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>(deMultiplexerFactory);

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(source, deMultiplexer);

			manager.connect(deMultiplexer, listener1);
			manager.connect(deMultiplexer, listener2);

			deMultiplexer.filterDestination(listener1.getComponentId(), new DataInformationFilter() {
				@Override
				public boolean accept(DataInformation information) {
					return information.hasValue("queue1", Object.class);
				}
			});

			deMultiplexer.filterDestination(listener2.getComponentId(), new DataInformationFilter() {
				@Override
				public boolean accept(DataInformation information) {
					return information.hasValue("queue2" + "", Object.class);
				}
			});

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

	public void testDeMultiplexer02() {
		final InitialComponent<DataWithInformation<String>, Void> source = new InitialComponent<DataWithInformation<String>, Void>(
				new SourceDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<Void> create(InitialComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// Nothing
							}
						};
					}
				});

		final DeMultiplexerDataHandlerFactory<String> deMultiplexerFactory = new DeMultiplexerDataHandlerFactory<String>() {
			@Override
			public UpStreamDataHandler<DataWithInformation<String>> create(
					FilteredDestinationComponents<String> filteredDestination) {
				return new FilteredDeMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>(deMultiplexerFactory);

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(source, deMultiplexer);
			manager.connect(source, deMultiplexer);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testDeMultiplexer03() {
		final DestinationComponent<DataWithInformation<String>, Void> listener1 = new TerminalComponent<DataWithInformation<String>, Void>(
				new DestinationDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final DeMultiplexerDataHandlerFactory<String> deMultiplexerFactory = new DeMultiplexerDataHandlerFactory<String>() {
			@Override
			public UpStreamDataHandler<DataWithInformation<String>> create(
					FilteredDestinationComponents<String> filteredDestination) {
				return new FilteredDeMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>(deMultiplexerFactory);

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(deMultiplexer, listener1);
			manager.connect(deMultiplexer, listener1);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testDeMultiplexer03b() {
		final DestinationComponent<DataWithInformation<String>, Void> listener1 = new TerminalComponent<DataWithInformation<String>, Void>(
				new DestinationDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});
		
		final DeMultiplexerDataHandlerFactory<String> deMultiplexerFactory = new DeMultiplexerDataHandlerFactory<String>() {
			@Override
			public UpStreamDataHandler<DataWithInformation<String>> create(
					FilteredDestinationComponents<String> filteredDestination) {
				return new FilteredDeMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>(deMultiplexerFactory);

		try {
			deMultiplexer.connect(listener1);
			deMultiplexer.connect(listener1);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testDeMultiplexer04() {
		final DestinationComponent<DataWithInformation<String>, Void> listener1 = new TerminalComponent<DataWithInformation<String>, Void>(
				new DestinationDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final DeMultiplexerDataHandlerFactory<String> deMultiplexerFactory = new DeMultiplexerDataHandlerFactory<String>() {
			@Override
			public UpStreamDataHandler<DataWithInformation<String>> create(
					FilteredDestinationComponents<String> filteredDestination) {
				return new FilteredDeMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>(deMultiplexerFactory);

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			final ComponentsLink<DataWithInformation<String>, Void> connect = manager.connect(deMultiplexer, listener1);
			connect.dispose();
		} catch (ComponentConnectionRejectedException e) {
			fail();
		} catch (ComponentDisconnectionRejectedException e) {
			fail();
		}
	}

	public void testDeMultiplexer05() {
		final DestinationComponent<DataWithInformation<String>, Void> listener1 = new TerminalComponent<DataWithInformation<String>, Void>(
				new DestinationDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							TerminalComponent<DataWithInformation<String>, Void> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final DeMultiplexerDataHandlerFactory<String> deMultiplexerFactory = new DeMultiplexerDataHandlerFactory<String>() {
			@Override
			public UpStreamDataHandler<DataWithInformation<String>> create(
					FilteredDestinationComponents<String> filteredDestination) {
				return new FilteredDeMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>(deMultiplexerFactory);

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			final ComponentsLink<DataWithInformation<String>, Void> connect = manager.connect(deMultiplexer, listener1);
			connect.dispose();
			connect.dispose();
			fail();
		} catch (ComponentConnectionRejectedException e) {
			fail();
		} catch (ComponentDisconnectionRejectedException e) {
			// OK
		}
	}
}
