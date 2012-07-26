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
 * <code>ClientFactory</code> is able to retrieve/create a client using a given
 * scheme (e.g. tcp, ws ...)
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ServerFactory {

	private static Map<String, String> prototypes;
	private static Map<String, Server> servers;

	/**
	 * Constructor
	 */
	private ServerFactory() {
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
	@SuppressWarnings("unchecked")
	public static Server create(ClassLoader loader, String scheme) throws ServerFactoryCreationException {
		try {
			if (servers.containsKey(scheme)) {
				return servers.get(scheme);
			} else {

				final String fromScheme = getFromScheme(scheme);

				assert fromScheme != null;

				final Class<Server> serverClass = (Class<Server>) loader.loadClass(fromScheme);
				final Server server = serverClass.newInstance();

				servers.put(scheme, server);

				return server;
			}
		} catch (Exception e) {
			throw new ServerFactoryCreationException(e);
		}
	}

}
