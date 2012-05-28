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

import org.wolfgang.contrail.component.SourceComponent;

/**
 * <code>InitialComponentIntegrator</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface SourceComponentFactory<U, D> {

	/**
	 * Method called whether a link must be done
	 * 
	 * @param linkManager
	 *            The link manager
	 * @param component
	 *            The component to be integrated
	 * @return the source component receiving terminal component data
	 * @throws CannotBindToTerminalComponentException
	 *             if the terminal integration fails
	 */
	SourceComponent<U, D> create(SourceComponentReleaseHandler<U,D> handler);

}
