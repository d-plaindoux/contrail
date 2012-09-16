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

package org.wolfgang.contrail.component.connection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailComponent;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.Server;
import org.wolfgang.contrail.connection.ServerNotFoundException;
import org.wolfgang.contrail.flow.DataFlowCloseException;

/**
 * <code>ServerComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailComponent
public class ServerComponent extends AbstractComponent {

	private final Server server;
	private final URI uri;

	/**
	 * Constructor
	 * 
	 * @param contextFactory
	 * @param reference
	 * @throws URISyntaxException
	 * @throws ServerNotFoundException
	 * @throws CannotCreateServerException
	 */
	@ContrailConstructor
	public ServerComponent(@ContrailArgument("context") ContextFactory contextFactory, @ContrailArgument("uri") String reference, @ContrailArgument("flow") ComponentFactoryListener listener)
			throws URISyntaxException, ServerNotFoundException, CannotCreateServerException {
		this.uri = new URI(reference);
		this.server = contextFactory.getServerFactory().get(uri.getScheme());
		this.server.bind(this.uri, listener);
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		try {
			server.close();
		} catch (IOException e) {
			throw new DataFlowCloseException(e);
		}
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		try {
			server.close();
		} catch (IOException e) {
			throw new DataFlowCloseException(e);
		}
	}
}
