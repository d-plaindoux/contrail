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

package org.contrail.stream.component.core;

import java.util.UUID;

import org.contrail.stream.component.ComponentId;

/**
 * <code>ComponentIdImpl</code> used to identify any component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ComponentIdImpl implements ComponentId {

	private final UUID identifier;

	/**
	 * Constructor
	 */
	ComponentIdImpl() {
		this(UUID.randomUUID());
	}

	/**
	 * Constructor
	 * 
	 * @param uuid
	 *            The identifier
	 */
	public ComponentIdImpl(UUID uuid) {
		super();
		this.identifier = uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ComponentIdImpl)) {
			return false;
		}
		ComponentIdImpl other = (ComponentIdImpl) obj;
		if (identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!identifier.equals(other.identifier)) {
			return false;
		}
		return true;
	}
}
