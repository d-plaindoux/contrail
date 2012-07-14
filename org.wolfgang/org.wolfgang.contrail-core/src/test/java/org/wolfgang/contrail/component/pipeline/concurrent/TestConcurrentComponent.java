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

import java.util.Random;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.bound.DataReceiverAdapter;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestConcurrentComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestConcurrentComponent extends TestCase {

	public void testConcurrent01() throws ComponentConnectionRejectedException, DataHandlerException, InterruptedException, ExecutionException {
		final int iterations = 1024;
		final FutureResponse<int[]> responseFuture = new FutureResponse<int[]>();
		final ComponentLinkManagerImpl componentLinkManagerImpl = new ComponentLinkManagerImpl();
		final InitialComponent<Integer, Integer> initialComponent = new InitialComponent<Integer, Integer>(new DataReceiverAdapter<Integer>());
		final TerminalComponent<Integer, Integer> terminalComponent = new TerminalComponent<Integer, Integer>(new DataReceiverAdapter<Integer>() {
			private int location = 0;
			final int[] responses = new int[iterations];

			@Override
			public synchronized void receiveData(Integer data) throws DataHandlerException {
				responses[location++] = data;

				if (location == responses.length) {
					responseFuture.setValue(responses);
				}
			}
		});
		final ConcurrentSourcePipelineComponent<Integer, Integer> concurrentSourcePipelineComponent = new ConcurrentSourcePipelineComponent<Integer, Integer>();

		componentLinkManagerImpl.connect(initialComponent, concurrentSourcePipelineComponent);
		componentLinkManagerImpl.connect(concurrentSourcePipelineComponent, terminalComponent);

		final Random random = new Random();

		long total1 = 0;
		for (int i = 0; i < iterations; i++) {
			int value = random.nextInt(1024);
			total1 += value;
			initialComponent.getDataSender().sendData(value);
		}

		final int[] response = responseFuture.get();
		long total2 = 0;
		for (int i = 0; i < response.length; i++) {
			total2 += response[i];
		}

		assertEquals(total1, total2);
	}

	public void testConcurrent02() throws ComponentConnectionRejectedException, DataHandlerException, InterruptedException, ExecutionException {
		final int iterations = 1024;
		final FutureResponse<int[]> responseFuture = new FutureResponse<int[]>();
		final ComponentLinkManagerImpl componentLinkManagerImpl = new ComponentLinkManagerImpl();
		final InitialComponent<Integer, Integer> initialComponent = new InitialComponent<Integer, Integer>(new DataReceiverAdapter<Integer>() {
			private int location = 0;
			final int[] responses = new int[iterations];

			@Override
			public synchronized void receiveData(Integer data) throws DataHandlerException {
				responses[location++] = data;

				if (location == responses.length) {
					responseFuture.setValue(responses);
				}
			}
		});

		final TerminalComponent<Integer, Integer> terminalComponent = new TerminalComponent<Integer, Integer>(new DataReceiverAdapter<Integer>());
		final ConcurrentDestinationPipelineComponent<Integer, Integer> concurrentSourcePipelineComponent = new ConcurrentDestinationPipelineComponent<Integer, Integer>();

		componentLinkManagerImpl.connect(initialComponent, concurrentSourcePipelineComponent);
		componentLinkManagerImpl.connect(concurrentSourcePipelineComponent, terminalComponent);

		final Random random = new Random();

		long total1 = 0;
		for (int i = 0; i < iterations; i++) {
			int value = random.nextInt(1024);
			total1 += value;
			terminalComponent.getDataSender().sendData(value);
		}

		final int[] response = responseFuture.get();
		long total2 = 0;
		for (int i = 0; i < response.length; i++) {
			total2 += response[i];
		}

		assertEquals(total1, total2);
	}
}