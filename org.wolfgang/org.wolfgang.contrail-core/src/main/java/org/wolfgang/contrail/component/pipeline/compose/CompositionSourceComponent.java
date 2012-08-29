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
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>ComposedPipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CompositionSourceComponent<U, D> extends AbstractComponent implements SourceComponent<U, D> {

	private final SourceComponent<?, ?> initialComponent;
	private final PipelineComponent<?, ?, U, D> terminalComponent;

	/**
	 * Constructor
	 * 
	 * @throws ComponentConnectionRejectedException
	 */
	@SuppressWarnings("unchecked")
	public CompositionSourceComponent(ComponentLinkManager linkManager, Component... components) throws ComponentConnectionRejectedException {
		super();

		assert components.length > 1;

		initialComponent = (SourceComponent<?, ?>) components[0];

		for (int i = 1; i < components.length; i++) {
			linkManager.connect(components[i - 1], components[i]);
		}

		terminalComponent = (PipelineComponent<?, ?, U, D>) components[components.length - 1];
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		this.initialComponent.closeUpStream();
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		this.terminalComponent.closeDownStream();
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return this.terminalComponent.getDownStreamDataHandler();
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return this.terminalComponent.acceptDestination(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
		return this.terminalComponent.connectDestination(handler);
	}

}
