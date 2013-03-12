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

import java.util.List;

import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.utils.Pair;

/**
 * <code>ActorBuilder</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class BoundActor implements Actor {

	private final Coordinator coordinator;
	private final String name;
	private final List<Pair<Request, Response>> actorActions;

	protected BoundActor(NotBoundActor actor) {
		this.name = actor.getActorId();
		this.coordinator = actor.getCoordinator();
		this.actorActions = actor.getActorActions();
	}

	@Override
	public boolean isBound() {
		return true;
	}

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public String getActorId() {
		return name;
	}

	public BoundActor bindToObject(Object model) throws ActorException {
		throw new ActorException("Already bound");
	}

	public BoundActor bindToRemote(String remoteName, String location) throws ActorException {
		throw new ActorException("Already bound");
	}

	public BoundActor bindToSource(String model) throws ActorException {
		throw new ActorException("Already bound");
	}

	@Override
	public synchronized void send(Request request, Response response) {
		this.actorActions.add(new Pair<Request, Response>(request, response));
	}

	@Override
	public void invoke(Request request, Response response) {
		response.failure(new ActorException("Not yet binded")); // TODO
	}

	public synchronized boolean performPendingAction() {
		if (this.actorActions.size() == 0) {
			return false;
		} else {
			final Pair<Request, Response> pendingAction = this.actorActions.remove(0);
			this.coordinator.performPendingActorAction(name, pendingAction.getFirst(), pendingAction.getSecond());
			return true;
		}
	}
}
