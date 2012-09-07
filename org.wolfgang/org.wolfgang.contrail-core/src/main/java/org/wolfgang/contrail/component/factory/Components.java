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

package org.wolfgang.contrail.component.factory;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.compose.CompositionComponent;
import org.wolfgang.contrail.component.pipeline.compose.CompositionDestinationComponent;
import org.wolfgang.contrail.component.pipeline.compose.CompositionPipelineComponent;
import org.wolfgang.contrail.component.pipeline.compose.CompositionSourceComponent;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowFactory;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;

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

	public static <U, D> InitialComponent<U, D> initial(DownStreamDataFlow<D> flow) {
		return new InitialComponent<U, D>(flow);
	}

	public static <U, D> InitialComponent<U, D> initial(DownStreamDataFlowFactory<U, D> factory) throws CannotCreateDataFlowException {
		return new InitialComponent<U, D>(factory);
	}

	public static <U, D> TerminalComponent<U, D> terminal(UpStreamDataFlow<U> flow) {
		return new TerminalComponent<U, D>(flow);
	}

	public static <U, D> TerminalComponent<U, D> terminal(UpStreamDataFlowFactory<U, D> factory) throws CannotCreateDataFlowException {
		return new TerminalComponent<U, D>(factory);
	}

	@SuppressWarnings("rawtypes")
	public static Component compose(ComponentLinkManager linkManager, Component... components) throws ComponentConnectionRejectedException {
		if (components.length == 0) {
			throw new ComponentConnectionRejectedException("TODO");
		} else if (components.length == 1) {
			return components[0];
		} else if (Coercion.canCoerce(components[0], PipelineComponent.class) && Coercion.canCoerce(components[components.length - 1], PipelineComponent.class)) {
			return new CompositionPipelineComponent(linkManager, components);
		} else if (Coercion.canCoerce(components[0], SourceComponent.class) && Coercion.canCoerce(components[components.length - 1], PipelineComponent.class)) {
			return new CompositionSourceComponent(linkManager, components);
		} else if (Coercion.canCoerce(components[0], PipelineComponent.class) && Coercion.canCoerce(components[components.length - 1], DestinationComponent.class)) {
			return new CompositionDestinationComponent(linkManager, components);
		} else if (Coercion.canCoerce(components[0], SourceComponent.class) && Coercion.canCoerce(components[components.length - 1], DestinationComponent.class)) {
			return new CompositionComponent(linkManager, components);
		} else {
			throw new ComponentConnectionRejectedException("TODO");
		}
	}
}