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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.contrail.actor.event.Request;
import org.contrail.actor.event.Response;
import org.contrail.common.message.Message;
import org.contrail.common.message.MessagesProvider;

/**
 * <code>LocalActor</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class LocalActor<T> extends BoundActor implements Actor {

	private static final Message SERVICE_NOT_FOUND;
	private static final Message MESSAGE_FAILURE;

	final private T model;
	final private Map<String, Method> methodsCache;

	static {
		SERVICE_NOT_FOUND = MessagesProvider.from(LocalActor.class).get("org/contrail/actor/message", "service.not.found");
		MESSAGE_FAILURE = MessagesProvider.from(LocalActor.class).get("org/contrail/actor/message", "execution.failure");
	}

	{
		this.methodsCache = new HashMap<String, Method>();
	}

	public LocalActor(T model, NotBoundActor actor) {
		super(actor);
		this.model = model;
	}

	public T getModel() {
		return model;
	}

	private String getMessage(String message, String alternative) {
		if (message == null) {
			return alternative;
		} else {
			return message;
		}
	}

	@Override
	public void invoke(Request request, Response response) {
		final Method method = getMethodByName(request.getName());

		if (method != null) {
			try {
				response.success(method.invoke(model, request.getParameters()));
			} catch (IllegalArgumentException e) {
				failure(response, new ActorException(getMessage(e.getMessage(), MESSAGE_FAILURE.format(request.getName(), this.getActorId())), e));
			} catch (IllegalAccessException e) {
				failure(response, new ActorException(getMessage(e.getMessage(), MESSAGE_FAILURE.format(request.getName(), this.getActorId())), e));
			} catch (InvocationTargetException e) {
				failure(response, new ActorException(getMessage(e.getCause().getMessage(), MESSAGE_FAILURE.format(request.getName(), this.getActorId())), e.getCause()));
			}
		} else {
			final String message = SERVICE_NOT_FOUND.format(request.getName(), this.getActorId());
			failure(response, new ActorException(message));
		}
	}

	private void findMethodByName(String name) {
		if (!this.methodsCache.containsKey(name)) {
			final Method[] methods = model.getClass().getMethods();

			for (Method method : methods) {
				if (method.getName().equals(name)) {
					this.methodsCache.put(name, method);
					return;
				}
			}
		}
	}

	private Method getMethodByName(String name) {
		this.findMethodByName(name);
		return this.methodsCache.get(name);
	}

	private void failure(Response response, ActorException e) {
		if (response != null) {
			response.failure(e);
		}
	}
}
