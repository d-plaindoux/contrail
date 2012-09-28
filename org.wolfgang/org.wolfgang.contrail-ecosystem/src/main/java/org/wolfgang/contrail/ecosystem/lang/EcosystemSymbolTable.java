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

package org.wolfgang.contrail.ecosystem.lang;

import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;

/**
 * <code>EcosystemSymbolTable</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface EcosystemSymbolTable {

	/**
	 * Check importation availability
	 * 
	 * @param name
	 *            The importation name
	 * @return true if an importation exist for the given key; false otherwise
	 */
	boolean hasImportation(String name);

	/**
	 * Method called whether a given importation must be retrieved
	 * 
	 * @param name
	 *            The importation name
	 * @return a importation or <code>null</code>
	 */
	EcosystemImportation<?> getImportation(String name);

	/**
	 * Check definition availability
	 * 
	 * @param name
	 *            The definition name
	 * @return true if a definition exist for the given key; false otherwise
	 */
	boolean hasDefinition(String name);

	/**
	 * Method called whether a given definition must be retrieved
	 * 
	 * @param name
	 *            The definition name
	 * @return a definition or <code>null</code>
	 */
	CodeValue getDefinition(String name);

}
