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

import java.util.List;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.component.ConnectionComponent;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;

/**
 * <code>TransformationBasedConnectionComponent</code> is an implementation
 * which requires data transformations performed each time an upstream or
 * downstrem data go through the connection component.
 * 
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TransformationBasedConnectionComponent<S, D> extends AbstractComponent implements ConnectionComponent<S, D> {

	/**
	 * The <code>TransformationBasedUpStreamDataHandler</code> is an
	 * implementation performing data transformation on the fly each time a data
	 * has to be managed.
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	class TransformationBasedUpStreamDataHandler implements UpStreamDataHandler<S> {

		/**
		 * Boolean used to store the handler status i.e. closed or not.
		 */
		private volatile boolean closed = false;

		/**
		 * The transformation process
		 */
		private final DataTransformation<S, D> streamXducer;

		/**
		 * Constructor
		 * 
		 * @param upStreamSourceComponent
		 *            The source receiving transformed data
		 * @param streamXducer
		 *            The data transformation process
		 */
		public TransformationBasedUpStreamDataHandler(DataTransformation<S, D> downstreamXducer) {
			this.streamXducer = downstreamXducer;
		}

		@Override
		public void handleData(S data) throws DataHandlerException {
			if (closed) {
				throw new UpStreamDataHandlerClosedException();
			} else if (upStreamDestinationComponent == null) {
				throw new DataHandlerException();
			} else {
				try {
					final List<D> transform = streamXducer.transform(data);
					for (D value : transform) {
						upStreamDestinationComponent.getUpStreamDataHandler().handleData(value);
					}
				} catch (DataTransformationException e) {
					throw new DataHandlerException(e);
				}
			}
		}

		@Override
		public void handleClose() {
			this.closed = true;
		}

		@Override
		public void handleLost() {
			this.closed = true;
		}

	}

	/**
	 * The <code>TransformationBasedDownStreamDataHandlle</code> is an
	 * implementation performing data transformation on the fly each time a data
	 * has to be managed.
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private class TransformationBasedDownStreamDataHandler implements DownStreamDataHandler<D> {

		/**
		 * Boolean used to store the handler status i.e. closed or not.
		 */
		private volatile boolean closed = false;

		/**
		 * The transformation process
		 */
		private final DataTransformation<D, S> streamXducer;

		/**
		 * Constructor
		 * 
		 * @param upStreamSourceComponent
		 *            The source receiving transformed data
		 * @param streamXducer
		 *            The data transformation process
		 */
		public TransformationBasedDownStreamDataHandler(DataTransformation<D, S> downstreamXducer) {
			this.streamXducer = downstreamXducer;
		}

		@Override
		public void handleData(D data) throws DataHandlerException {
			if (closed) {
				throw new DownStreamDataHandlerClosedException();
			} else if (upStreamSourceComponent == null) {
				throw new DataHandlerException();
			} else {
				try {
					final List<S> transform = streamXducer.transform(data);
					for (S value : transform) {
						upStreamSourceComponent.getDownStreamDataHandler().handleData(value);
					}
				} catch (DataTransformationException e) {
					throw new DataHandlerException(e);
				}
			}
		}

		@Override
		public void handleClose() {
			this.closed = true;
		}

		@Override
		public void handleLost() {
			this.closed = true;
		}

	}

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
	 * @param upstreamXducer
	 *            The data transformation used for incoming data (upstream)
	 * @param streamXducer
	 *            The data transformation used for outgoing data (downstream)
	 */
	public TransformationBasedConnectionComponent(final DataTransformation<S, D> upstreamXducer,
			final DataTransformation<D, S> downstreamXducer) {
		super(new ComponentId());

		this.upStreamDataHandler = new TransformationBasedUpStreamDataHandler(upstreamXducer);
		this.downStreamDataHandler = new TransformationBasedDownStreamDataHandler(downstreamXducer);
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
		if (this.upStreamSourceComponent != null
				&& this.upStreamSourceComponent.getComponentId().equals(handler.getComponentId())) {
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
		if (this.upStreamDestinationComponent != null
				&& this.upStreamDestinationComponent.getComponentId().equals(handler.getComponentId())) {
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

	@Override
	public void closeUpStream() {
		this.upStreamDataHandler.handleClose();
		this.upStreamDestinationComponent.closeUpStream();
	}

	@Override
	public void closeDownStream() {
		this.downStreamDataHandler.handleClose();
		this.upStreamSourceComponent.closeDownStream();
	}
}
