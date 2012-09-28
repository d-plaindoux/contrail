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
public final class Clients implements Closeable {

	private Map<String, Class<? extends Client>> prototypes;
	private Map<String, Client> clients;

	{
		this.prototypes = new HashMap<String, Class<? extends Client>>();
		this.clients = new HashMap<String, Client>();
	}

	/**
	 * Constructor
	 */
	public Clients() {
		super();
	}

	/**
	 * Method called whether a given scheme must be registered
	 * 
	 * @param scheme
	 *            The scheme
	 * @param aClass
	 *            The corresponding class
	 */
	public void register(String scheme, Class<? extends Client> aClass) {
		prototypes.put(scheme, aClass);
	}

	/**
	 * @param scheme
	 * @return
	 * @throws ClientNotFoundException
	 */
	private Class<? extends Client> getFromScheme(String scheme) throws ClientNotFoundException {
		if (prototypes.containsKey(scheme)) {
			return prototypes.get(scheme);
		} else {
			throw new ClientNotFoundException("TODO");
		}
	}

	/**
	 * Method called whether a client must created
	 * 
	 * @param uri
	 *            The destination
	 * @param factory
	 *            The data sender factory
	 * @return
	 * @throws ClientNotFoundException
	 */
	public Client get(String scheme) throws ClientNotFoundException {
		if (clients.containsKey(scheme)) {
			return clients.get(scheme);
		} else {
			final Class<? extends Client> clientClass = getFromScheme(scheme);
			try {
				final Client client = clientClass.newInstance();
				clients.put(scheme, client);
				return client;
			} catch (Exception e) {
				throw new ClientNotFoundException(e);
			}
		}
	}

	@Override
	public void close() throws IOException {
		for (Client client : this.clients.values()) {
			client.close();
		}
	}
}
