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

package org.wolfgang.contrail.network.route;

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.network.builder.ClientBuilder;

/**
 * <code>RouteTable</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RouteTable {

	private final Map<String, ClientBuilder> routes;

	{
		this.routes = new HashMap<String, ClientBuilder>();
	}

	public void addEntry(String name, ClientBuilder endpoint) throws RouteAlreadyExistException {
		if (this.hasEntry(name)) {
			throw new RouteAlreadyExistException(MessagesProvider.message("org/wolfgang/contrail/message", "route.already.exist").format(name, endpoint));
		} else {
			this.routes.put(name, endpoint);
		}
	}

	public boolean hasEntry(String name) {
		return this.routes.containsKey(name);
	}

	public ClientBuilder getEntry(String name) throws RouteNotFoundException {
		if (this.hasEntry(name)) {
			return this.routes.get(name);
		} else {
			throw new RouteNotFoundException(MessagesProvider.message("org/wolfgang/contrail/message", "route.not.found").format(name));
		}
	}

}
