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

package org.wolfgang.contrail.component.pipeline.transducer.flow;

import java.util.List;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.link.ComponentLinkFactory;

/**
 * The <code>TransformationBasedDownStreamDataHandlle</code> is an
 * implementation performing data transformation on the fly each time a data has
 * to be managed.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TransducerDataFlow<U, D> implements DataFlow<U> {

	/**
	 * The component which is in charge of this data handler
	 */
	private final TransducerComponent<?, D, ?, U> component;

	/**
	 * The transformation process
	 */
	private final DataTransducer<U, D> streamXducer;

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
	public TransducerDataFlow(TransducerComponent<?, D, ?, U> component, DataTransducer<U, D> downstreamXducer) {
		this.component = component;
		this.streamXducer = downstreamXducer;
	}

	@Override
	public void handleData(U data) throws DataFlowException {
		if (ComponentLinkFactory.isUndefined(this.component.getSourceComponentLink())) {
			final String message = TransducerComponent.XDUCER_UNKNOWN.format();
			throw new DataFlowException(message);
		} else {
			try {
				final List<D> transform = streamXducer.transform(data);
				for (D value : transform) {
					this.component.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleData(value);
				}
			} catch (ComponentNotConnectedException e) {
				final String message = TransducerComponent.XDUCER_ERROR.format(e.getMessage());
				throw new DataFlowException(message, e);
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
		if (!ComponentLinkFactory.isUndefined(this.component.getSourceComponentLink())) {
			try {
				final List<D> transform = streamXducer.finish();
				for (D value : transform) {
					this.component.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleData(value);
				}
			} catch (ComponentNotConnectedException e) {
				final String message = TransducerComponent.XDUCER_ERROR.format(e.getMessage());
				throw new DataFlowCloseException(message, e);
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
