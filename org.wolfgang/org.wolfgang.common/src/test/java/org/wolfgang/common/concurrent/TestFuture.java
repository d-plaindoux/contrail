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
		final FutureResponse<String> futureResponse = givenAStringFuture();
		final String value = "Hello, World!";
		futureResponse.setValue(value);
		try {
			assertEquals(value, futureResponse.get());
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			fail();
		}
	}

	@Test
	public void ShouldRaiseAnErrorWhenSettingAnError() {
		final FutureResponse<String> futureResponse = givenAStringFuture();
		final Throwable value = new Throwable();
		futureResponse.setError(value);
		try {
			futureResponse.get();
			fail();
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			assertEquals(value, e.getCause());
		}
	}

	@Test
	public void ShouldRaiseATimeOutWhenNoValueSet() {
		final FutureResponse<String> futureResponse = givenAStringFuture();
		try {
			futureResponse.get(1, TimeUnit.SECONDS);
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
		final FutureResponse<String> futureResponse = givenAStringFuture();
		final String value = "Hello, World!";
		
		setAValueWithOneSecondDelay(futureResponse, value);

		try {
			assertEquals(value, futureResponse.get(2, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			fail();
		} catch (TimeoutException e) {
			fail();
		}
	}

	/**
	 * @param futureResponse
	 * @param value
	 */
	private void setAValueWithOneSecondDelay(final FutureResponse<String> futureResponse, final String value) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
				} catch (InterruptedException e) {
					futureResponse.setError(e);
				}
				futureResponse.setValue(value);
			}
		}.start();
	}

	@Test
	public void GivenAFutureSetWithDelayToErrorThrowThError() {
		final FutureResponse<String> futureResponse = givenAStringFuture();
		final Throwable value = new Throwable();

		setAnErrorWithOneSecondDelay(futureResponse, value);

		try {
			futureResponse.get(2, TimeUnit.SECONDS);
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
	 * @return
	 */
	private FutureResponse<String> givenAStringFuture() {
		return new FutureResponse<String>();
	}

	/**
	 * @param futureResponse
	 * @param value
	 */
	private void setAnErrorWithOneSecondDelay(final FutureResponse<String> futureResponse, final Throwable value) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS));
				} catch (InterruptedException e) {
					futureResponse.setError(e);
				}
				futureResponse.setError(value);
			}
		}.start();
	}
}
