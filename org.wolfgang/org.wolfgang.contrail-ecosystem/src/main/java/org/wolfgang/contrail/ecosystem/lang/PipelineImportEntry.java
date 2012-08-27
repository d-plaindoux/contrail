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

import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValueVisitor;
import org.wolfgang.contrail.ecosystem.lang.delta.PipelineComponentFactory;

/**
 * <code>PipelineImportEntry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
class PipelineImportEntry implements EcosystemImportation<PipelineComponent> {
	private final CodeValueVisitor visitor;
	private final ContextFactory factory;
	private final Class<?> component;

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	PipelineImportEntry(CodeValueVisitor visitor, ContextFactory factory, Class component) {
		super();
		this.visitor = visitor;
		this.factory = factory;
		this.component = component;
	}

	@Override
	public PipelineComponent create(Map<String, CodeValue> environment) throws CannotCreateComponentException {
		return PipelineComponentFactory.create(visitor, factory, component, environment);
	}
}