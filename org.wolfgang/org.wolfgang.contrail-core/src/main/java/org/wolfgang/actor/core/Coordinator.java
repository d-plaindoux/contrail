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
import org.wolfgang.common.message.MessagesProvider;

/**
 * <code>Coordinator</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Coordinator implements Runnable {

	public class Remote {

		public class Actor {

			private final String actorId;

			private Actor(String actorId) {
				super();
				this.actorId = actorId;
			}

			public String getActorId() {
				return actorId;
			}

			public void send(Request request) {
				this.send(request, null);
			}

			public void send(final Request request, final Response response) {
				actorActionsExecutor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						remoteActorHandler.handle(location, actorId, request, response);
						return null;
					}
				});

				activateCoordinatorIfNecessary();
			}

			@Override
			public String toString() {
				return location + "@" + actorId;
			}
		}

		private final String location;

		public Remote(String location) {
			this.location = location;
		}

		public Actor actor(String name) {
			return new Actor(name);
		}
	}

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

	public Remote domain(String location) {
		return new Remote(location);
	}

	public void setRemoteActorHandler(RemoteActorHandler remoteActorHandler) {
		this.remoteActorHandler = remoteActorHandler;
	}

	public RemoteActorHandler getRemoteActorHandler() {
		return this.remoteActorHandler;
	}

	public Coordinator start() {
		if (this.coordinatorExecutor == null) {
			actorActionsExecutor = Executors.newFixedThreadPool(1);
			coordinatorExecutor = Executors.newSingleThreadExecutor();
			this.activateCoordinatorIfNecessary();
		}

		return this;
	}

	private void activateCoordinatorIfNecessary() {
		synchronized (this) {
			if (!this.isInExecutionStage && coordinatorExecutor != null) {
				this.isInExecutionStage = true;
				this.coordinatorExecutor.submit(this);
			}
		}
	}

	void performPendingActorAction(final String actorId, final Request request, final Response response) {
		this.actorActionsExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				invoke(actorId, request, response);
				return null;
			}
		});
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
			actionPerfomed = this.universe.get(actorId).performPendingAction() || actionPerfomed;
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

	public boolean hasActor(String name) {
		return this.universe.containsKey(name);
	}

	public Actor actor(String name) {
		if (hasActor(name)) {
			return this.universe.get(name);
		} else {
			final NotBoundActor abstractActor = new NotBoundActor(name, this);
			this.universe.put(abstractActor.getActorId(), abstractActor);
			return abstractActor;
		}
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
		if (this.hasActor(actorId)) {
			this.universe.get(actorId).send(request, response);
			this.activateCoordinatorIfNecessary();
		} else if (response != null) {
			response.failure(new ActorException(MessagesProvider.message("org/wolfgang/actor/message", "actor.not.found").format(actorId)));
		}
	}

	public void send(String actorId, Request request) {
		this.send(actorId, request, null);
	}

	public void invoke(String actorId, Request request, Response response) {
		if (this.hasActor(actorId)) {
			this.universe.get(actorId).invoke(request, response);
		} else if (response != null) {
			response.failure(new ActorException(MessagesProvider.message("org/wolfgang/actor/message", "actor.not.found").format(actorId)));
		}
	}

	public void broadcast(Request request) {
		for (String actorId : this.universe.keySet()) {
			this.send(actorId, request, null);
		}
	}
}
