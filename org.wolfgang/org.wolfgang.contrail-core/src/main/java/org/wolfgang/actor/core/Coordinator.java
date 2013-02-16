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

package org.wolfgang.actor.core;

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.actor.core.component.handler.RemoteActorHandler;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;

/**
 * <code>Coordinator</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Coordinator {

	private final Map<String, Actor> universe;
	private/* mutable */RemoteActorHandler remoteActorHandler;

	{
		this.universe = new HashMap<String, Actor>();
	}

	public Coordinator() {
		// Nothing to be done
	}

	public void setRemoteActorHandler(RemoteActorHandler remoteActorHandler) {
		this.remoteActorHandler = remoteActorHandler;
	}

	public RemoteActorHandler getRemoteActorHandler() {
		return this.remoteActorHandler;
	}

	public void start() {
		// TODO
	}

	public void stop() {
		// TODO
	}

	public void activateActor(Actor actor) {

	}

	public void deactivateActor(String identifier) {

	}

	public AbstractActor actor(String name) {
		final AbstractActor abstractActor = new AbstractActor(name, this);
		this.universe.put(abstractActor.getActorId(), abstractActor);
		return abstractActor;
	}

	public void registerActor(Actor actor) {
		this.universe.put(actor.getActorId(), actor);
		this.activateActor(actor);
	}

	public void disposeActor(String identifier) {
		this.deactivateActor(identifier);
		this.universe.remove(identifier);
	}

	public void send(String actorId, Request request, Response response) {
		if (this.universe.containsKey(actorId)) {
			this.universe.get(actorId).send(request, response);
		} else if (response != null) {
			response.failure(new Exception("TODO"));
		}
	}

	public void invoke(String actorId, Request request, Response response) {
		if (this.universe.containsKey(actorId)) {
			this.universe.get(actorId).invoke(request, response);
		} else if (response != null) {
			response.failure(new Exception("TODO"));
		}
	}

	public void broadcast(Request request) {
		for (String actorId : this.universe.keySet()) {
			this.send(actorId, request, null);
		}
	}
}
