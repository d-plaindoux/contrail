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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
public class TestStationComponent {

	public static class StringDataStreamHandler implements IDataStreamHandler<String> {
		private final String filter;

		/**
		 * Constructor
		 * 
		 * @param filter
		 */
		public StringDataStreamHandler(String filter) {
			super();
			this.filter = filter;
		}

		@Override
		public String accept(String data) throws CannotAcceptDataException {
			if (data.equals(filter)) {
				return data;
			} else {
				throw new CannotAcceptDataException();
			}
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

		final Component initialPipeline = new StationPipeline<String, String>(null, new StringDataStreamHandler("Hello, World!"));
		final Component terminalPipeline = new StationPipeline<String, String>(new StringDataStreamHandler("Hello"), null);
		final Component stationComponent = new StationComponent<String, String>();
		final ComponentLinkManagerImpl linkManager = new ComponentLinkManagerImpl();

		Components.compose(linkManager, initialComponent, initialPipeline, stationComponent, terminalPipeline, terminalComponent);

		initialComponent.getUpStreamDataFlow().handleData("Hello");
		assertEquals("Hello, World!", response.get(10, TimeUnit.SECONDS));
	}

	@Test
	public void testNominal02() throws ComponentConnectionRejectedException, ComponentNotConnectedException, DataFlowException, InterruptedException, ExecutionException, TimeoutException,
			CannotCreateDataFlowException {
		final FutureResponse<String> response1 = new FutureResponse<String>();
		final InitialComponent<String, String> initialComponent1 = Components.initial(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				response1.setValue(data);
			}
		});
		final FutureResponse<String> response2 = new FutureResponse<String>();
		final InitialComponent<String, String> initialComponent2 = Components.initial(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				response2.setValue(data);
			}
		});

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowFactory<String, String>() {
			@Override
			public UpStreamDataFlow<String> create(final DownStreamDataFlow<String> component) throws CannotCreateDataFlowException {
				return new UpStreamDataFlowAdapter<String>() {
					@Override
					public void handleData(String data) throws DataFlowException {
						component.handleData(data + ", World! <2>");
					}
				};
			}
		});

		final Component initialPipeline1 = new StationPipeline<String, String>(null, new StringDataStreamHandler("Hello, World! <1>"));
		final Component initialPipeline2 = new StationPipeline<String, String>(null, new StringDataStreamHandler("Hello, World! <2>"));
		final Component stationComponent = new RouterComponent<String, String>();

		final ComponentLinkManagerImpl linkManager = new ComponentLinkManagerImpl();
		final Component station = Components.compose(linkManager, stationComponent, terminalComponent);
		Components.compose(linkManager, initialComponent1, initialPipeline1, station);
		Components.compose(linkManager, initialComponent2, initialPipeline2, station);

		initialComponent1.getUpStreamDataFlow().handleData("Hello");
		assertEquals("Hello, World! <2>", response2.get(10, TimeUnit.SECONDS));
		assertFalse(response1.isCancelled() || response1.isDone());
	}
}
