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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

/**
 * <code>TestFuture</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestFuture {

	@Test
	public void ShouldProvideAValueWhenSettingAFuture() {
		final Promise<String, Exception> promise = Promise.create();
		final String value = "Hello, World!";
		promise.success(value);
		try {
			assertEquals(value, promise.getFuture().get());
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			fail();
		}
	}

	@Test
	public void ShouldRaiseAnErrorWhenSettingAnError() {
		final Promise<String, Throwable> promise = Promise.create();
		final Throwable value = new Throwable();
		promise.failure(value);
		try {
			promise.getFuture().get();
			fail();
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			assertEquals(value, e.getCause());
		}
	}

	@Test
	public void ShouldRaiseATimeOutWhenNoValueSet() {
		final Promise<String, Exception> promise = Promise.create();
		try {
			promise.getFuture().get(1, TimeUnit.SECONDS);
			fail();
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			fail();
		} catch (TimeoutException e) {
			// OK
		}
	}

	@Test
	public void ShouldProvideAValueWhenSettingAFutureAfter1SecondDelay() {
		final Promise<String, Exception> promise = Promise.create();
		final String value = "Hello, World!";

		setAValueWithOneSecondDelay(promise, value);

		try {
			assertEquals(value, promise.getFuture().get(2, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			fail();
		} catch (TimeoutException e) {
			fail();
		}
	}

	/**
	 * @param promise
	 * @param value
	 */
	private void setAValueWithOneSecondDelay(final Promise<String, Exception> promise, final String value) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
				} catch (InterruptedException e) {
					promise.failure(e);
				}
				promise.success(value);
			}
		}.start();
	}

	@Test
	public void GivenAFutureSetWithDelayToErrorThrowThError() {
		final Promise<String, Throwable> promise = Promise.create();
		final Throwable value = new Throwable();

		setAnErrorWithOneSecondDelay(promise, value);

		try {
			promise.getFuture().get(2, TimeUnit.SECONDS);
			fail();
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			assertEquals(value, e.getCause());
		} catch (TimeoutException e) {
			fail();
		}
	}

	/**
	 * @param promise
	 * @param value
	 */
	private void setAnErrorWithOneSecondDelay(final Promise<String, Throwable> promise, final Throwable value) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
				} catch (InterruptedException e) {
					promise.failure(e);
				}
				promise.failure(value);
			}
		}.start();
	}
}
