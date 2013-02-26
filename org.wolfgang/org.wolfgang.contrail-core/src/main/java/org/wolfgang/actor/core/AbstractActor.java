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

import java.util.LinkedList;
import java.util.List;

import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.utils.Pair;

/**
 * <code>AbstractActor</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class AbstractActor implements Actor {

	private final Coordinator coordinator;
	private final String name;
	private final List<Pair<Request, Response>> actorActions;

	public AbstractActor(String name, Coordinator coordinator) {
		this.name = name;
		this.coordinator = coordinator;
		this.actorActions = new LinkedList<Pair<Request, Response>>();
	}

	protected AbstractActor(AbstractActor actor) {
		this.name = actor.name;
		this.coordinator = actor.coordinator;
		this.actorActions = actor.actorActions;
	}

	public List<Pair<Request, Response>> getActorActions() {
		return actorActions;
	}

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public String getActorId() {
		return name;
	}

	public Actor bindToObject(Object model) {
		final LocalActor actor = new LocalActor(model, this);
		this.coordinator.registerActor(actor);
		return actor;
	}

	public Actor bindToRemote(String location) {
		final RemoteActor actor = new RemoteActor(location, this);
		this.coordinator.registerActor(actor);
		return actor;
	}

	@Override
	public void send(Request request, Response response) {
		this.actorActions.add(new Pair<Request, Response>(request, response));
	}

	@Override
	public void invoke(Request request, Response response) {
		response.failure(new ActorException("Not yet binded")); // TODO
	}

	@Override
	public boolean performPendingAction() {
		return false;
	}
}
