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

package org.contrail.stream.component.pipeline.transducer;

import org.contrail.common.message.Message;
import org.contrail.common.message.MessagesProvider;
import org.contrail.stream.component.pipeline.AbstractPipelineComponent;
import org.contrail.stream.component.pipeline.transducer.flow.TransducerDownStreamDataFlow;
import org.contrail.stream.component.pipeline.transducer.flow.TransducerUpStreamDataFlow;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.DataFlowFactory;

/**
 * <code>TransducerComponent</code> is an implementation which requires data
 * transformations performed each time an upstream or downstream data go through
 * the connection component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class TransducerComponent<U1, D1, U2, D2> extends AbstractPipelineComponent<U1, D1, U2, D2> {

	/**
	 * Static message definition for unknown transformation
	 */
	public static final Message XDUCER_UNKNOWN;

	/**
	 * Static message definition for transformation error
	 */
	public static final Message XDUCER_ERROR;

	static {
		final String category = "org.contrail.message";

		XDUCER_UNKNOWN = MessagesProvider.from(TransducerComponent.class).get(category, "transducer.upstream.unknown");
		XDUCER_ERROR = MessagesProvider.from(TransducerComponent.class).get(category, "transducer.transformation.error");
	}

	/**
	 * Internal upstream handler performing a data transformation for S to D
	 */
	private final DataFlow<U1> upStreamDataHandler;

	/**
	 * Internal upstream handler performing a data transformation for D to S
	 */
	private final DataFlow<D2> downStreamDataHandler;

	/**
	 * Constructor
	 * 
	 * @param upstreamXducer
	 *            The data transformation used for incoming data (upstream)
	 * @param streamXducer
	 *            The data transformation used for outgoing data (downstream)
	 */
	public TransducerComponent(DataTransducer<U1, U2> upstreamXducer, DataTransducer<D2, D1> downstreamXducer) {
		super();

		this.upStreamDataHandler = DataFlowFactory.<U1> closable(new TransducerUpStreamDataFlow<U1, U2>(this, upstreamXducer));
		this.downStreamDataHandler = DataFlowFactory.<D2> closable(new TransducerDownStreamDataFlow<D2, D1>(this, downstreamXducer));
	}

	@Override
	public DataFlow<U1> getUpStreamDataFlow() {
		return upStreamDataHandler;
	}

	@Override
	public DataFlow<D2> getDownStreamDataFlow() {
		return downStreamDataHandler;
	}
}
