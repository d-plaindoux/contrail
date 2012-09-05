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

package org.wolfgang.contrail.component.pipeline.transducer;

import java.util.List;

import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLinkFactory;

/**
 * The <code>TransducerBasedUpStreamDataHandler</code> is an implementation
 * performing data transformation on the fly each time a data has to be managed.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class TransducerUpStreamDataHandler<U, D> implements UpStreamDataFlow<U> {

	/**
	 * The component which is in charge of this data handler
	 */
	private final TransducerComponent<U, ?, D, ?> component;

	/**
	 * The transformation process
	 */
	private final DataTransducer<U, D> streamXducer;

	/**
	 * Constructor
	 * 
	 * @param upStreamSourceComponent
	 *            The source receiving transformed data
	 * @param streamXducer
	 *            The data transformation process
	 */
	public TransducerUpStreamDataHandler(TransducerComponent<U, ?, D, ?> component, DataTransducer<U, D> downstreamXducer) {
		this.component = component;
		this.streamXducer = downstreamXducer;
	}

	@Override
	public void handleData(U data) throws DataFlowException {
		if (ComponentLinkFactory.isUndefined(component.getDestinationComponentLink())) {
			final String message = TransducerComponent.XDUCER_UNKNOWN.format();
			throw new DataFlowException(message);
		} else {
			try {
				final List<D> transform = streamXducer.transform(data);
				for (D value : transform) {
					component.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(value);
				}
			} catch (DataTransducerException e) {
				final String message = TransducerComponent.XDUCER_ERROR.format(e.getMessage());
				throw new DataFlowException(message, e);
			} catch (RuntimeException e) {
				final String message = TransducerComponent.XDUCER_ERROR.format(e.getMessage());
				throw new DataFlowException(message, e);
			}
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		if (!ComponentLinkFactory.isUndefined(this.component.getDestinationComponentLink())) {
			try {
				final List<D> transform = streamXducer.finish();
				for (D value : transform) {
					component.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(value);
				}
				component.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleClose();
			} catch (DataTransducerException e) {
				final String message = TransducerComponent.XDUCER_ERROR.format(e.getMessage());
				throw new DataFlowCloseException(message, e);
			} catch (DataFlowException e) {
				final String message = TransducerComponent.XDUCER_ERROR.format(e.getMessage());
				throw new DataFlowCloseException(message, e);
			}
		}
	}
}
