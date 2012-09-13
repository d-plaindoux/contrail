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
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

/**
 * <code>StationPipeline</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class StationPipeline<U, D> extends AbstractPipelineComponent<U, D, U, D> {

	private final IDataStreamHandler<U> handlerUpStream;
	private final IDataStreamHandler<D> handlerDownStream;

	/**
	 * Constructor
	 * 
	 * @param handler
	 */
	public StationPipeline(IDataStreamHandler<U> handlerUpStream, IDataStreamHandler<D> handlerDownStream) {
		super();
		assert handlerUpStream != null || handlerDownStream != null;

		this.handlerUpStream = handlerUpStream;
		this.handlerDownStream = handlerDownStream;
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		final UpStreamDataFlow<U> upStreamDataFlow = getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow();
		if (handlerUpStream == null) {
			return upStreamDataFlow;
		} else {
			return new UpStreamDataFlow<U>() {
				@Override
				public void handleData(U data) throws DataFlowException {
					upStreamDataFlow.handleData(handlerUpStream.accept(data));
				}

				@Override
				public void handleClose() throws DataFlowCloseException {
					upStreamDataFlow.handleClose();
				}
			};
		}
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		final DownStreamDataFlow<D> downStreamDataFlow = this.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow();
		if (handlerDownStream == null) {
			return downStreamDataFlow;
		} else {
			return new DownStreamDataFlow<D>() {
				@Override
				public void handleData(D data) throws DataFlowException {
					downStreamDataFlow.handleData(handlerDownStream.accept(data));
				}

				@Override
				public void handleClose() throws DataFlowCloseException {
					downStreamDataFlow.handleClose();
				}
			};
		}
	}

}
