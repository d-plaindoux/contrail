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

import java.util.Map;

/**
 * <code>ClientFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ClientFactory {

	private static Map<String, String> prototypes;
	private static Map<String, Client> clients;

	/**
	 * Constructor
	 */
	private ClientFactory() {
		super();
	}

	/**
	 * @param scheme
	 * @return
	 */
	private static String getFromScheme(String scheme) {
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
	public static Client create(ClassLoader loader, String scheme) throws ClientFactoryCreationException {
		if (clients.containsKey(scheme)) {
			return clients.get(scheme);
		}

		try {
			final Class<Client> clientClass = (Class<Client>) loader.loadClass(getFromScheme(scheme));
			final Client client = clientClass.newInstance();

			clients.put(scheme, client);

			return client;
		} catch (Exception e) {
			throw new ClientFactoryCreationException(e);
		}
	}

}
