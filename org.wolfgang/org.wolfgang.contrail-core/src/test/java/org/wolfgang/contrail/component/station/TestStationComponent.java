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

package org.wolfgang.contrail.component.station;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestStationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestStationComponent extends TestCase {

	public static class StringHandler implements IDataStreamHandler<String> {
		private final String filter;

		/**
		 * Constructor
		 * 
		 * @param filter
		 */
		public StringHandler(String filter) {
			super();
			this.filter = filter;
		}

		@Override
		public boolean canAccept(String data) {
			return data.equals(filter);
		}

		@Override
		public String accept(String data) {
			return data;
		}
	}

	@Test
	public void testNominal01() throws ComponentConnectionRejectedException, ComponentNotConnectedException, DataFlowException, InterruptedException, ExecutionException, TimeoutException,
			CannotCreateDataFlowException {
		final FutureResponse<String> response = new FutureResponse<String>();
		final InitialComponent<String, String> initialComponent = Components.initial(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				response.setValue(data);
			}
		});

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowFactory<String, String>() {
			@Override
			public UpStreamDataFlow<String> create(final DownStreamDataFlow<String> component) throws CannotCreateDataFlowException {
				return new UpStreamDataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						component.handleData(data + ", World!");
					}
				};
			}
		});

		final Component initialPipeline = new StationPipeline<String>(new StringHandler("Hello, World!"));
		final Component terminalPipeline = new StationPipeline<String>(new StringHandler("Hello"));
		final Component stationComponent = new StationComponent<String>();
		final ComponentLinkManagerImpl linkManager = new ComponentLinkManagerImpl();

		Components.compose(linkManager, initialComponent, initialPipeline, stationComponent, terminalPipeline, terminalComponent);

		initialComponent.getUpStreamDataFlow().handleData("Hello");
		assertEquals("Hello, World!", response.get(10, TimeUnit.SECONDS));
	}
}
