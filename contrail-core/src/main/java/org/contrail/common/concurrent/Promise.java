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

import java.util.concurrent.Future;

/**
 * <code>Promise</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Promise<V, E extends Throwable> {

	private final PromisedFuture<V> future;

	{
		this.future = new PromisedFuture<V>();
	}

	public static <V, E extends Throwable> Promise<V, E> create() {
		return new Promise<V, E>();
	}

	protected Promise() {
		super();
	}

	public Future<V> getFuture() {
		return this.future;
	}

	public void success(V value) {
		future.setValue(value);
	}

	public void failure(E error) {
		future.setError(error);
	}
}
