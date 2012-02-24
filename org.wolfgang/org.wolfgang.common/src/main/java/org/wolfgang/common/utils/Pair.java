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

package org.wolfgang.common.utils;

/**
 * <code>Pair</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Pair<First, Second> {

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
	 * @param snd
	 */
	public Pair(final First fst, final Second snd) {
		this.first = fst;
		this.second = snd;
	}

	/**
	 * @return the first
	 */
	public First getFirst() {
		return first;
	}

	/**
	 * @return the second
	 */
	public Second getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Pair))
			return false;

		Pair pair = (Pair) o;

		if (first != null ? !first.equals(pair.first) : pair.first != null)
			return false;
		if (second != null ? !second.equals(pair.second) : pair.second != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = first != null ? first.hashCode() : 0;
		result = 31 * result + (second != null ? second.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "(" + first + "*" + second + ")";
	}
}
