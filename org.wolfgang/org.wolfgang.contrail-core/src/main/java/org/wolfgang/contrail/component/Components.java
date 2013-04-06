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

package org.wolfgang.contrail.component;

import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.compose.CompositionComponent;
import org.wolfgang.contrail.component.compose.CompositionDestinationComponent;
import org.wolfgang.contrail.component.compose.CompositionPipelineComponent;
import org.wolfgang.contrail.component.compose.CompositionSourceComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.CannotCreateDataFlowException;

/**
 * <code>BoundComponents</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class Components {

	/**
	 * Constructor
	 */
	private Components() {
		super();
	}

	public static <U, D> InitialComponent<U, D> initial(DataFlow<D> flow) {
		return new InitialComponent<U, D>(flow);
	}

	public static <U, D> InitialComponent<U, D> initial(ComponentDataFlowFactory<U, D> factory) throws CannotCreateDataFlowException {
		return new InitialComponent<U, D>(factory);
	}

	public static <U, D> TerminalComponent<U, D> terminal(DataFlow<U> flow) {
		return new TerminalComponent<U, D>(flow);
	}

	public static <U, D> TerminalComponent<U, D> terminal(ComponentDataFlowFactory<D, U> factory) throws CannotCreateDataFlowException {
		return new TerminalComponent<U, D>(factory);
	}

	@SuppressWarnings("rawtypes")
	public static Component compose(Component... components) throws ComponentConnectionRejectedException {
		if (components.length == 0) {
			throw new ComponentConnectionRejectedException("TODO");
		} else if (components.length == 1) {
			return components[0];
		} else if (Coercion.canCoerce(components[0], SourceComponent.class, DestinationComponent.class) && Coercion.canCoerce(components[components.length - 1], SourceComponent.class, DestinationComponent.class)) {
			return new CompositionPipelineComponent(components);
		} else if (Coercion.canCoerce(components[0], SourceComponent.class) && Coercion.canCoerce(components[components.length - 1], SourceComponent.class, DestinationComponent.class)) {
			return new CompositionSourceComponent(components);
		} else if (Coercion.canCoerce(components[0], SourceComponent.class, DestinationComponent.class) && Coercion.canCoerce(components[components.length - 1], DestinationComponent.class)) {
			return new CompositionDestinationComponent(components);
		} else if (Coercion.canCoerce(components[0], SourceComponent.class) && Coercion.canCoerce(components[components.length - 1], DestinationComponent.class)) {
			return new CompositionComponent(components);
		} else {
			throw new ComponentConnectionRejectedException(MessagesProvider.from(Components.class).get("org/wolfgang/contrail/message", "not.a.source.and.destination").format());
		}
	}
}
