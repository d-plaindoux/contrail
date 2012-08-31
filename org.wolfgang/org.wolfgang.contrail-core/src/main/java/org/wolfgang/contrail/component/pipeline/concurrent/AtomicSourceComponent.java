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

package org.wolfgang.contrail.component.pipeline.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

/**
 * <code>AtomicSourcePipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class AtomicSourceComponent<U, D> extends AbstractPipelineComponent<U, D, U, D> {

	/**
	 * The upstream data handler
	 */
	private final UpStreamDataFlow<U> upStreamDataHandler;

	{
		final Lock upstreamLock = new ReentrantLock();
		this.upStreamDataHandler = new UpStreamDataFlow<U>() {
			@Override
			public void handleData(final U data) throws DataFlowException {
				upstreamLock.lock();
				try {
					getDestinationComponentLink().getDestinationComponent().getUpStreamDataHandler().handleData(data);
				} finally {
					upstreamLock.unlock();
				}
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				getDestinationComponentLink().getDestinationComponent().closeUpStream();
			}

			@Override
			public void handleLost() throws DataFlowCloseException {
				getDestinationComponentLink().getDestinationComponent().closeUpStream();
			}
		};
	}

	/**
	 * Constructor
	 */
	public AtomicSourceComponent() {
		super();
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataHandler() {
		return this.getSourceComponentLink().getSourceComponent().getDownStreamDataHandler();
	}

}
