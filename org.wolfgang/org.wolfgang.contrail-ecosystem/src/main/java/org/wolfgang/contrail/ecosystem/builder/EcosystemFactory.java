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

package org.wolfgang.contrail.ecosystem.builder;

import org.wolfgang.contrail.ecosystem.Ecosystem;

/**
 * <code>EcosystemFactory</code> is the basic interface used for the component
 * ecosystem creation
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface EcosystemFactory {

	/**
	 * @param nameSpace
	 */
	void addImport(String nameSpace);

	/**
	 * Add a new initial binder
	 * 
	 * @param flow
	 *            the binded data flow
	 */
	void addBinderDataFlow(DataFlow flow);

	/**
	 * Add a new internal component
	 * 
	 * @param flow
	 *            the binded data flow
	 */
	void addInternalDataFlow(DataFlow flow);

	/**
	 * Predicated used whether the current construction must be validated
	 * 
	 * @return true if the ecosystem is coherent and complete; false otherwise
	 */
	boolean validate();

	/**
	 * Method called whether the component ecosystem must be generated
	 * 
	 * @return a valid, consistent and ready to use ecosystem
	 */
	Ecosystem generateEcosystem();

}
