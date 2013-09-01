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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.contrail.actor.component.handler.RemoteActorHandler;
import org.contrail.actor.event.Request;
import org.contrail.actor.event.Response;
import org.contrail.common.message.MessagesProvider;
import org.contrail.stream.data.JSonifier;

/**
 * <code>Coordinator</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Coordinator implements Runnable {

	private static class ActorReference {
		private final AtomicReference<CoordinatedActor> actor;
		private final AtomicBoolean active;

		private ActorReference(CoordinatedActor actor) {
			super();
			this.actor = new AtomicReference<CoordinatedActor>(actor);
			this.active = new AtomicBoolean(false);
		}

		boolean isNotActive() {
			return !active.get();
		}

		void setActive() {
			active.set(true);
		}

		void setInactive() {
			active.set(false);
		}

		CoordinatedActor getActor() {
			return actor.get();
		}

		void setActor(CoordinatedActor actor) {
			this.actor.set(actor);
		}
	}

	public class Proxy {

		public class Actor {

			private final String actorId;

			private Actor(String actorId) {
				super();
				this.actorId = actorId;
			}

			public String getActorId() {
				return actorId;
			}

			public void ask(Request request) {
				ask(request, null);
			}

			public void ask(final Request request, final Response response) {
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

		public Proxy(String location) {
			this.location = location;
		}

		public Actor actor(String name) {
			return new Actor(name);
		}
	}

	private final Map<String, ActorReference> universe;

	private final List<String> pendingActivedActors;
	private final List<String> pendingDeactivedActors;
	private final List<String> activeActors;

	private RemoteActorHandler remoteActorHandler;

	private ExecutorService coordinatorExecutor;

	private boolean isInExecutionStage;
	private ExecutorService actorActionsExecutor;

	{
		this.universe = new HashMap<String, ActorReference>();
		this.activeActors = new ArrayList<String>();
		this.pendingActivedActors = new ArrayList<String>();
		this.pendingDeactivedActors = new ArrayList<String>();
		this.isInExecutionStage = false;
	}

	public Proxy domain(String location) {
		return new Proxy(location);
	}

	public String getDomainId() {
		if (remoteActorHandler != null) {
			return remoteActorHandler.getDomainId();
		} else {
			return null; // TODO
		}
	}

	public void addJSonifiers(JSonifier... jSonifiers) {
		if (remoteActorHandler != null) {
			getRemoteActorHandler().addJSonifiers(jSonifiers);
		} else {
			// TODO
		}
	}

	public void setRemoteActorHandler(RemoteActorHandler remoteActorHandler) {
		this.remoteActorHandler = remoteActorHandler;
	}

	public RemoteActorHandler getRemoteActorHandler() {
		return remoteActorHandler;
	}

	public Coordinator start() {
		synchronized (this) {
			if (coordinatorExecutor == null) {
				final AtomicInteger index = new AtomicInteger(0);
				actorActionsExecutor = Executors.newFixedThreadPool(10, new ThreadFactory() {
					@Override
					public Thread newThread(Runnable arg0) {
						return new Thread(arg0, "Actor Executor #" + index.incrementAndGet());
					}
				});

				this.coordinatorExecutor = Executors.newSingleThreadExecutor();
				activateCoordinatorIfNecessary();
			}
		}

		return this;
	}

	private void activateCoordinatorIfNecessary() {
		synchronized (this) {
			if (!isInExecutionStage && coordinatorExecutor != null) {
				isInExecutionStage = true;
				coordinatorExecutor.submit(this);
			}
		}
	}

	void performPendingActorAction(final String actorId, final Request request, final Response response) {
		final ActorReference actorReference = universe.get(actorId);

		if (actorReference != null) {

			actorReference.setActive();

			actorActionsExecutor.submit(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					askNow(actorId, request, response);
					return null;
				}
			});
		}
	}

	public void run() {
		boolean actionPerfomed = false;

		synchronized (this) {
			activeActors.addAll(pendingActivedActors);
			pendingActivedActors.clear();
			activeActors.removeAll(pendingDeactivedActors);
			pendingDeactivedActors.clear();
		}

		for (final String actorId : activeActors) {
			final ActorReference actorReference = universe.get(actorId);
			if (actorReference.isNotActive() && actorReference.getActor().performPendingAction()) {
				actionPerfomed = true;
			}
		}

		synchronized (this) {
			this.isInExecutionStage = false;
			if (actionPerfomed) {
				activateCoordinatorIfNecessary();
			}
		}
	}

	public void stop() {
		coordinatorExecutor.shutdown();
		actorActionsExecutor.shutdown();
	}

	private synchronized void activateActor(Actor actor) {
		final String identifier = actor.getActorId();
		if (pendingDeactivedActors.contains(identifier)) {
			pendingDeactivedActors.remove(identifier);
		} else if (pendingActivedActors.contains(identifier)) {
			// Nothing
		} else if (!activeActors.contains(identifier)) {
			pendingActivedActors.add(identifier);
			activateCoordinatorIfNecessary();
		}
	}

	private synchronized void deactivateActor(String identifier) {
		if (pendingActivedActors.contains(identifier)) {
			pendingActivedActors.remove(identifier);
		} else if (pendingDeactivedActors.contains(identifier)) {
			// Nothing
		} else if (activeActors.contains(identifier)) {
			pendingDeactivedActors.add(identifier);
			activateCoordinatorIfNecessary();
		}
	}

	public boolean hasActor(String name) {
		return universe.containsKey(name);
	}

	public Actor actor(String name) {
		if (hasActor(name)) {
			return universe.get(name).getActor();
		} else {
			final NotBoundActor abstractActor = new NotBoundActor(name, this);
			universe.put(abstractActor.getActorId(), new ActorReference(abstractActor));
			return abstractActor;
		}
	}

	void registerActor(CoordinatedActor actor) {
		final ActorReference actorReference = universe.get(actor.getActorId());

		assert actorReference != null;

		actorReference.setActor(actor);
		activateActor(actor);
	}

	public void disposeActor(String identifier) {
		deactivateActor(identifier);
		universe.remove(identifier);
	}

	public void ask(String actorId, Request request, Response response) {
		final ActorReference actorReference = universe.get(actorId);
		
		if (actorReference != null) {
			actorReference.getActor().ask(request, response);
			activateCoordinatorIfNecessary();
		} else if (response != null) {
			response.failure(new ActorException(MessagesProvider.from(this).get("org/contrail/actor/message", "actor.not.found").format(actorId)));
		}
	}

	public void ask(String actorId, Request request) {
		ask(actorId, request, null);
	}

	void askNow(String actorId, Request request, Response response) {
		final ActorReference actorReference = universe.get(actorId);
		
		if (actorReference != null) {
			try {
				actorReference.getActor().askNow(request, response);
			} finally {
				actorReference.setInactive();
				activateCoordinatorIfNecessary();
			}
		} else if (response != null) {
			response.failure(new ActorException(MessagesProvider.from(this).get("org/contrail/actor/message", "actor.not.found").format(actorId)));
		}
	}

	public void broadcast(Request request) {
		for (String actorId : universe.keySet()) {
			ask(actorId, request, null);
		}
	}
}
