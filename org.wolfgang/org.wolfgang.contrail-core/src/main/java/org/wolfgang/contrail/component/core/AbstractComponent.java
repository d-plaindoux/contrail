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

package org.wolfgang.contrail.component.core;

import static org.wolfgang.common.message.MessagesProvider.message;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentId;

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
		final String category = "org.wolfgang.contrail.message";
		
		ALREADY_CONNECTED = message(category, "already.connected");
		NOT_YET_CONNECTED = message(category, "not.yet.connected");
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

}
