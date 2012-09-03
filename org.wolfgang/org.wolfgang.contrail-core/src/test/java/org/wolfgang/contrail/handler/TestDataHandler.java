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

package org.wolfgang.contrail.handler;

import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;

/**
 * <code>TestDataHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestDataHandler extends TestCase {

	@Test
	public void testClosable01() throws InterruptedException, ExecutionException, DataFlowException {
		final FutureResponse<String> future = new FutureResponse<String>();

		final DownStreamDataFlow<String> handler = DataFlows.<String> closable(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.setValue(data);
			}
		});

		final String message = "Hello,  World!";
		handler.handleData(message);
		assertEquals(message, future.get());
	}

	@Test
	public void testClosable02() throws InterruptedException, ExecutionException, DataFlowException {
		final FutureResponse<String> future = new FutureResponse<String>();

		final DownStreamDataFlow<String> handler = DataFlows.<String> closable(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.setValue(data);
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
	public void testClosable03() throws InterruptedException, ExecutionException, DataFlowException {
		final FutureResponse<String> future = new FutureResponse<String>();

		final DownStreamDataFlow<String> handler = DataFlows.<String> closable(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.setValue(data);
			}
		});

		handler.handleLost();

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
		final FutureResponse<String> future = new FutureResponse<String>();

		final UpStreamDataFlow<String> handler = DataFlows.<String> closable(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.setValue(data);
			}
		});

		final String message = "Hello,  World!";
		handler.handleData(message);
		assertEquals(message, future.get());
	}

	@Test
	public void testClosable05() throws InterruptedException, ExecutionException, DataFlowException {
		final FutureResponse<String> future = new FutureResponse<String>();

		final UpStreamDataFlow<String> handler = DataFlows.<String> closable(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.setValue(data);
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
	public void testClosable06() throws InterruptedException, ExecutionException, DataFlowException {
		final FutureResponse<String> future = new FutureResponse<String>();

		final UpStreamDataFlow<String> handler = DataFlows.<String> closable(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				future.setValue(data);
			}
		});

		handler.handleLost();

		try {
			final String message = "Hello,  World!";
			handler.handleData(message);
			fail();
		} catch (DataFlowCloseException e) {
			// OK
		}
	}
}
