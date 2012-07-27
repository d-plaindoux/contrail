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
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

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
	private final UpStreamDataHandler<U> upStreamDataHandler;

	{
		final Lock upstreamLock = new ReentrantLock();
		this.upStreamDataHandler = new UpStreamDataHandler<U>() {
			@Override
			public void handleData(final U data) throws DataHandlerException {
				upstreamLock.lock();
				try {
					getDestinationComponentLink().getDestination().getUpStreamDataHandler().handleData(data);
				} finally {
					upstreamLock.unlock();
				}
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestination().closeUpStream();
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestination().closeUpStream();
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
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return this.getSourceComponentLink().getSource().getDownStreamDataHandler();
	}

}