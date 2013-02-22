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

package org.wolfgang.contrail.network.connection.web.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDataFlowFactory;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.contrail.ComponentSourceManager;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowAdapter;
import org.wolfgang.contrail.flow.exception.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.network.connection.web.client.WebClient;
import org.wolfgang.contrail.network.connection.web.client.WebClient.Instance;
import org.wolfgang.contrail.network.connection.web.server.WebServer;

/**
 * <code>TestWebPage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestWebServer {

	@Test
	public void shouldHavePageContentUsingHTTPWhenRequired() throws Exception {
		final String address = "http://localhost:2777";

		final URI uriServer = new URI(address);
		final WebServer server = WebServer.create(uriServer, new ComponentSourceManager() {
			@Override
			public void attach(SourceComponent<String, String> source) throws CannotCreateComponentException {
				throw new CannotCreateComponentException("Not allowed");
			}
		});
		server.bind();

		final URI uriClient = new URI(address + "/helloworld");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			final InputStream inputStream = uriClient.toURL().openStream();
			try {
				final byte[] bytes = new byte[1024];
				int len;
				while ((len = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, len);
					outputStream.flush();
				}
			} finally {
				inputStream.close();
			}
		} finally {
			outputStream.close();
		}

		assertEquals("Hello, World!", outputStream.toString());

		server.close();
	}

	@Test
	public void shouldHaveServerResponseWhenClientSendUsingWS() throws Exception {

		final ComponentDataFlowFactory<String, String> receiverFactory = new ComponentDataFlowFactory<String, String>() {
			@Override
			public DataFlow<String> create(final DataFlow<String> componentDataFlow) throws CannotCreateDataFlowException {
				return new DataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						if (data.equals("helloworld")) {
							componentDataFlow.handleData("Hello, World!");
						}
					}
				};
			}
		};

		final TerminalComponent<String, String> serverTerminal = new TerminalComponent<String, String>(receiverFactory);

		final ComponentSourceManager serverSourceManager = new ComponentSourceManager() {
			@Override
			public void attach(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, serverTerminal);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebServer server = WebServer.create(new URI("http://localhost:2777"), serverSourceManager).bind();

		final Promise<Boolean> ready = Promise.create();
		final Promise<String> response = Promise.create();

		final TerminalComponent<String, String> clientTerminal = new TerminalComponent<String, String>(new ComponentDataFlowFactory<String, String>() {
			@Override
			public DataFlow<String> create(final DataFlow<String> componentDataFlow) throws CannotCreateDataFlowException {
				return new DataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						response.success(data);
					}
				};
			}
		});

		final ComponentSourceManager clientSourceManager = new ComponentSourceManager() {
			@Override
			public void attach(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, clientTerminal);
					ready.success(true);
				} catch (ComponentConnectionRejectedException e) {
					ready.failure(e);
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebClient client = WebClient.create(clientSourceManager);
		final Instance connect = client.instance(new URI("ws://localhost:2777/websocket")).connect();

		assertTrue(ready.getFuture().get(10, TimeUnit.SECONDS));

		clientTerminal.getDownStreamDataFlow().handleData("helloworld");

		assertEquals("Hello, World!", response.getFuture().get(10, TimeUnit.SECONDS));

		connect.close();
		server.close();
	}

	@Test
	public void shouldHaveClientResponseWhenServerSendUsingWS() throws Exception {

		final Promise<String> response = Promise.create();

		final ComponentDataFlowFactory<String, String> receiverFactory = new ComponentDataFlowFactory<String, String>() {
			@Override
			public DataFlow<String> create(final DataFlow<String> componentDataFlow) throws CannotCreateDataFlowException {
				return new DataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						response.success(data);
					}
				};
			}
		};

		final TerminalComponent<String, String> serverTerminal = new TerminalComponent<String, String>(receiverFactory);

		final ComponentSourceManager serverSourceManager = new ComponentSourceManager() {
			@Override
			public void attach(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, serverTerminal);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebServer server = WebServer.create(new URI("http://localhost:2778"), serverSourceManager).bind();

		final Promise<Boolean> ready = Promise.create();

		final TerminalComponent<String, String> clientTerminal = new TerminalComponent<String, String>(new ComponentDataFlowFactory<String, String>() {
			@Override
			public DataFlow<String> create(final DataFlow<String> componentDataFlow) throws CannotCreateDataFlowException {
				return new DataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						if (data.equals("helloworld")) {
							componentDataFlow.handleData("Hello, World!");
						}
					}
				};
			}
		});

		final ComponentSourceManager clientSourceManager = new ComponentSourceManager() {
			@Override
			public void attach(SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, clientTerminal);
					ready.success(true);
				} catch (ComponentConnectionRejectedException e) {
					ready.failure(e);
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebClient client = WebClient.create(clientSourceManager);
		final Instance connect = client.instance(new URI("ws://localhost:277/websocket")).connect();

		assertTrue(ready.getFuture().get(10, TimeUnit.SECONDS));

		serverTerminal.getDownStreamDataFlow().handleData("helloworld");

		assertEquals("Hello, World!", response.getFuture().get(10, TimeUnit.SECONDS));

		connect.close();
		server.close();
	}
}
