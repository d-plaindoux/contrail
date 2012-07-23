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

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.wolfgang.contrail.component.annotation.ComponentPipeline;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>ConcurrentSourcePipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ComponentPipeline("ParallelSource")
public class ParallelSourceComponent<U, D> extends AbstractPipelineComponent<U, D, U, D> {

	/**
	 * The upstream data handler
	 */
	private final UpStreamDataHandler<U> upStreamDataHandler;

	/**
	 * The internal executor in charge of managing incoming connection requests
	 */
	private final ThreadPoolExecutor executor;

	{
		final ThreadGroup group = new ThreadGroup("Threaded.Source");
		final ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(group, r, "Threaded.Source.Client");
			}
		};
		final LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<Runnable>();
		this.executor = new ThreadPoolExecutor(0, 256, 30L, TimeUnit.SECONDS, linkedBlockingQueue, threadFactory);
		this.executor.allowCoreThreadTimeOut(true);
	}

	{
		this.upStreamDataHandler = new UpStreamDataHandler<U>() {
			@Override
			public void handleData(final U data) throws DataHandlerException {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						getDestinationComponentLink().getDestination().getUpStreamDataHandler().handleData(data);
						return null;
					}
				});
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				try {
					getDestinationComponentLink().getDestination().closeUpStream();
				} finally {
					executor.shutdown();
				}
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				try {
					getDestinationComponentLink().getDestination().closeUpStream();
				} finally {
					executor.shutdown();
				}
			}
		};
	}

	/**
	 * Constructor
	 */
	public ParallelSourceComponent() {
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
