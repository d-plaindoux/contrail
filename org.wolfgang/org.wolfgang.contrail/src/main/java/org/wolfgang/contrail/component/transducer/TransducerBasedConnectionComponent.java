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

package org.wolfgang.contrail.component.transducer;

import java.util.List;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.ConnectionComponent;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;

/**
 * <code>TransducerBasedConnectionComponent</code> is an implementation which
 * requires data transformations performed each time an upstream or downstream
 * data go through the connection component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class TransducerBasedConnectionComponent<S, D> extends AbstractComponent implements ConnectionComponent<S, D> {

	/**
	 * Static message definition for unknown transformation
	 */
	protected static final Message XDUCER_UNKNOWN;

	/**
	 * Static message definition for transformation error
	 */
	protected static final Message XDUCER_ERROR;

	static {
		final String category = "org.wolfgang.contrail.message";

		XDUCER_UNKNOWN = MessagesProvider.get(category, "transducer.upstream.unknown");
		XDUCER_ERROR = MessagesProvider.get(category, "transducer.transformation.error");
	}

	/**
	 * The <code>TransformationBasedUpStreamDataHandler</code> is an
	 * implementation performing data transformation on the fly each time a data
	 * has to be managed.
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	class TransducerBasedUpStreamDataHandler implements UpStreamDataHandler<S> {

		/**
		 * Boolean used to store the handler status i.e. closed or not.
		 */
		private volatile boolean closed = false;

		/**
		 * The transformation process
		 */
		private final DataTransducer<S, D> streamXducer;

		/**
		 * Constructor
		 * 
		 * @param upStreamSourceComponent
		 *            The source receiving transformed data
		 * @param streamXducer
		 *            The data transformation process
		 */
		public TransducerBasedUpStreamDataHandler(DataTransducer<S, D> downstreamXducer) {
			this.streamXducer = downstreamXducer;
		}

		@Override
		public void handleData(S data) throws DataHandlerException {
			if (closed) {
				throw new UpStreamDataHandlerClosedException();
			} else if (upStreamDestinationComponent == null) {
				final String message = XDUCER_UNKNOWN.format();
				throw new DataHandlerException(message);
			} else {
				try {
					final List<D> transform = streamXducer.transform(data);
					for (D value : transform) {
						upStreamDestinationComponent.getUpStreamDataHandler().handleData(value);
					}
				} catch (DataTransducerException e) {
					final String message = XDUCER_ERROR.format(e.getMessage());
					throw new DataHandlerException(message, e);
				}
			}
		}

		@Override
		public void handleClose() throws DataHandlerCloseException {
			this.closed = true;
			try {
				final List<D> transform = streamXducer.finish();
				for (D value : transform) {
					upStreamDestinationComponent.getUpStreamDataHandler().handleData(value);
				}
			} catch (DataTransducerException e) {
				final String message = XDUCER_ERROR.format(e.getMessage());
				throw new DataHandlerCloseException(message, e);
			} catch (DataHandlerException e) {
				final String message = XDUCER_ERROR.format(e.getMessage());
				throw new DataHandlerCloseException(message, e);
			}
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
		private final DataTransducer<D, S> streamXducer;

		/**
		 * Constructor
		 * 
		 * @param upStreamSourceComponent
		 *            The source receiving transformed data
		 * @param streamXducer
		 *            The data transformation process
		 */
		public TransformationBasedDownStreamDataHandler(DataTransducer<D, S> downstreamXducer) {
			this.streamXducer = downstreamXducer;
		}

		@Override
		public void handleData(D data) throws DataHandlerException {
			if (closed) {
				throw new DownStreamDataHandlerClosedException();
			} else if (upStreamSourceComponent == null) {
				final String message = XDUCER_UNKNOWN.format();
				throw new DataHandlerException(message);
			} else {
				try {
					final List<S> transform = streamXducer.transform(data);
					for (S value : transform) {
						upStreamSourceComponent.getDownStreamDataHandler().handleData(value);
					}
				} catch (DataTransducerException e) {
					final String message = XDUCER_ERROR.format(e.getMessage());
					throw new DataHandlerException(message, e);
				}
			}
		}

		@Override
		public void handleClose() throws DataHandlerCloseException {
			this.closed = true;
			try {
				final List<S> transform = streamXducer.finish();
				for (S value : transform) {
					upStreamSourceComponent.getDownStreamDataHandler().handleData(value);
				}
			} catch (DataTransducerException e) {
				final String message = XDUCER_ERROR.format(e.getMessage());
				throw new DataHandlerCloseException(message, e);
			} catch (DataHandlerException e) {
				final String message = XDUCER_ERROR.format(e.getMessage());
				throw new DataHandlerCloseException(message, e);
			}
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
	public TransducerBasedConnectionComponent(DataTransducer<S, D> upstreamXducer, DataTransducer<D, S> downstreamXducer) {
		super();
		this.upStreamDataHandler = new TransducerBasedUpStreamDataHandler(upstreamXducer);
		this.downStreamDataHandler = new TransformationBasedDownStreamDataHandler(downstreamXducer);
	}

	@Override
	public void connect(UpStreamSourceComponent<S> handler) throws ComponentConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(UpStreamSourceComponent<S> handler) throws ComponentNotConnectedException {
		if (this.upStreamSourceComponent != null
				&& this.upStreamSourceComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamSourceComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void connect(UpStreamDestinationComponent<D> handler) throws ComponentConnectedException {
		if (this.upStreamDestinationComponent == null) {
			this.upStreamDestinationComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(UpStreamDestinationComponent<D> handler) throws ComponentNotConnectedException {
		if (this.upStreamDestinationComponent != null
				&& this.upStreamDestinationComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamDestinationComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
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
	public void closeUpStream() throws DataHandlerCloseException {
		try {
			this.upStreamDataHandler.handleClose();
		} finally {
			this.upStreamDestinationComponent.closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		try {
			this.downStreamDataHandler.handleClose();
		} finally {
			this.upStreamSourceComponent.closeDownStream();
		}
	}
}
