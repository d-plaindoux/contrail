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

package org.contrail.stream.component.compose;

import org.contrail.common.utils.Coercion;
import org.contrail.stream.component.Component;
import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.DestinationComponent;
import org.contrail.stream.component.SourceComponent;
import org.contrail.stream.component.core.AbstractComponent;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.link.ComponentManager;

/**
 * <code>ComposedPipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CompositionComponent extends AbstractComponent implements Component {

	private final SourceComponent<?, ?> initialComponent;
	private final DestinationComponent<?, ?> terminalComponent;

	/**
	 * Constructor
	 * 
	 * @throws ComponentConnectionRejectedException
	 */
	public CompositionComponent(Component... components) throws ComponentConnectionRejectedException {
		super();

		assert components.length > 1;

		initialComponent = Coercion.coerce(components[0], SourceComponent.class);

		for (int i = 1; i < components.length; i++) {
			ComponentManager.connect(components[i - 1], components[i]);
		}

		terminalComponent = Coercion.coerce(components[components.length - 1], DestinationComponent.class);
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		this.initialComponent.closeUpStream();
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		this.terminalComponent.closeDownStream();
	}
}
