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

import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

/**
 * <code>ConcurrentSourcePipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ParallelSourceComponent<U, D> extends AbstractPipelineComponent<U, D, U, D> {

	/**
	 * The upstream data handler
	 */
	private final UpStreamDataFlow<U> upStreamDataHandler;

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
		this.upStreamDataHandler = new UpStreamDataFlow<U>() {
			@Override
			public void handleData(final U data) throws DataFlowException {
				executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(data);
						return null;
					}
				});
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				try {
					getDestinationComponentLink().getDestinationComponent().closeUpStream();
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
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		return this.upStreamDataHandler;
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		return this.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow();
	}

}
