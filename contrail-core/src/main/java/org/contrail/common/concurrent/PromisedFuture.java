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

package org.contrail.common.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <code>PromisedFuture</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
final class PromisedFuture<V> implements Future<V> {

	private enum Status {
		UNBOUND, CANCELLED, BOUND_VALUE, BOUND_ERROR
	}

	private final Lock barrier;
	private final Condition condition;

	private Status status;

	private V value;
	private Throwable error;

	{
		this.barrier = new ReentrantLock();
		this.condition = this.barrier.newCondition();
		this.status = Status.UNBOUND;
		this.value = null;
		this.error = null;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		barrier.lock();
		try {
			if (this.isCancelled() || this.isDone()) {
				// Do nothing
				return false;
			} else {
				this.status = Status.CANCELLED;
				this.condition.signalAll();
			}
		} finally {
			barrier.unlock();
		}

		return false;
	}

	/**
	 * Method checking the status availability
	 * 
	 * @param timeout
	 *            The time out
	 * @param unit
	 *            The time unit
	 * @return true if the status has been set up; flase otherwise
	 */
	private boolean waitIfNecessary(long timeout, TimeUnit unit) {
		if (this.status == Status.UNBOUND) {
			try {
				this.condition.await(timeout, unit);
			} catch (InterruptedException consume) {
				// Ignore
			}
		}

		return this.status != Status.UNBOUND;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		barrier.lock();
		try {
			if (!this.waitIfNecessary(60 * 3 + 14, TimeUnit.SECONDS)) {
				throw new InterruptedException();
			} else {
				/* Manage the status */
				switch (status) {
				case BOUND_ERROR:
					throw new ExecutionException(this.error);
				case CANCELLED:
					throw new CancellationException();
				case BOUND_VALUE:
					return this.value;
				default:
					throw new IllegalArgumentException();
				}
			}
		} finally {
			barrier.unlock();
		}
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		barrier.lock();
		try {
			if (!this.waitIfNecessary(timeout, unit)) {
				throw new TimeoutException();
			} else {
				switch (status) {
				case BOUND_ERROR:
					throw new ExecutionException(this.error);
				case CANCELLED:
					throw new CancellationException();
				case BOUND_VALUE:
					return this.value;
				default:
					throw new IllegalArgumentException();
				}
			}
		} finally {
			barrier.unlock();
		}
	}

	@Override
	public boolean isCancelled() {
		barrier.lock();
		try {
			return this.status == Status.CANCELLED;
		} finally {
			barrier.unlock();
		}
	}

	@Override
	public boolean isDone() {
		barrier.lock();
		try {
			return this.status == Status.BOUND_VALUE || this.status == Status.BOUND_ERROR;
		} finally {
			barrier.unlock();
		}
	}

	void setValue(V value) {
		barrier.lock();
		try {
			if (!this.isCancelled() && !this.isDone()) {
				this.value = value;
				this.status = Status.BOUND_VALUE;
				this.condition.signalAll();
			}
		} finally {
			barrier.unlock();
		}
	}

	void setError(Throwable error) {
		barrier.lock();
		try {
			if (!this.isCancelled() && !this.isDone()) {
				this.error = error;
				this.status = Status.BOUND_ERROR;
				this.condition.signalAll();
			}
		} finally {
			barrier.unlock();
		}
	}
}
