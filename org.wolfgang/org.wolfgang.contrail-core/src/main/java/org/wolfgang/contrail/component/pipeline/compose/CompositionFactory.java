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

package org.wolfgang.contrail.component.pipeline.compose;

import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>CompositionFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class CompositionFactory {

	/**
	 * Constructor
	 */
	private CompositionFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	public static Component compose(ComponentLinkManager linkManager, Component... components) throws ComponentConnectionRejectedException {
		if (components.length == 0) {
			throw new ComponentConnectionRejectedException("TODO");
		} else if (components.length == 1) {
			return components[0];
		} else if (components[0] instanceof PipelineComponent && components[components.length - 1] instanceof PipelineComponent) {
			return new CompositionPipelineComponent(linkManager, components);
		} else if (components[0] instanceof SourceComponent && components[components.length - 1] instanceof PipelineComponent) {
			return new CompositionSourceComponent(linkManager, components);
		} else if (components[0] instanceof PipelineComponent && components[components.length - 1] instanceof DestinationComponent) {
			return new CompositionDestinationComponent(linkManager, components);
		} else if (components[0] instanceof SourceComponent && components[components.length - 1] instanceof DestinationComponent) {
			return new CompositionComponent(linkManager, components);
		} else {
			throw new ComponentConnectionRejectedException("TODO");
		}
	}
}
