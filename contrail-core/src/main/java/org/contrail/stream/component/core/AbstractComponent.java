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

package org.contrail.stream.component.core;

import org.contrail.common.message.MessagesProvider;

import org.contrail.common.message.Message;
import org.contrail.stream.component.Component;
import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.ComponentId;
import org.contrail.stream.component.Components;

/**
 * The <code>AbstractComponent</code> is the basic component implementation
 * providing identification facility.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public abstract class AbstractComponent implements Component {

	/**
	 * Static message definition for connected component
	 */
	protected static final Message ALREADY_CONNECTED;

	/**
	 * Static message definition for not yet connected component
	 */
	protected static final Message NOT_YET_CONNECTED;

	static {
		final String category = "org.contrail.message";

		ALREADY_CONNECTED = MessagesProvider.from(AbstractComponent.class).get(category, "already.connected");
		NOT_YET_CONNECTED = MessagesProvider.from(AbstractComponent.class).get(category, "not.yet.connected");
	}

	/**
	 * The component identifier
	 */
	private final ComponentId componentId;

	/**
	 * Constructor
	 * 
	 * @param componentId
	 *            The component identifier
	 */
	protected AbstractComponent() {
		super();
		this.componentId = new ComponentIdImpl();
	}

	@Override
	public ComponentId getComponentId() {
		return this.componentId;
	}

	@Override
	public Component connect(Component... components) throws ComponentConnectionRejectedException {
		final Component[] allComponents = new Component[components.length + 1];
		allComponents[0] = this;
		System.arraycopy(components, 0, allComponents, 1, components.length);
		return Components.compose(allComponents);
	}
}
