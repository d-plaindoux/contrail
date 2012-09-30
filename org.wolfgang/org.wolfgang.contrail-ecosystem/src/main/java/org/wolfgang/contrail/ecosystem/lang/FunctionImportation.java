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

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.delta.LibraryBuilder;

/**
 * <code>FuntionImportEntry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class FunctionImportation<T> implements EcosystemImportation<T> {
	private final ContextFactory factory;
	private final String name;
	private final Object provider;

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	FunctionImportation(ContextFactory factory, Object object, String name) {
		super();
		this.factory = factory;
		this.name = name;
		this.provider = object;
	}

	@Override
	public T create(EcosystemSymbolTable symbolTable) throws CannotCreateComponentException {
		return LibraryBuilder.create(provider, name, factory, symbolTable);
	}

	@Override
	public String toString() {
		return provider.getClass().getName() + "#" + name;
	}
	
}