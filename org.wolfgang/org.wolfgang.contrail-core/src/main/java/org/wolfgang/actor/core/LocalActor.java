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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;

/**
 * <code>LocalActor</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class LocalActor extends BindActor implements Actor {

	final private Object model;
	final private Map<String, Method> methodsCache;

	{
		this.methodsCache = new HashMap<String, Method>();
	}

	public LocalActor(Object model, AbstractActor actor) {
		super(actor);
		this.model = model;
	}

	@Override
	public void invoke(Request request, Response response) {
		final Method method = getMethodByName(request.getName());

		if (method != null) {
			try {
				response.success(method.invoke(model, request.getParameters()));
			} catch (IllegalArgumentException e) {
				failure(response, e);
			} catch (IllegalAccessException e) {
				failure(response, e);
			} catch (InvocationTargetException e) {
				failure(response, e.getCause());
			}
		} else {
			failure(response, new Exception("TODO"));
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

	private void failure(Response response, Throwable e) {
		if (response != null) {
			response.failure(e);
		}
	}
}