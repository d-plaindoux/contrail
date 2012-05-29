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

import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.ecosystem.ComponentEcosystem;
import org.wolfgang.contrail.ecosystem.ComponentEcosystemImpl;
import org.wolfgang.contrail.ecosystem.DestinationComponentFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.network.connection.nio.NIOServer;

/**
 * <code>WebServer</code> is A HTTP server which serves bsic HTTP requests and
 * Web Socket. This server will work with:
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Chrome 16+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Firefox 11+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * </ul>
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
	public WebServer(ComponentEcosystem ecosystem, int port) {
		super(port, new WebServerPipelineFactory(ecosystem));
	}

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 9090;
		}

		/**
		 * Prepare the ecosystem
		 */
		
		final ComponentEcosystemImpl ecosystem = new ComponentEcosystemImpl();
		
		final TerminalDataReceiverFactory<String, String> dataFactory = new TerminalDataReceiverFactory<String, String>() {
			@Override
			public DataReceiver<String> create(final TerminalComponent<String, String> component) {
				return new DataReceiver<String>() {
					@Override
					public void receiveData(String data) throws DataHandlerException {
						component.getDataSender().sendData(data.toUpperCase());
					}
					
					@Override
					public void close() throws IOException {
						component.getDataSender().close();
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

		ecosystem.addDestinationFactory(String.class, String.class, destinationComponentFactory);
		
		new WebServer(ecosystem, port).call();
	}

}
