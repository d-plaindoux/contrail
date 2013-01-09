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

package org.wolfgang.contrail.component.compose;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.link.ComponentManager;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.DisposableLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>ComposedPipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CompositionPipelineComponent<U1, D1, U2, D2> extends AbstractComponent implements PipelineComponent<U1, D1, U2, D2> {

	private final DestinationComponent<U1, D1> initialComponent;
	private final SourceComponent<U2, D2> terminalComponent;

	/**
	 * Constructor
	 * 
	 * @throws ComponentConnectionRejectedException
	 */
	@SuppressWarnings("unchecked")
	public CompositionPipelineComponent(Component... components) throws ComponentConnectionRejectedException {
		super();

		assert components.length > 1;

		initialComponent = Coercion.coerce(components[0], DestinationComponent.class);

		for (int i = 1; i < components.length; i++) {
			ComponentManager.connect(components[i - 1], components[i]);
		}

		terminalComponent = Coercion.coerce(components[components.length - 1], SourceComponent.class);
	}

	@Override
	public DataFlow<D2> getDownStreamDataFlow() throws ComponentNotConnectedException {
		return this.terminalComponent.getDownStreamDataFlow();
	}

	@Override
	public DataFlow<U1> getUpStreamDataFlow() throws ComponentNotConnectedException {
		return this.initialComponent.getUpStreamDataFlow();
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return this.initialComponent.acceptSource(componentId);
	}

	@Override
	public DisposableLink connectSource(SourceComponentLink<U1, D1> handler) throws ComponentConnectionRejectedException {
		return this.initialComponent.connectSource(handler);
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		this.initialComponent.closeUpStream();
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		this.terminalComponent.closeDownStream();
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return this.terminalComponent.acceptDestination(componentId);
	}

	@Override
	public DisposableLink connectDestination(DestinationComponentLink<U2, D2> handler) throws ComponentConnectionRejectedException {
		return this.terminalComponent.connectDestination(handler);
	}

}
