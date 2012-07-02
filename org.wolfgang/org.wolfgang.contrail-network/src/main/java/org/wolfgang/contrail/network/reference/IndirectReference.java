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

/**
 * <code>EndPoint</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface IndirectReference extends Reference {

	/**
	 * Method called whether a simple end point must be added as fist reference
	 * 
	 * @param reference
	 *            The new added direct reference
	 */
	DirectReference getNext();

	/**
	 * Method called whether a simple end point must be added as fist reference
	 * 
	 * @param reference
	 *            The new added direct reference
	 */
	IndirectReference removeNext();

	/**
	 * @return
	 */
	boolean hasNext();

	/**
	 * Method called whether a simple end point must be added as last reference
	 * 
	 * @param reference
	 *            The new added direct reference
	 */
	IndirectReference addFirst(DirectReference reference);

}
