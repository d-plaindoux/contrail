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

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.wolfgang.actor.annotation.ActorAnnotationSolver;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Pair;

/**
 * <code>AbstractActor</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NotBoundActor implements Actor {

	private final Coordinator coordinator;
	private final String name;
	private final List<Pair<Request, Response>> actorActions;

	public NotBoundActor(String name, Coordinator coordinator) {
		this.name = name;
		this.coordinator = coordinator;
		this.actorActions = new LinkedList<Pair<Request, Response>>();
	}

	protected NotBoundActor(NotBoundActor actor) {
		this.name = actor.name;
		this.coordinator = actor.coordinator;
		this.actorActions = actor.actorActions;
	}

	@Override
	public boolean isBound() {
		return false;
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

	public BoundActor bindToSource(String model) throws ActorException {
		try {
			// TODO -- class loader to be determined?
			return this.bindToObject(Class.forName(model).newInstance());
		} catch (InstantiationException e) {
			throw new ActorException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ActorException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new ActorException(e.getMessage(), e);
		}
	}

	public BoundActor bindToObject(Object model) throws ActorException {
		try {
			final LocalActor actor = new LocalActor(ActorAnnotationSolver.solve(this, model), this);
			this.coordinator.registerActor(actor);
			return actor;
		} catch (IllegalArgumentException e) {
			throw new ActorException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ActorException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ActorException(e.getCause().getMessage(), e.getCause());
		}
	}

	public BoundActor bindToRemote(String remoteName, String location) throws ActorException {
		final RemoteActor actor = new RemoteActor(remoteName, location, this);
		this.coordinator.registerActor(actor);
		return actor;
	}

	@Override
	public void send(Request request, Response response) {
		this.actorActions.add(new Pair<Request, Response>(request, response));
	}

	@Override
	public void invoke(Request request, Response response) {
		response.failure(new ActorException(MessagesProvider.from(this).get("org/wolfgang/actor/message", "actor.not.yet.bound").format(this.getActorId())));
	}

	@Override
	public boolean performPendingAction() {
		return false;
	}
}
