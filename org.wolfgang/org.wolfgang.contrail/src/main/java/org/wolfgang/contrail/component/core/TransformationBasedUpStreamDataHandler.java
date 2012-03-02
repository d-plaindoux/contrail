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

import org.wolfgang.common.utils.Option;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * The <code>TransformationBasedUpStreamDataHandler</code> is an implementation
 * performing data transformation on the fly each time a data has to be managed.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class TransformationBasedUpStreamDataHandler<S, D> implements UpStreamDataHandler<S> {

	/**
	 * Boolean used to store the handler status i.e. closed or not.
	 */
	private volatile boolean closed = false;

	/**
	 * The upstream which receives the transformation results
	 */
	private final UpStreamDestinationComponent<D> upStreamDestinationComponent;

	/**
	 * The transformation process
	 */
	private final DataTransformation<S, D> downstreamXducer;

	/**
	 * Constructor
	 * 
	 * @param upStreamSourceComponent
	 *            The source receiving transformed data
	 * @param downstreamXducer
	 *            The data transformation process
	 */
	public TransformationBasedUpStreamDataHandler(UpStreamDestinationComponent<D> upStreamDestinationComponent,
			DataTransformation<S, D> downstreamXducer) {
		this.upStreamDestinationComponent = upStreamDestinationComponent;
		this.downstreamXducer = downstreamXducer;
		this.closed = false;
	}

	@Override
	public void handleData(S data) throws DataHandlerException {
		if (closed) {
			throw new DownStreamDataHandlerClosedException();
		} else if (upStreamDestinationComponent == null) {
			throw new DataHandlerException();
		} else {
			try {
				final Option<D> transform = downstreamXducer.transform(data);
				switch (transform.getKind()) {
				case None:
					break;
				case Some:
					upStreamDestinationComponent.getUpStreamDataHandler().handleData(transform.getValue());
					break;
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
