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

package org.contrail.actor.core;

import org.contrail.actor.event.Request;
import org.contrail.actor.event.Response;

/**
 * <code>RemoteActor</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RemoteActor extends BoundActor implements Actor {

	private final String remoteName;
	private final String location;

	public RemoteActor(String remoteName, String location, NotBoundActor actor) {
		super(actor);
		this.remoteName = remoteName;
		this.location = location;
	}

	@Override
	public void invoke(Request request, Response response) {
		this.getCoordinator().getRemoteActorHandler().handle(location, this.remoteName, request, response);
	}
}
