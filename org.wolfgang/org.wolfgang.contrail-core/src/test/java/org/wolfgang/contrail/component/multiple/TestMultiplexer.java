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

import java.io.IOException;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataReceiverFactory;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.link.ComponentsLink;
import org.wolfgang.contrail.link.ComponentsLinkManagerImpl;

/**
 * <code>TestMultiplexer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestMultiplexer extends TestCase {

	public void testMultiplexer01() {
		final TerminalComponent<Void, String> destination = new TerminalComponent<Void, String>(
				new DataReceiverFactory<Void, String>() {
					@Override
					public DataReceiver<Void> create(DataSender<String> component) {
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// Nothing
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final SourceComponent<Void, String> listener1 = new InitialComponent<Void, String>(
				new DataReceiverFactory<String, Void>() {
					@Override
					public DataReceiver<String> create(DataSender<Void> component) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws DataHandlerException {
								assertTrue(data.endsWith("Hello, World!"));
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final SourceComponent<Void, String> listener2 = new InitialComponent<Void, String>(
				new DataReceiverFactory<String, Void>() {
					@Override
					public DataReceiver<String> create(DataSender<Void> component) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws DataHandlerException {
								fail();
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final MultiplexerDataHandlerFactory<String> multiplexerFactory = new MultiplexerDataHandlerFactory<String>() {
			@Override
			public DownStreamDataHandler<String> create(FilteredSourceComponents<String> filteredDestination) {
				return new FilteredMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>(multiplexerFactory);

		final ComponentsLinkManagerImpl manager = new ComponentsLinkManagerImpl();
		try {
			manager.connect(multiplexer, destination);

			manager.connect(listener1, multiplexer);
			manager.connect(listener2, multiplexer);

			multiplexer.filterSource(listener1.getComponentId(), new DataFilter<String>() {
				@Override
				public boolean accept(String information) {
					return information.startsWith("@queue1:");
				}
			});

			multiplexer.filterSource(listener2.getComponentId(), new DataFilter<String>() {
				@Override
				public boolean accept(String information) {
					return information.startsWith("@queue2:");
				}
			});

			destination.getDataSender().sendData("@queue1:Hello, World!");

		} catch (ComponentConnectionRejectedException e) {
			fail();
		} catch (DataHandlerException e) {
			fail();
		}
	}

	public void testMultiplexer02() {
		final TerminalComponent<Void, String> destination = new TerminalComponent<Void, String>(
				new DataReceiverFactory<Void, String>() {
					@Override
					public DataReceiver<Void> create(DataSender<String> component) {
						return new DataReceiver<Void>() {
							@Override
							public void receiveData(Void data) throws DataHandlerException {
								// Nothing
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final MultiplexerDataHandlerFactory<String> multiplexerFactory = new MultiplexerDataHandlerFactory<String>() {
			@Override
			public DownStreamDataHandler<String> create(FilteredSourceComponents<String> filteredDestination) {
				return new FilteredMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>(multiplexerFactory);

		final ComponentsLinkManagerImpl manager = new ComponentsLinkManagerImpl();
		try {
			manager.connect(multiplexer, destination);
			manager.connect(multiplexer, destination);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testMultiplexer03() {
		final SourceComponent<Void, String> listener1 = new InitialComponent<Void, String>(
				new DataReceiverFactory<String, Void>() {
					@Override
					public DataReceiver<String> create(DataSender<Void> component) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws DataHandlerException {
								assertEquals("Hello, World!", data);
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final MultiplexerDataHandlerFactory<String> multiplexerFactory = new MultiplexerDataHandlerFactory<String>() {
			@Override
			public DownStreamDataHandler<String> create(FilteredSourceComponents<String> filteredDestination) {
				return new FilteredMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>(multiplexerFactory);

		final ComponentsLinkManagerImpl manager = new ComponentsLinkManagerImpl();
		try {
			manager.connect(listener1, multiplexer);
			manager.connect(listener1, multiplexer);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testMultiplexer03b() {
		final SourceComponent<Void, String> listener1 = new InitialComponent<Void, String>(
				new DataReceiverFactory<String, Void>() {
					@Override
					public DataReceiver<String> create(DataSender<Void> component) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws DataHandlerException {
								assertEquals("Hello, World!", data);
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final MultiplexerDataHandlerFactory<String> multiplexerFactory = new MultiplexerDataHandlerFactory<String>() {
			@Override
			public DownStreamDataHandler<String> create(FilteredSourceComponents<String> filteredDestination) {
				return new FilteredMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>(multiplexerFactory);

		try {
			multiplexer.connect(listener1);
			multiplexer.connect(listener1);
			fail();
		} catch (ComponentConnectionRejectedException e) {
			// OK
		}
	}

	public void testMultiplexer04() {
		final SourceComponent<Void, String> listener1 = new InitialComponent<Void, String>(
				new DataReceiverFactory<String, Void>() {
					@Override
					public DataReceiver<String> create(DataSender<Void> component) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws DataHandlerException {
								assertEquals("Hello, World!", data);
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final MultiplexerDataHandlerFactory<String> multiplexerFactory = new MultiplexerDataHandlerFactory<String>() {
			@Override
			public DownStreamDataHandler<String> create(FilteredSourceComponents<String> filteredDestination) {
				return new FilteredMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>(multiplexerFactory);

		final ComponentsLinkManagerImpl manager = new ComponentsLinkManagerImpl();
		try {
			ComponentsLink<Void, String> connect = manager.connect(listener1, multiplexer);
			connect.dispose();
		} catch (ComponentConnectionRejectedException e) {
			fail();
		} catch (ComponentDisconnectionRejectedException e) {
			fail();
		}
	}

	public void testMultiplexer05() {
		final SourceComponent<Void, String> listener1 = new InitialComponent<Void, String>(
				new DataReceiverFactory<String, Void>() {
					@Override
					public DataReceiver<String> create(DataSender<Void> component) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws DataHandlerException {
								assertTrue(data.endsWith("Hello, World!"));
							}

							@Override
							public void close() throws IOException {
								// Nothing
							}
						};
					}
				});

		final MultiplexerDataHandlerFactory<String> multiplexerFactory = new MultiplexerDataHandlerFactory<String>() {
			@Override
			public DownStreamDataHandler<String> create(FilteredSourceComponents<String> filteredDestination) {
				return new FilteredMultiplexerDataHandler<String>(filteredDestination);
			}
		};

		final MultiplexerComponent<Void, String> multiplexer = new MultiplexerComponent<Void, String>(multiplexerFactory);

		final ComponentsLinkManagerImpl manager = new ComponentsLinkManagerImpl();
		try {
			ComponentsLink<Void, String> connect = manager.connect(listener1, multiplexer);
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
