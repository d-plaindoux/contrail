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
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.data.DataInformation;
import org.wolfgang.contrail.data.DataInformationFactory;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataInformationValueAlreadyDefinedException;
import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentsLink;
import org.wolfgang.contrail.link.ComponentsLinkManager;

/**
 * <code>TestMultiplexer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestMultiple extends TestCase {

	public void testDeMultiplexer01() {
		final InitialComponent<DataWithInformation<String>, Void> source = new InitialComponent<DataWithInformation<String>, Void>(
				new InitialDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<Void> create(InitialComponent<DataWithInformation<String>, Void> component) {
						// TODO Auto-generated method stub
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// TODO
							}
						};
					}
				});

		final DestinationComponent<DataWithInformation<String>, Void> listener1 = new TerminalComponent<DataWithInformation<String>, Void>(
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
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
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
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

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>();

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(source, deMultiplexer);

			manager.connect(deMultiplexer, listener1);
			manager.connect(deMultiplexer, listener2);

			deMultiplexer.filter(listener1.getComponentId(), new DataInformationFilter() {
				@Override
				public boolean accept(DataInformation information) {
					return information.hasValue("queue1", Object.class);
				}
			});

			deMultiplexer.filter(listener2.getComponentId(), new DataInformationFilter() {
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
				new InitialDataReceiverFactory<DataWithInformation<String>, Void>() {
					@Override
					public DataReceiver<Void> create(InitialComponent<DataWithInformation<String>, Void> component) {
						// TODO Auto-generated method stub
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// TODO
							}
						};
					}
				});

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>();

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
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
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

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>();

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
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
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

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>();

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
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
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

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>();

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
				new TerminalDataReceiverFactory<DataWithInformation<String>, Void>() {
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

		final DeMultiplexerComponent<String, Void> deMultiplexer = new DeMultiplexerComponent<String, Void>();

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

	public void testMultiplexer01() {
		final TerminalComponent<Void, DataWithInformation<String>> destination = new TerminalComponent<Void, DataWithInformation<String>>(
				new TerminalDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<Void> create(TerminalComponent<Void, DataWithInformation<String>> component) {
						// TODO Auto-generated method stub
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// TODO
							}
						};
					}
				});

		final SourceComponent<Void, DataWithInformation<String>> listener1 = new InitialComponent<Void, DataWithInformation<String>>(
				new InitialDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							InitialComponent<Void, DataWithInformation<String>> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final SourceComponent<Void, DataWithInformation<String>> listener2 = new InitialComponent<Void, DataWithInformation<String>>(
				new InitialDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							InitialComponent<Void, DataWithInformation<String>> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								fail();
							}
						};
					}
				});

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>();

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(multiplexer, destination);

			manager.connect(listener1, multiplexer);
			manager.connect(listener2, multiplexer);

			multiplexer.filter(listener1.getComponentId(), new DataInformationFilter() {
				@Override
				public boolean accept(DataInformation information) {
					return information.hasValue("queue1", Object.class);
				}
			});

			multiplexer.filter(listener2.getComponentId(), new DataInformationFilter() {
				@Override
				public boolean accept(DataInformation information) {
					return information.hasValue("queue2" + "", Object.class);
				}
			});

			final DataInformation information = DataInformationFactory.createDataInformation();
			information.setValue("queue1", new Object());

			destination.getDataSender()
					.sendData(DataInformationFactory.createDataWithInformation(information, "Hello, World!"));

		} catch (ComponentConnectionRejectedException e) {
			fail();
		} catch (DataHandlerException e) {
			fail();
		} catch (DataInformationValueAlreadyDefinedException e) {
			fail();
		}
	}

	public void testMultiplexer02() {
		final TerminalComponent<Void, DataWithInformation<String>> destination = new TerminalComponent<Void, DataWithInformation<String>>(
				new TerminalDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<Void> create(TerminalComponent<Void, DataWithInformation<String>> component) {
						// TODO Auto-generated method stub
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// TODO
							}
						};
					}
				});

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>();

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(multiplexer, destination);
			manager.connect(multiplexer, destination);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testMultiplexer03() {
		final SourceComponent<Void, DataWithInformation<String>> listener1 = new InitialComponent<Void, DataWithInformation<String>>(
				new InitialDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							InitialComponent<Void, DataWithInformation<String>> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>();

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			manager.connect(listener1, multiplexer);
			manager.connect(listener1, multiplexer);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}


	public void testMultiplexer03b() {
		final SourceComponent<Void, DataWithInformation<String>> listener1 = new InitialComponent<Void, DataWithInformation<String>>(
				new InitialDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							InitialComponent<Void, DataWithInformation<String>> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>();

		try {
			multiplexer.connect(listener1);
			multiplexer.connect(listener1);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testMultiplexer04() {
		final SourceComponent<Void, DataWithInformation<String>> listener1 = new InitialComponent<Void, DataWithInformation<String>>(
				new InitialDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							InitialComponent<Void, DataWithInformation<String>> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>();

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			ComponentsLink<Void, DataWithInformation<String>> connect = manager.connect(listener1, multiplexer);
			connect.dispose();
		} catch (ComponentConnectionRejectedException e) {
			fail();
		} catch (ComponentDisconnectionRejectedException e) {
			fail();
		}
	}

	public void testMultiplexer05() {
		final SourceComponent<Void, DataWithInformation<String>> listener1 = new InitialComponent<Void, DataWithInformation<String>>(
				new InitialDataReceiverFactory<Void, DataWithInformation<String>>() {
					@Override
					public DataReceiver<DataWithInformation<String>> create(
							InitialComponent<Void, DataWithInformation<String>> component) {
						return new DataReceiver<DataWithInformation<String>>() {
							@Override
							public void receiveData(DataWithInformation<String> data) throws DataHandlerException {
								assertEquals("Hello, World!", data.getData());
							}
						};
					}
				});

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>();

		final ComponentsLinkManager manager = new ComponentsLinkManager();
		try {
			ComponentsLink<Void, DataWithInformation<String>> connect = manager.connect(listener1, multiplexer);
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
