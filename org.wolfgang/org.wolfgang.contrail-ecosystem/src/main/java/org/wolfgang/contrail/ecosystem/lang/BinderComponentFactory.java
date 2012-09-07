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
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.delta.ComponentBuilder;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.ConversionException;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>BinderComponentFactory</code> is dedicated to the binder mechanism
 * creation and was a specific component factory implementation.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class BinderComponentFactory implements ComponentFactory {
	private final CodeValue flow;
	private final ComponentLinkManager linkManager;

	/**
	 * Constructor
	 * 
	 * @param linkManager
	 * @param items
	 */
	BinderComponentFactory(ComponentLinkManager linkManager, CodeValue flow) {
		super();
		this.linkManager = linkManager;
		this.flow = flow;
	}

	@Override
	public ComponentLinkManager getLinkManager() {
		return this.linkManager;
	}

	@Override
	public Component create() throws CannotCreateComponentException {
		try {
			return ComponentBuilder.create(linkManager, Component.class, flow);
		} catch (ConversionException e) {
			throw new CannotCreateComponentException(e);
		}
	}
}