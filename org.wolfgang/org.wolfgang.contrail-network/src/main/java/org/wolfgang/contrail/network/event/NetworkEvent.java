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

package org.wolfgang.contrail.network.event;

import java.io.Serializable;

import org.wolfgang.contrail.network.reference.DirectReference;
import org.wolfgang.contrail.network.reference.IndirectReference;

/**
 * <code>NetworkEvent</code> is the basic and one possible top-level type
 * implemented by network events.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface NetworkEvent {

	/**
	 * Method providing the previous reference
	 * 
	 * @return a direct reference
	 */
	DirectReference getSender();

	/**
	 * Method setting previous reference
	 * 
	 * @return a direct reference
	 */
	NetworkEvent sentBy(DirectReference reference);

	/**
	 * @return the target reference
	 */
	IndirectReference getReferenceToDestination();

	/**
	 * @return the source reference
	 */
	IndirectReference getReferenceToSource();

	/**
	 * Provide the message content
	 * 
	 * @return a content
	 */
	Serializable getContent();

}