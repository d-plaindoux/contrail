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

package org.wolfgang.contrail.component.station;

import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

/**
 * <code>StationPipeline</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class StationPipeline<U> extends AbstractPipelineComponent<U, U, U, U> implements IDataStreamHandler<U> {

	private final IDataStreamHandler<U> handler;

	/**
	 * Constructor
	 * 
	 * @param handler
	 */
	public StationPipeline(IDataStreamHandler<U> handler) {
		super();
		this.handler = handler;
	}

	/**
	 * @param data
	 * @return
	 * @see org.wolfgang.contrail.component.station.IDataStreamHandler#canAccept(java.lang.Object)
	 */
	public boolean canAccept(U data) {
		return handler.canAccept(data);
	}

	/**
	 * @param data
	 * @return
	 * @see org.wolfgang.contrail.component.station.IDataStreamHandler#accept(java.lang.Object)
	 */
	public U accept(U data) {
		return handler.accept(data);
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		return this.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow();
	}

	@Override
	public DownStreamDataFlow<U> getDownStreamDataFlow() {
		return this.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow();
	}

}
