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

package org.wolfgang.contrail.reference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <code>ChainedReferences</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
final class ChainedReferences implements IndirectReference, Serializable {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = 4685858894460203748L;

	/**
	 * 
	 */
	private final List<DirectReference> references;

	{
		this.references = new ArrayList<DirectReference>();
	}

	/**
	 * Constructor
	 * 
	 * @param references
	 */
	ChainedReferences(DirectReference... references) {
		this.references.addAll(Arrays.asList(references));
	}

	@Override
	public DirectReference getCurrent() {
		assert this.references.size() > 0;
		return this.references.get(0);
	}

	@Override
	public IndirectReference removeCurrent() {
		assert this.references.size() > 0;
		this.references.remove(0);
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.references.size() > 1;
	}

	@Override
	public IndirectReference addFirst(DirectReference reference) {
		if (this.references.size() == 0 || !this.getCurrent().equals(reference)) {
			this.references.add(0, reference);
		}
		return this;
	}

	@Override
	public <E, X extends Exception> E visit(ReferenceVisitor<E, X> visitor) throws X {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return Arrays.toString(references.toArray(new DirectReference[references.size()]));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((references == null) ? 0 : references.hashCode());
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
		if (!(obj instanceof ChainedReferences)) {
			return false;
		}
		ChainedReferences other = (ChainedReferences) obj;
		if (references == null) {
			if (other.references != null) {
				return false;
			}
		} else if (!references.equals(other.references)) {
			return false;
		}
		return true;
	}
}
