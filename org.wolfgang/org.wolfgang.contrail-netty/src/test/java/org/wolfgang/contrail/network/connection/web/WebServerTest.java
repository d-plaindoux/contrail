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

package org.wolfgang.contrail.network.connection.web;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDataFlowFactory;
import org.wolfgang.contrail.component.SourceComponentNotifier;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowAdapter;
import org.wolfgang.contrail.flow.exception.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.network.connection.web.client.WebClient;
import org.wolfgang.contrail.network.connection.web.client.WebClient.Instance;
import org.wolfgang.contrail.network.connection.web.content.ResourceWebContentProvider;
import org.wolfgang.contrail.network.connection.web.content.WebContentProvider;
import org.wolfgang.contrail.network.connection.web.server.WebServer;

/**
 * <code>TestWebPage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class WebServerTest {

	@Test
	public void shouldHavePageContentUsingHTTPWhenRequired() throws Exception {

		final WebContentProvider contentProvider = new ResourceWebContentProvider();
		final WebServer server = WebServer.create(new SourceComponentNotifier() {
			@Override
			public void accept(int identifier, SourceComponent<String, String> source) throws CannotCreateComponentException {
				throw new CannotCreateComponentException("Not allowed");
			}
		}, contentProvider);
		server.bind(2777);

		final URI uriClient = new URI("http://localhost:2777/helloworld");
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

		final SourceComponentNotifier serverSourceManager = new SourceComponentNotifier() {
			@Override
			public void accept(int identifier, SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, serverTerminal);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebContentProvider contentProvider = new ResourceWebContentProvider();
		final WebServer server = WebServer.create(serverSourceManager, contentProvider).bind(2777);

		final Promise<String, Exception> serverResponse = Promise.create();

		final TerminalComponent<String, String> clientTerminal = new TerminalComponent<String, String>(new DataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				serverResponse.success(data);
			}
		});

		final SourceComponentNotifier clientSourceManager = new SourceComponentNotifier() {
			@Override
			public void accept(int identifier, SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, clientTerminal);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebClient client = WebClient.create(clientSourceManager);
		final Instance connect = client.instance(new URI("ws://localhost:2777/websocket")).connect().awaitEstablishment();

		clientTerminal.getDownStreamDataFlow().handleData("helloworld");

		assertEquals("Hello, World!", serverResponse.getFuture().get(10, TimeUnit.SECONDS));

		connect.close();
		server.close();
	}

	@Test
	public void shouldHaveClientResponseWhenServerSendUsingWS() throws Exception {

		final Promise<String, Exception> clientResponse = Promise.create();

		final TerminalComponent<String, String> serverTerminal = new TerminalComponent<String, String>(new DataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				clientResponse.success(data);
			}
		});

		final SourceComponentNotifier serverSourceManager = new SourceComponentNotifier() {
			@Override
			public void accept(int identifier, SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, serverTerminal);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebContentProvider contentProvider = new ResourceWebContentProvider();
		final WebServer server = WebServer.create(serverSourceManager, contentProvider).bind(2778);

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

		final SourceComponentNotifier clientSourceManager = new SourceComponentNotifier() {
			@Override
			public void accept(int identifier, SourceComponent<String, String> source) throws CannotCreateComponentException {
				try {
					Components.compose(source, clientTerminal);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final WebClient client = WebClient.create(clientSourceManager);
		final Instance connect = client.instance(new URI("ws://localhost:2778/websocket")).connect().awaitEstablishment();

		serverTerminal.getDownStreamDataFlow().handleData("helloworld");

		assertEquals("Hello, World!", clientResponse.getFuture().get(10, TimeUnit.SECONDS));

		connect.close();
		server.close();
	}
}
