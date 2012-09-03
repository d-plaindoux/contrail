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

package org.wolfgang.contrail.connection;

import java.net.URI;
import java.net.URISyntaxException;

import org.wolfgang.contrail.component.Component;

/**
 * <code>ConnectionComponentFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ConnectionComponentFactory {

	/**
	 * @param contextFactory
	 * @param uri
	 * @param componentFactory
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientCreationException
	 * @throws CannotCreateClientException
	 */
	public static Component client(ContextFactory contextFactory, String uri, ComponentFactory componentFactory) throws URISyntaxException, ClientCreationException, CannotCreateClientException {
		final URI reference = new URI(uri);
		final Client client = contextFactory.getClientFactory().get(reference.getScheme());
		return client.connect(reference, componentFactory);
	}

	/**
	 * @param contextFactory
	 * @param uri
	 * @param componentFactory
	 * @return
	 * @throws URISyntaxException
	 * @throws ServerCreationException
	 * @throws CannotCreateServerException
	 */
	public static Worker server(ContextFactory contextFactory, String uri, ComponentFactory componentFactory) throws URISyntaxException, ServerCreationException, CannotCreateServerException {
		final URI reference = new URI(uri);
		final Server server = contextFactory.getServerFactory().get(reference.getScheme());
		return server.bind(reference, componentFactory);
	}
}
