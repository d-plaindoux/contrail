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

package org.wolgang.contrail.network.reference;

import java.io.Serializable;
import java.util.UUID;

/**
 * <code>EndPoint</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ServerReference implements SimpleReference, Serializable {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = -5559719113890135312L;

	/**
	 * The identifier
	 */
	private final UUID identifier;

	/**
	 * Constructor
	 * 
	 * @param host
	 * @param port
	 */
	public ServerReference(UUID identifier) {
		this.identifier = identifier;
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
		if (!(obj instanceof ServerReference)) {
			return false;
		}
		ServerReference other = (ServerReference) obj;
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
