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

package org.wolfgang.contrail.component.reverse;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>RerversePipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RerversedPipelineComponent<U1, D1, U2, D2> extends AbstractComponent implements PipelineComponent<U1, D1, U2, D2> {

	private final PipelineComponent<D2, U2, D1, U1> component;

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	public RerversedPipelineComponent(PipelineComponent<D2, U2, D1, U1> component) {
		super();
		this.component = component;
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		component.closeDownStream();
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		component.closeUpStream();
	}

	@Override
	public UpStreamDataFlow<U1> getUpStreamDataFlow() {
		return DataFlows.reverse(component.getDownStreamDataFlow());
	}

	@Override
	public DownStreamDataFlow<D2> getDownStreamDataFlow() {
		return DataFlows.reverse(component.getUpStreamDataFlow());
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return component.acceptDestination(componentId);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<U1, D1> handler) throws ComponentConnectionRejectedException {
		return null;
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return component.acceptDestination(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<U2, D2> handler) throws ComponentConnectionRejectedException {
		return null;
	}

}
