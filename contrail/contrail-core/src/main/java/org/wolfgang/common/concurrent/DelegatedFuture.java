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

package org.wolfgang.common.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <code>DelegatedFuture</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DelegatedFuture<V> implements Future<V> {

	private final Future<V> future;

	/**
	 * Constructor
	 * 
	 * @param future
	 */
	public DelegatedFuture(Future<V> future) {
		super();
		this.future = future;
	}

	/**
	 * @param mayInterruptIfRunning
	 * @return
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	/**
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @see java.util.concurrent.Future#get()
	 */
	public V get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	/**
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

	/**
	 * @return
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	public boolean isCancelled() {
		return future.isCancelled();
	}

	/**
	 * @return
	 * @see java.util.concurrent.Future#isDone()
	 */
	public boolean isDone() {
		return future.isDone();
	}

	
}
