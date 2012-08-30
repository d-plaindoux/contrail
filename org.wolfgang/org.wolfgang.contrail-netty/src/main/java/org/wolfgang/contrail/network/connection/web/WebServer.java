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

import java.util.ArrayList;
import java.util.List;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialUpStreamDataHandler;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerAdapter;
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
	public WebServer(String host, int port, UpStreamDataHandlerFactory<String, String> upStreamDataHandlerFactory) {
		super(host, port, new WebServerPipelineFactory(upStreamDataHandlerFactory));
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
		final List<DownStreamDataHandler<String>> components = new ArrayList<DownStreamDataHandler<String>>();

		final UpStreamDataHandlerFactory<String, String> dataFactory = new UpStreamDataHandlerFactory<String, String>() {
			@Override
			public UpStreamDataHandler<String> create(final DownStreamDataHandler<String> component) {
				components.add(component);

				return new UpStreamDataHandlerAdapter<String>() {
					@Override
					public void handleData(String data) throws DataHandlerException {
						for (DownStreamDataHandler<String> aComponent : components) {
							aComponent.handleData(data);
						}
					}

					@Override
					public void handleClose() throws DataHandlerCloseException {
						component.handleClose();
						components.remove(component);
					}

					@Override
					public void handleLost() throws DataHandlerCloseException {
						component.handleLost();
						components.remove(component);
					}
				};
			}
		};

		final UpStreamDataHandlerFactory<String, String> destinationComponentFactory = new UpStreamDataHandlerFactory<String, String>() {

			@Override
			public UpStreamDataHandler<String> create(DownStreamDataHandler<String> receiver) throws CannotCreateDataHandlerException {
				final InitialComponent<String, String> initialComponent = new InitialComponent<String, String>(receiver);
				final TerminalComponent<String, String> terminalComponent = new TerminalComponent<String, String>(dataFactory);
				final ComponentLinkManagerImpl componentsLinkManagerImpl = new ComponentLinkManagerImpl();
				try {
					componentsLinkManagerImpl.connect(initialComponent, terminalComponent);
				} catch (ComponentConnectionRejectedException e) {
					throw new CannotCreateDataHandlerException(e);
				}
				return InitialUpStreamDataHandler.<String>create(initialComponent);
			}
		};

		final RegisteredUnitEcosystemKey key = EcosystemKeyFactory.key("web.socket", String.class, String.class);
		ecosystem.addBinder(key, destinationComponentFactory);

		new WebServer("localhost", port, ecosystem.<String, String> getBinder(key)).call();
	}
}
