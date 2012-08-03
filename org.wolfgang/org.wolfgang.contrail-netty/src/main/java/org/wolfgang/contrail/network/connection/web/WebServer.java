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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataReceiverFactory;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKeyFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;
import org.wolfgang.contrail.network.connection.nio.NIOServer;

/**
 * <code>WebServer</code> is A HTTP server which serves HTTP requests and Web
 * Socket.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class WebServer extends NIOServer {

	/**
	 * Constructor
	 * 
	 * @param port
	 */
	public WebServer(String host, int port, DataSenderFactory<String, String> factory) {
		super(host, port, new WebServerPipelineFactory(factory));
	}

	/**
	 * Main
	 * 
	 * @param args
	 * @throws CannotProvideComponentException
	 */
	public static void main(String[] args) throws CannotProvideComponentException {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 9090;
		}

		/**
		 * Prepare the ecosystem
		 */

		final EcosystemImpl ecosystem = new EcosystemImpl();
		final List<DataSender<String>> components = new ArrayList<DataSender<String>>();

		final DataReceiverFactory<String, String> dataFactory = new DataReceiverFactory<String, String>() {
			@Override
			public DataReceiver<String> create(final DataSender<String> component) {
				components.add(component);

				return new DataReceiver<String>() {
					@Override
					public void receiveData(String data) throws DataHandlerException {
						for (DataSender<String> aComponent : components) {
							aComponent.sendData(data);
						}
					}

					@Override
					public void close() throws IOException {
						component.close();
						components.remove(component);
					}
				};
			}
		};

		final DataSenderFactory<String, String> destinationComponentFactory = new DataSenderFactory<String, String>() {

			@Override
			public DataSender<String> create(DataReceiver<String> receiver) throws CannotCreateDataSenderException {
				final InitialComponent<String, String> initialComponent = new InitialComponent<String, String>(receiver);
				final TerminalComponent<String, String> terminalComponent = new TerminalComponent<String, String>(dataFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataSenderException(e);
				}
				return initialComponent.getDataSender();
			}
		};

		final RegisteredUnitEcosystemKey key = UnitEcosystemKeyFactory.getKey("web.socket", String.class, String.class);
		ecosystem.addBinder(key, destinationComponentFactory);

		new WebServer("localhost", port, ecosystem.<String, String> getBinder(key)).call();
	}
}
