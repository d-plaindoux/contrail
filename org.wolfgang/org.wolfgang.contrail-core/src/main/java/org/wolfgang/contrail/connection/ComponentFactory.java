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

package org.wolfgang.contrail.connection;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>ComponentFactory</code> specifies the minimal behaviors required for a
 * component creation on-demand and associated linkage manager.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface ComponentFactory {

	/**
	 * Method called whether the embedded component must be created
	 * 
	 * @return a component (never <code>null</code>)
	 * @throws CannotCreateComponentException
	 */
	Component create() throws CannotCreateComponentException;

	/**
	 * Method called whether the link manager used for the creation is required
	 * 
	 * @return a link manager (nerver <code>null</code>)
	 */
	ComponentLinkManager getLinkManager();
}
