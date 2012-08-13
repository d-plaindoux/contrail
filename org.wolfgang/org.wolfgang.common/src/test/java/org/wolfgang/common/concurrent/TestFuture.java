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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * <code>TestFuture</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestFuture extends TestCase {

	@Test
	public void testNominal01() {
		final FutureResponse<String> futureResponse = new FutureResponse<String>();
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
	public void testNominal02() {
		final FutureResponse<String> futureResponse = new FutureResponse<String>();
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
	public void testNominal03() {
		final FutureResponse<String> futureResponse = new FutureResponse<String>();
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
	public void testNominal04() {
		final FutureResponse<String> futureResponse = new FutureResponse<String>();
		final String value = "Hello, World!";
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

		try {
			assertEquals(value, futureResponse.get(4, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			fail();
		} catch (TimeoutException e) {
			fail();
		}
	}

	@Test
	public void testNominal05() {
		final FutureResponse<String> futureResponse = new FutureResponse<String>();
		final Throwable value = new Throwable();
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

		try {
			futureResponse.get(4, TimeUnit.SECONDS);
			fail();
		} catch (InterruptedException e) {
			fail();
		} catch (ExecutionException e) {
			assertEquals(value, e.getCause());
		} catch (TimeoutException e) {
			fail();
		}
	}
}
