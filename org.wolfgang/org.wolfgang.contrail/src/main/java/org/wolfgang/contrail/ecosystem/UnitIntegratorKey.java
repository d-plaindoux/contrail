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

package org.wolfgang.contrail.ecosystem;

/**
 * <code>Entry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class UnitIntegratorKey<U, D> {

	/**
	 * 
	 */
	private final Class<U> upstream;

	/**
	 * 
	 */
	private final Class<D> downstream;

	/**
	 * Constructor
	 * 
	 * @param upstream
	 * @param downstream
	 */
	public UnitIntegratorKey(Class<U> upstream, Class<D> downstream) {
		super();
		this.upstream = upstream;
		this.downstream = downstream;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((downstream == null) ? 0 : downstream.hashCode());
		result = prime * result + ((upstream == null) ? 0 : upstream.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		UnitIntegratorKey other = (UnitIntegratorKey) obj;
		if (downstream == null) {
			if (other.downstream != null) {
				return false;
			}
		} else if (!downstream.equals(other.downstream)) {
			return false;
		}
		if (upstream == null) {
			if (other.upstream != null) {
				return false;
			}
		} else if (!upstream.equals(other.upstream)) {
			return false;
		}
		return true;
	}
}