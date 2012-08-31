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

import static org.wolfgang.common.message.MessagesProvider.message;

import org.wolfgang.common.message.Message;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

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
	static final Message XDUCER_UNKNOWN;

	/**
	 * Static message definition for transformation error
	 */
	static final Message XDUCER_ERROR;

	static {
		final String category = "org.wolfgang.contrail.message";

		XDUCER_UNKNOWN = message(category, "transducer.upstream.unknown");
		XDUCER_ERROR = message(category, "transducer.transformation.error");
	}

	/**
	 * Internal upstream handler performing a data transformation for S to D
	 */
	private final UpStreamDataFlow<U1> upStreamDataHandler;

	/**
	 * Internal upstream handler performing a data transformation for D to S
	 */
	private final DownStreamDataFlow<D2> downStreamDataHandler;

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

		this.upStreamDataHandler = DataFlows.<U1> closable(new TransducerUpStreamDataHandler<U1, U2>(this, upstreamXducer));
		this.downStreamDataHandler = DataFlows.<D2> closable(new TransducerDownStreamDataHandler<D2, D1>(this, downstreamXducer));
	}

	@Override
	public UpStreamDataFlow<U1> getUpStreamDataHandler() {
		return upStreamDataHandler;
	}

	@Override
	public DownStreamDataFlow<D2> getDownStreamDataHandler() {
		return downStreamDataHandler;
	}
}
