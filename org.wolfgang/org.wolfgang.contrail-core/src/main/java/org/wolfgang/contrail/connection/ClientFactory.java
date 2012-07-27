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

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>ClientFactory</code> is able to retrieve/create a client using a given
 * scheme (e.g. tcp, ws ...)
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ClientFactory implements Closeable {

	private Map<String, String> prototypes;
	private Map<String, Client> clients;

	{
		this.prototypes = new HashMap<String, String>();
		this.clients = new HashMap<String, Client>();
	}

	/**
	 * Constructor
	 */
	public ClientFactory() {
		super();
	}

	/**
	 * Method called whether a given scheme must be registered
	 * 
	 * @param scheme
	 *            The scheme
	 * @param className
	 *            The corresponding class name
	 */
	public void declareScheme(String scheme, String className) {
		prototypes.put(scheme, className);
	}

	/**
	 * @param scheme
	 * @return
	 */
	private String getFromScheme(String scheme) {
		return prototypes.get(scheme);
	}

	/**
	 * Method called whether a client must created
	 * 
	 * @param uri
	 *            The destination
	 * @param factory
	 *            The data sender factory
	 * @return
	 * @throws ClientFactoryCreationException
	 */
	@SuppressWarnings("unchecked")
	public Client create(ClassLoader loader, String scheme) throws ClientFactoryCreationException {
		try {
			if (clients.containsKey(scheme)) {
				return clients.get(scheme);
			} else {

				final String fromScheme = getFromScheme(scheme);

				assert fromScheme != null;

				final Class<Client> clientClass = (Class<Client>) loader.loadClass(fromScheme);
				final Client client = clientClass.newInstance();

				clients.put(scheme, client);

				return client;
			}
		} catch (Exception e) {
			throw new ClientFactoryCreationException(e);
		}
	}

	@Override
	public void close() throws IOException {
		for (Client client : this.clients.values()) {
			client.close();
		}
	}
}
