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

package org.wolfgang.contrail.component.router;

import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.pipeline.identity.IdentityComponent;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;
import org.wolfgang.contrail.reference.DirectReference;

/**
 * <code>NetworkLinkComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class OnLinkAcceptanceComponent extends IdentityComponent<Event, Event> {

	private final DirectReference reference;

	/**
	 * Constructor
	 */
	public OnLinkAcceptanceComponent(DirectReference reference) {
		super();
		this.reference = reference;
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<Event, Event> handler) throws ComponentConnectionRejectedException {
		final DestinationComponent<Event, Event> destination = handler.getDestinationComponent();
		final ComponentId componentId = destination.getComponentId();
		final ComponentLink connectDestination = super.connectDestination(handler);
		if (this.acceptDestination(componentId) && Coercion.canCoerce(destination, RouterComponent.class)) {
			final RouterComponent router = Coercion.coerce(destination, RouterComponent.class);
			router.filter(this.getComponentId(), this.reference);
			return connectDestination;
		} else {
			// TODO - Add a specific message
			throw new ComponentConnectionRejectedException(MessagesProvider.message("org.wolfgang.contrail.message", "destination.not.a.router").format());
		}
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<Event, Event> handler) throws ComponentConnectionRejectedException {
		final SourceComponent<Event, Event> destination = handler.getSourceComponent();
		final ComponentId componentId = destination.getComponentId();
		final ComponentLink connectDestination = super.connectSource(handler);
		if (this.acceptDestination(componentId) && Coercion.canCoerce(destination, RouterComponent.class)) {
			final RouterComponent router = Coercion.coerce(destination, RouterComponent.class);
			router.filter(this.getComponentId(), this.reference);
			return connectDestination;
		} else {
			// TODO - Add a specific message
			throw new ComponentConnectionRejectedException(MessagesProvider.message("org.wolfgang.contrail.message", "destination.not.a.router").format());
		}
	}
}
