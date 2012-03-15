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

import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;

/**
 * The <code>TransformationBasedDownStreamDataHandlle</code> is an
 * implementation performing data transformation on the fly each time a data has
 * to be managed.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class TransducerBasedDownStreamDataHandler<S, D> implements DownStreamDataHandler<D> {

	/**
	 * The component which is in charge of this data handler 
	 */
	private final TransducerBasedConnectionComponent<S, D> component;

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
	 * @param component
	 *            The related transducer based component
	 * @param upStreamSourceComponent
	 *            The source receiving transformed data
	 * @param streamXducer
	 *            The data transformation process
	 */
	public TransducerBasedDownStreamDataHandler(TransducerBasedConnectionComponent<S, D> component,
			DataTransducer<D, S> downstreamXducer) {
		this.component = component;
		this.streamXducer = downstreamXducer;
	}

	@Override
	public void handleData(D data) throws DataHandlerException {
		if (closed) {
			throw new DownStreamDataHandlerClosedException();
		} else if (this.component.getUpStreamSourceComponent() == null) {
			final String message = TransducerBasedConnectionComponent.XDUCER_UNKNOWN.format();
			throw new DataHandlerException(message);
		} else {
			try {
				final List<S> transform = streamXducer.transform(data);
				for (S value : transform) {
					this.component.getUpStreamSourceComponent().getDownStreamDataHandler().handleData(value);
				}
			} catch (DataTransducerException e) {
				final String message = TransducerBasedConnectionComponent.XDUCER_ERROR.format(e.getMessage());
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
				this.component.getUpStreamSourceComponent().getDownStreamDataHandler().handleData(value);
			}
		} catch (DataTransducerException e) {
			final String message = TransducerBasedConnectionComponent.XDUCER_ERROR.format(e.getMessage());
			throw new DataHandlerCloseException(message, e);
		} catch (DataHandlerException e) {
			final String message = TransducerBasedConnectionComponent.XDUCER_ERROR.format(e.getMessage());
			throw new DataHandlerCloseException(message, e);
		}
	}

	@Override
	public void handleLost() {
		this.closed = true;
	}
}
