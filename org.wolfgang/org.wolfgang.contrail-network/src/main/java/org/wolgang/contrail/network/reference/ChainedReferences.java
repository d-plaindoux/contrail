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
import java.util.ArrayList;

/**
 * <code>ChainedReferences</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ChainedReferences implements IndirectReference, Serializable {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = 4685858894460203748L;

	/**
	 * 
	 */
	private final ArrayList<DirectReference> references;

	{
		this.references = new ArrayList<DirectReference>();
	}

	/**
	 * Constructor
	 */
	ChainedReferences() {
		// Nothing special
	}

	/**
	 * Method called whether a simple end point must be added
	 * 
	 * @param endPoint
	 *            The new added simple endpoint
	 */
	public void addToChain(DirectReference endPoint) {
		assert endPoint != null;
		references.add(0, endPoint);
	}

	/**
	 * @param endPoint
	 */
	public int getIndex(DirectReference endPoint) {
		int position = -1;
		for (DirectReference current : references) {
			position += 1;
			if (current.equals(endPoint)) {
				return position;
			}
		}
		return -1;
	}

	/**
	 * @param endPoint
	 * @return
	 */
	public boolean hasNextReference(DirectReference endPoint) {
		final int index = this.getIndex(endPoint);
		return -1 < index && index + 1 < this.references.size();
	}

	/**
	 * @param endPoint
	 * @return
	 */
	public DirectReference getNextReference(DirectReference endPoint) {
		assert this.hasNextReference(endPoint);
		return this.references.get(this.getIndex(endPoint) + 1);
	}

	/**
	 * @param endPoint
	 * @return
	 */
	public boolean hasPreviousReference(DirectReference endPoint) {
		final int index = this.getIndex(endPoint);
		return -1 < index - 1;
	}

	/**
	 * @param endPoint
	 * @return
	 */
	public DirectReference getPreviousReference(DirectReference endPoint) {
		assert this.hasPreviousReference(endPoint);
		return this.references.get(this.getIndex(endPoint) - 1);
	}
}
