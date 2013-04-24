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

package org.contrail.actor.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.contrail.actor.core.ActorException;
import org.contrail.actor.core.Coordinator;
import org.contrail.actor.core.LocalActor;
import org.contrail.actor.core.NotBoundActor;
import org.contrail.actor.event.Request;
import org.contrail.actor.event.Response;

/**
 * <code>AnnotationSolver</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ActorAnnotationSolver {
	
	public interface Register {
		void register(final String actorName, final Class<?> modelType);
	}

	private static <T> T solveFields(NotBoundActor actor, T model) throws IllegalArgumentException, IllegalAccessException {
		final Field[] fields = model.getClass().getFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(ActorId.class) && String.class.isAssignableFrom(field.getType())) {
				field.set(model, actor.getActorId());
			} else if (field.isAnnotationPresent(ActorCoordinator.class) && Coordinator.class.isAssignableFrom(field.getType())) {
				field.set(model, actor.getCoordinator());
			} else if (field.isAnnotationPresent(ActorReference.class) && LocalActor.class.isAssignableFrom(field.getType())) {
				final ActorReference reference = field.getAnnotation(ActorReference.class);
				if (actor.getCoordinator().hasActor(reference.value())) {
					field.set(model, actor.getCoordinator().actor(reference.value()));
				}
			}
		}

		return model;
	}

	private static <T> T solveNotifier(NotBoundActor actor, T model) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		final Method[] methods = model.getClass().getMethods();

		for (Method method : methods) {
			if (method.isAnnotationPresent(ActorBoundNotifier.class) && method.getParameterTypes().length == 0) {
				method.invoke(model);
			}
		}

		return model;
	}

	public static <T> T solve(NotBoundActor actor, T model) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return solveNotifier(actor, solveFields(actor, model));
	}

	public static void installActor(final Register register, final Class<?> modelType) {
		if (modelType.isAnnotationPresent(Actor.class)) {
			final Actor actor = modelType.getAnnotation(Actor.class);
			register.register(actor.value(), modelType);
		}
	}
}
