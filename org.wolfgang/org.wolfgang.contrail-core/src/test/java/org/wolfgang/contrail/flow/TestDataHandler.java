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

package org.wolfgang.contrail.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.wolfgang.common.concurrent.Promise;

/**
 * <code>TestDataHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestDataHandler {

	@Test
	public void testClosable01() throws InterruptedException, ExecutionException, DataFlowException {
		final Promise<String> future = Promise.create();

		final DownStreamDataFlow<String> handler = DataFlows.closable(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		});

		final String message = "Hello,  World!";
		handler.handleData(message);
		assertEquals(message, future.getFuture().get());
	}

	@Test
	public void testClosable02() throws InterruptedException, ExecutionException, DataFlowException {
		final Promise<String> future = Promise.create();

		final DownStreamDataFlow<String> handler = DataFlows.closable(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		});

		handler.handleClose();

		try {
			final String message = "Hello,  World!";
			handler.handleData(message);
			fail();
		} catch (DataFlowCloseException e) {
			// OK
		}
	}

	@Test
	public void testClosable04() throws InterruptedException, ExecutionException, DataFlowException {
		final Promise<String> future = Promise.create();

		final UpStreamDataFlow<String> handler = DataFlows.closable(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		});

		final String message = "Hello,  World!";
		handler.handleData(message);
		assertEquals(message, future.getFuture().get());
	}

	@Test
	public void testClosable05() throws InterruptedException, ExecutionException, DataFlowException {
		final Promise<String> future = Promise.create();

		final UpStreamDataFlow<String> handler = DataFlows.closable(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		});

		handler.handleClose();

		try {
			final String message = "Hello,  World!";
			handler.handleData(message);
			fail();
		} catch (DataFlowCloseException e) {
			// OK
		}
	}

	@Test
	public void testReverse01() throws DataFlowException, InterruptedException, ExecutionException {
		final Promise<String> future = Promise.create();

		final UpStreamDataFlow<String> handler = DataFlows.reverse(DataFlows.closable(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		}));

		final String message = "Hello,  World!";
		handler.handleData(message);
		assertEquals(message, future.getFuture().get());

	}

	@Test
	public void testReverse02() throws DataFlowException, InterruptedException, ExecutionException {
		final Promise<String> future = Promise.create();

		final UpStreamDataFlow<String> handler = DataFlows.reverse(DataFlows.closable(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		}));

		handler.handleClose();

		try {
			final String message = "Hello,  World!";
			handler.handleData(message);
			fail();
		} catch (DataFlowCloseException e) {
			// OK
		}

	}

	@Test
	public void testReverse04() throws DataFlowException, InterruptedException, ExecutionException {
		final Promise<String> future = Promise.create();

		final DownStreamDataFlow<String> handler = DataFlows.reverse(DataFlows.closable(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		}));

		final String message = "Hello,  World!";
		handler.handleData(message);
		assertEquals(message, future.getFuture().get());

	}

	@Test
	public void testReverse05() throws DataFlowException, InterruptedException, ExecutionException {
		final Promise<String> future = Promise.create();

		final DownStreamDataFlow<String> handler = DataFlows.reverse(DataFlows.closable(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.success(data);
			}
		}));

		handler.handleClose();

		try {
			final String message = "Hello,  World!";
			handler.handleData(message);
			fail();
		} catch (DataFlowCloseException e) {
			// OK
		}

	}
}
