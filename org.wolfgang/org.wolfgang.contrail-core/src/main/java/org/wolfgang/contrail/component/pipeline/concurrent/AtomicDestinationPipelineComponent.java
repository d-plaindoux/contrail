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
 * <code>AtomicDestinationPipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class AtomicDestinationPipelineComponent<U, D> extends AbstractPipelineComponent<U, D, U, D> {

	/**
	 * The downstream data handler
	 */
	private final DownStreamDataHandler<D> downStreamDataHandler;

	{
		final Lock downstreamLock = new ReentrantLock();
		this.downStreamDataHandler = new DownStreamDataHandler<D>() {
			@Override
			public void handleData(final D data) throws DataHandlerException {
				downstreamLock.lock();
				try {
					getSourceComponentLink().getSource().getDownStreamDataHandler().handleData(data);
				} finally {
					downstreamLock.unlock();
				}
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestination().closeDownStream();
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestination().closeDownStream();
			}
		};
	}

	/**
	 * Constructor
	 */
	public AtomicDestinationPipelineComponent() {
		super();
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.getDestinationComponentLink().getDestination().getUpStreamDataHandler();
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

}
