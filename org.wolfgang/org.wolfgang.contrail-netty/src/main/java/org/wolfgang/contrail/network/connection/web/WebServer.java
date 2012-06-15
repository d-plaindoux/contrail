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

import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.ecosystem.CannotProvideInitialComponentException;
import org.wolfgang.contrail.ecosystem.DestinationComponentFactory;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.handler.DataHandlerException;
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
	 * @throws CannotProvideInitialComponentException
	 */
	public static void main(String[] args) throws CannotProvideInitialComponentException {
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
		final List<TerminalComponent<String, String>> components = new ArrayList<TerminalComponent<String, String>>();

		final TerminalDataReceiverFactory<String, String> dataFactory = new TerminalDataReceiverFactory<String, String>() {
			@Override
			public DataReceiver<String> create(final TerminalComponent<String, String> component) {
				System.err.println("Accept client " + component.toString());
				components.add(component);
				
				return new DataReceiver<String>() {
					@Override
					public void receiveData(String data) throws DataHandlerException {
						for (TerminalComponent<String, String> aComponent : components) {
							aComponent.getDataSender().sendData(data);
						}
					}

					@Override
					public void close() throws IOException {
						component.getDataSender().close();
						components.remove(component);
					}
				};
			}
		};

		final DestinationComponentFactory<String, String> destinationComponentFactory = new DestinationComponentFactory<String, String>() {
			@Override
			public DestinationComponent<String, String> create() {
				return new TerminalComponent<String, String>(dataFactory);
			}
		};

		final RegisteredUnitEcosystemKey key = UnitEcosystemKey.getKey("web.socket", String.class, String.class);
		ecosystem.addDestinationFactory(key, destinationComponentFactory);

		new WebServer("localhost", port, ecosystem.<String, String> getInitialBinder(key)).call();
	}

}
