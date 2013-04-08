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

package org.contrail.common.utils;

/**
 * The <code>Pair</code> data structure. Such pair provides two values on-demand
 * and these values can be <code>null</code>.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Pair<First, Second> {

	public static <U, D> Pair<U, D> create(U u, D d) {
		return new Pair<U, D>(u, d);
	}

	/**
	 * The first attribute
	 */
	private final First first;

	/**
	 * The second attribute
	 */
	private final Second second;

	/**
	 * Constructor
	 * 
	 * @param fst
	 *            This first value
	 * @param snd
	 *            The second value
	 */
	public Pair(final First fst, final Second snd) {
		this.first = fst;
		this.second = snd;
	}

	/**
	 * Method called whether the first pair value is required
	 * 
	 * @return the first value
	 */
	public First getFirst() {
		return first;
	}

	/**
	 * Method called whether the second pair value is required
	 * 
	 * @return the second value
	 */
	public Second getSecond() {
		return second;
	}

	/**
	 * {@link Object#hashCode()}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/**
	 * {@link Object#equals(Object)}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pair)) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		return true;
	}
}
