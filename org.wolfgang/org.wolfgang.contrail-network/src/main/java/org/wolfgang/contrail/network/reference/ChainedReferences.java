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

package org.wolfgang.contrail.network.reference;

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

	@Override
	public void addFirst(DirectReference reference) {
		assert reference != null;
		references.add(reference);
	}

	@Override
	public void addLast(DirectReference reference) {
		assert reference != null;
		references.add(reference);
	}

	/**
	 * Method called whether a direct reference index must be retrieved
	 * 
	 * @param reference
	 */
	private int getIndex(DirectReference reference) {
		int position = -1;
		for (DirectReference current : references) {
			position += 1;
			if (current.equals(reference)) {
				return position;
			}
		}
		return -1;
	}

	@Override
	public boolean hasNextReference(DirectReference reference) {
		final int index = this.getIndex(reference);
		return -1 < index && index + 1 < this.references.size();
	}

	@Override
	public DirectReference getNextReference(DirectReference reference) {
		assert this.hasNextReference(reference);
		return this.references.get(this.getIndex(reference) + 1);
	}

	@Override
	public boolean hasPreviousReference(DirectReference reference) {
		final int index = this.getIndex(reference);
		return -1 < index - 1;
	}

	@Override
	public DirectReference getPreviousReference(DirectReference reference) {
		assert this.hasPreviousReference(reference);
		return this.references.get(this.getIndex(reference) - 1);
	}

	@Override
	public <E, X extends Exception> E visit(ReferenceVisitor<E, X> visitor) throws X {
		return visitor.visit(this);
	}
}
