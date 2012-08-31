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

package org.wolfgang.contrail.component.pipeline.logger;

import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.StreamDataHandlerFactory;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>AtomicDestinationPipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailPipeline(name = "LoggerDestination")
public class LoggerDestinationComponent<U, D> extends AbstractPipelineComponent<U, D, U, D> {

	/**
	 * The downstream data handler
	 */
	private final DownStreamDataHandler<D> downStreamDataHandler;

	/**
	 * The message prefix
	 */
	private final String prefix;

	{
		this.downStreamDataHandler = StreamDataHandlerFactory.<D> closable(new DownStreamDataHandler<D>() {
			@Override
			public void handleData(final D data) throws DataHandlerException {
				getSourceComponentLink().getSourceComponent().getDownStreamDataHandler().handleData(data);
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestinationComponent().closeDownStream();
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestinationComponent().closeDownStream();
			}
		});
	}

	/**
	 * Constructor
	 */
	public LoggerDestinationComponent(String prefix) {
		super();
		this.prefix = prefix;
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.getDestinationComponentLink().getDestinationComponent().getUpStreamDataHandler();
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

}
