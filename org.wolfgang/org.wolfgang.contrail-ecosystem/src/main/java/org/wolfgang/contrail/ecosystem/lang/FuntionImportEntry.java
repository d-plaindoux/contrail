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

import java.lang.reflect.Method;
import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.delta.LibraryBuilder;

/**
 * <code>FuntionImportEntry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class FuntionImportEntry implements EcosystemImportation<Component> {
	private final ContextFactory factory;
	private final Method method;

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	FuntionImportEntry(ContextFactory factory, Method method) {
		super();
		this.factory = factory;
		this.method = method;
	}

	@Override
	public Component create(Map<String, CodeValue> environment) throws CannotCreateComponentException {
		return LibraryBuilder.create(method, factory, environment);
	}
}