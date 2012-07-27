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

package org.wolfgang.contrail.component.pipeline.compose;

import java.io.IOException;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>ComposedPipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailPipeline(name = "Composition")
public class CompositionComponent<U1, D1, U2, D2> extends AbstractPipelineComponent<U1, D1, U2, D2> {

	private final UpStreamDataHandler<U1> upStreamDataHandler;
	private final InitialComponent<U1, D1> initialComponent;
	private final TerminalComponent<U2, D2> terminalComponent;
	private final DownStreamDataHandler<D2> downStreamDataHandler;

	{
		final DataReceiver<D1> sourceReceiver = new DataReceiver<D1>() {
			@Override
			public void close() throws IOException {
				try {
					getSourceComponentLink().getSource().getDownStreamDataHandler().handleClose();
				} catch (DataHandlerCloseException e) {
					throw new IOException(e);
				}
			}

			@Override
			public void receiveData(D1 data) throws DataHandlerException {
				getSourceComponentLink().getSource().getDownStreamDataHandler().handleData(data);
			}
		};

		final DataReceiver<U2> destinationReceiver = new DataReceiver<U2>() {
			@Override
			public void close() throws IOException {
				try {
					getDestinationComponentLink().getDestination().getUpStreamDataHandler().handleClose();
				} catch (DataHandlerCloseException e) {
					throw new IOException(e);
				}
			}

			@Override
			public void receiveData(U2 data) throws DataHandlerException {
				getDestinationComponentLink().getDestination().getUpStreamDataHandler().handleData(data);
			}
		};

		this.initialComponent = new InitialComponent<U1, D1>(sourceReceiver);
		this.terminalComponent = new TerminalComponent<U2, D2>(destinationReceiver);

		this.upStreamDataHandler = new UpStreamDataHandler<U1>() {
			@Override
			public void handleData(U1 data) throws DataHandlerException {
				initialComponent.getDataSender().sendData(data);
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				initialComponent.closeUpStream();
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				initialComponent.closeUpStream();
			}

		};

		this.downStreamDataHandler = new DownStreamDataHandler<D2>() {
			@Override
			public void handleData(D2 data) throws DataHandlerException {
				terminalComponent.getDataSender().sendData(data);
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				terminalComponent.closeDownStream();
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				terminalComponent.closeDownStream();
			}
		};

	}

	/**
	 * Constructor
	 * 
	 * @throws ComponentConnectionRejectedException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CompositionComponent(PipelineComponent... pipelines) throws ComponentConnectionRejectedException {
		super();

		assert pipelines.length > 0;

		final ComponentLinkManagerImpl componentLinkManagerImpl = new ComponentLinkManagerImpl();
		componentLinkManagerImpl.connect(initialComponent, pipelines[0]);
		for (int i = 1; i < pipelines.length; i++) {
			componentLinkManagerImpl.connect(pipelines[i - 1], pipelines[i]);
		}
		componentLinkManagerImpl.connect(pipelines[pipelines.length - 1], terminalComponent);
	}

	@Override
	public DownStreamDataHandler<D2> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

	@Override
	public UpStreamDataHandler<U1> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

}
