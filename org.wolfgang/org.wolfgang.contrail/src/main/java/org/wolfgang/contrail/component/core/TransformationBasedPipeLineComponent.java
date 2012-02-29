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

package org.wolfgang.contrail.component.core;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.HandleDataException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>AbstractPipeLineComponent</code> is an implementation which requires
 * pipeline transducers for the transformations performed each time an upstream
 * or downstrem data go through the pipeline.
 * 
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TransformationBasedPipeLineComponent<S, D> implements PipelineComponent<S, D> {

	/**
	 * Internal upstream handler performing a data transformation for S to D
	 */
	private final UpStreamDataHandler<S> upStreamDataHandler;

	/**
	 * Internal upstream handler performing a data transformation for D to S
	 */
	private final DownStreamDataHandler<D> downStreamDataHandler;

	/**
	 * Related down stream data handler after connection. Null otherwise
	 */
	private UpStreamSourceComponent<S> upStreamSourceComponent;

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private UpStreamDestinationComponent<D> upStreamDestinationComponent;

	/**
	 * Constructor
	 * 
	 * @param upStreamDataHandler
	 */
	public TransformationBasedPipeLineComponent(final DataTransformation<S, D> upstreamXducer, final DataTransformation<D, S> downstreamXducer) {
		this.upStreamDataHandler = new UpStreamDataHandler<S>() {
			@Override
			public void handleData(S data) throws HandleDataException {
				if (upStreamDestinationComponent == null) {
					throw new HandleDataException();
				} else {
					try {
						upStreamDestinationComponent.getUpStreamDataHandler().handleData(upstreamXducer.transform(data));
					} catch (DataTransformationException e) {
						throw new HandleDataException(e);
					}
				}
			}

			@Override
			public void handleClose() {
				// TODO
			}

			@Override
			public void handleLost() {
				// TODO
			}
		};

		this.downStreamDataHandler = new DownStreamDataHandler<D>() {
			@Override
			public void handleData(D data) throws HandleDataException {
				if (upStreamSourceComponent == null) {
					throw new HandleDataException();
				} else {
					try {
						upStreamSourceComponent.getDownStreamDataHandler().handleData(downstreamXducer.transform(data));
					} catch (DataTransformationException e) {
						throw new HandleDataException(e);
					}
				}
			}

			@Override
			public void handleClose() {
				// TODO
			}

			@Override
			public void handleLost() {
				// TODO
			}
		};
	}

	@Override
	public void connect(UpStreamSourceComponent<S> handler) throws ComponentAlreadyConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "already.connected");
			throw new ComponentAlreadyConnectedException(message.format());
		}
	}

	@Override
	public void disconnect(UpStreamSourceComponent<S> handler) throws ComponentNotYetConnectedException {
		if (this.upStreamSourceComponent != null) {
			this.upStreamSourceComponent = null;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotYetConnectedException(message.format());
		}
	}

	@Override
	public void connect(UpStreamDestinationComponent<D> handler) throws ComponentAlreadyConnectedException {
		if (this.upStreamDestinationComponent == null) {
			this.upStreamDestinationComponent = handler;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "already.connected");
			throw new ComponentAlreadyConnectedException(message.format());
		}
	}

	@Override
	public void disconnect(UpStreamDestinationComponent<D> handler) throws ComponentNotYetConnectedException {
		if (this.upStreamDestinationComponent != null) {
			this.upStreamDestinationComponent = null;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotYetConnectedException(message.format());
		}
	}

	@Override
	public UpStreamDataHandler<S> getUpStreamDataHandler() {
		return upStreamDataHandler;
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return downStreamDataHandler;
	}

}