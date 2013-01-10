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
import java.util.Collections;
import java.util.List;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentDataFlowFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.contrail.ComponentFactory;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowAdapter;
import org.wolfgang.contrail.flow.DataFlowFactory;
import org.wolfgang.contrail.flow.exception.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.link.ComponentManager;
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

	public static WebServer create(int port) {
		/**
		 * Prepare the ecosystem
		 */

		final ComponentDataFlowFactory<String, String> dataFactory = new ComponentDataFlowFactory<String, String>() {
			final List<DataFlow<String>> components = Collections.synchronizedList(new ArrayList<DataFlow<String>>());

			@Override
			public DataFlow<String> create(final DataFlow<String> component) {
				components.add(component);

				return DataFlowFactory.<String> closable(new DataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						for (DataFlow<String> aComponent : components) {
							aComponent.handleData(data);
						}
					}

					@Override
					public void handleClose() throws DataFlowCloseException {
						component.handleClose();
						components.remove(component);
					}
				});
			}
		};

		final ComponentFactory destinationComponentFactory = new ComponentFactory() {
			@Override
			public Component create(Object... arguments) throws CannotCreateComponentException {
				try {
					return new TerminalComponent<String, String>(dataFactory);
				} catch (CannotCreateDataFlowException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};

		return new WebServer("0.0.0.0", port, destinationComponentFactory);
	}

	/**
	 * Main
	 * 
	 * @param args
	 * @throws CannotProvideComponentException
	 */
	public static void main(String[] args) {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 9090;
		}

		create(port).call();
	}
}
