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

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.CannotProvideComponentException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;
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
	public WebServer(String host, int port, ComponentFactory factory) {
		super(host, port, new WebServerPipelineFactory(factory));
	}

	public static WebServer create(int port) throws CannotProvideComponentException {
		/**
		 * Prepare the ecosystem
		 */

		final EcosystemImpl ecosystem = new EcosystemImpl();
		final List<DownStreamDataFlow<String>> components = new ArrayList<DownStreamDataFlow<String>>();

		final UpStreamDataFlowFactory<String, String> dataFactory = new UpStreamDataFlowFactory<String, String>() {
			@Override
			public UpStreamDataFlow<String> create(final DownStreamDataFlow<String> component) {
				components.add(component);

				return DataFlows.<String> closable(new UpStreamDataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						for (DownStreamDataFlow<String> aComponent : components) {
							aComponent.handleData(data);
						}
					}

					@Override
					public void handleClose() throws DataFlowCloseException {
						component.handleClose();
						components.remove(component);
					}

					@Override
					public void handleLost() throws DataFlowCloseException {
						component.handleLost();
						components.remove(component);
					}
				});
			}
		};

		final ComponentFactory destinationComponentFactory = new ComponentFactory() {
			@Override
			public ComponentLinkManager getLinkManager() {
				return ecosystem.getLinkManager();
			}

			@Override
			public Component create() throws CannotCreateComponentException {
				try {
					return new TerminalComponent<String, String>(dataFactory);
				} catch (CannotCreateDataFlowException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		final RegisteredUnitEcosystemKey key = EcosystemKeyFactory.key("web.socket", String.class, String.class);
		ecosystem.addBinder(key, destinationComponentFactory);

		return new WebServer("0.0.0.0", port, ecosystem.getFactory(key));
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

		create(port).call();
	}
}
