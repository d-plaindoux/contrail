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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.wolfgang.actor.component.handler.RemoteActorHandler;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.utils.Pair;

/**
 * <code>Coordinator</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Coordinator implements Runnable {

	private final Map<String, Actor> universe;

	private final List<String> pendingActivedActors;
	private final List<String> pendingDeactivedActors;

	private final List<String> activeActors;

	private RemoteActorHandler remoteActorHandler;

	private ExecutorService coordinatorExecutor;
	private boolean isInExecutionStage;

	private ExecutorService actorActionsExecutor;

	{
		this.universe = new HashMap<String, Actor>();
		this.activeActors = new ArrayList<String>();
		this.pendingActivedActors = new ArrayList<String>();
		this.pendingDeactivedActors = new ArrayList<String>();
		this.isInExecutionStage = false;
	}

	public Coordinator() {
	}

	public void setRemoteActorHandler(RemoteActorHandler remoteActorHandler) {
		this.remoteActorHandler = remoteActorHandler;
	}

	public RemoteActorHandler getRemoteActorHandler() {
		return this.remoteActorHandler;
	}

	public void start() {
		if (this.coordinatorExecutor == null) {
			actorActionsExecutor = Executors.newScheduledThreadPool(40); // TODO
			coordinatorExecutor = Executors.newSingleThreadExecutor();
			this.activateCoordinatorIfNecessary();
		}
	}

	private void activateCoordinatorIfNecessary() {
		synchronized (this) {
			if (!this.isInExecutionStage && coordinatorExecutor != null) {
				this.isInExecutionStage = true;
				this.coordinatorExecutor.submit(this);
			}
		}
	}

	public void run() {
		boolean actionPerfomed = false;

		synchronized (this) {
			this.activeActors.addAll(this.pendingActivedActors);
			this.pendingActivedActors.clear();
			this.activeActors.removeAll(this.pendingDeactivedActors);
			this.pendingDeactivedActors.clear();
		}

		for (final String actorId : activeActors) {
			final Pair<Request, Response> nextAction = this.universe.get(actorId).getNextAction();
			if (nextAction != null) {
				actionPerfomed = true;
				this.actorActionsExecutor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						invoke(actorId, nextAction.getFirst(), nextAction.getSecond());
						return null;
					}
				});
			}
		}

		synchronized (this) {
			this.isInExecutionStage = false;
			if (actionPerfomed) {
				this.activateCoordinatorIfNecessary();
			}
		}
	}

	public void stop() {
		this.coordinatorExecutor.shutdown();
		this.actorActionsExecutor.shutdown();
	}

	public synchronized void activateActor(Actor actor) {
		final String identifier = actor.getActorId();
		if (this.pendingDeactivedActors.contains(identifier)) {
			this.pendingDeactivedActors.remove(identifier);
		} else if (this.pendingActivedActors.contains(identifier)) {
			// Nothing
		} else if (!this.activeActors.contains(identifier)) {
			this.pendingActivedActors.add(identifier);
			this.activateCoordinatorIfNecessary();
		}
	}

	public synchronized void deactivateActor(String identifier) {
		if (this.pendingActivedActors.contains(identifier)) {
			this.pendingActivedActors.remove(identifier);
		} else if (this.pendingDeactivedActors.contains(identifier)) {
			// Nothing
		} else if (this.activeActors.contains(identifier)) {
			this.pendingDeactivedActors.add(identifier);
			this.activateCoordinatorIfNecessary();
		}
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
			this.activateCoordinatorIfNecessary();
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
