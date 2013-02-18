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

package org.wolfgang.actor.component.flow;

import org.wolfgang.actor.event.Request;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.data.ObjectRecord;

/**
 * <code>ActorFilter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ActorInteractionFilter {

	public static boolean isAnActorRequest(Object object) {
		if (Coercion.canCoerce(object, ObjectRecord.class)) {
			final ObjectRecord record = Coercion.coerce(object, ObjectRecord.class);
			return record.has("identifier", String.class) && record.has("request", Request.class) && record.hasOrNull("response", String.class);
		} else {
			return false;
		}
	}

	public static boolean isAnActorResponse(Object object) {
		if (Coercion.canCoerce(object, ObjectRecord.class)) {
			final ObjectRecord record = Coercion.coerce(object, ObjectRecord.class);
			return record.has("identifier", String.class) && record.has("type", Integer.TYPE) && record.hasOrNull("value", Object.class);
		} else {
			return false;
		}
	}
}
