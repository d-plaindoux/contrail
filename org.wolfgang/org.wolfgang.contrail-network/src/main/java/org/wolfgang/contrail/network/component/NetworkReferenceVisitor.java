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

package org.wolfgang.contrail.network.component;

import org.wolfgang.contrail.network.reference.ChainedReferences;
import org.wolfgang.contrail.network.reference.ClientReference;
import org.wolfgang.contrail.network.reference.DirectReference;
import org.wolfgang.contrail.network.reference.ReferenceVisitor;
import org.wolfgang.contrail.network.reference.ServerReference;

/**
 * <code>NetworkReferenceVisitor</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkReferenceVisitor implements ReferenceVisitor<Boolean, Exception> {

	private final DirectReference currentReference;
	private final DirectReference targetReference;

	/**
	 * Constructor
	 * 
	 * @param currentReference
	 * @param targetReference
	 */
	public NetworkReferenceVisitor(DirectReference currentReference, DirectReference targetReference) {
		super();
		this.currentReference = currentReference;
		this.targetReference = targetReference;
	}

	@Override
	public Boolean visit(ClientReference reference) throws Exception {
		return targetReference.equals(reference);
	}

	@Override
	public Boolean visit(ServerReference reference) throws Exception {
		return targetReference.equals(reference);
	}

	@Override
	public Boolean visit(ChainedReferences reference) throws Exception {
		if (reference.hasNextReference(currentReference)) {
			final DirectReference nextReference = reference.getNextReference(currentReference);
			return nextReference.equals(targetReference);
		} else {
			return false;
		}
	}

}
