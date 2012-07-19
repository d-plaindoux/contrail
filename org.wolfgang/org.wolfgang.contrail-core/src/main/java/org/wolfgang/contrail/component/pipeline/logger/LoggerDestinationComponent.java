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

import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>AtomicDestinationPipelineComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
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
		this.downStreamDataHandler = new DownStreamDataHandler<D>() {
			@Override
			public void handleData(final D data) throws DataHandlerException {
				System.err.println("<" + prefix + "> " + data);
				getSourceComponentLink().getSource().getDownStreamDataHandler().handleData(data);
			}

			@Override
			public void handleClose() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestination().closeDownStream();
			}

			@Override
			public void handleLost() throws DataHandlerCloseException {
				getDestinationComponentLink().getDestination().closeDownStream();
			}
		};
	}

	/**
	 * Constructor
	 */
	public LoggerDestinationComponent(String... arguments) {
		super();
		if (arguments.length > 0) {
			this.prefix = arguments[0];
		} else {
			this.prefix = "*";
		}
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.getDestinationComponentLink().getDestination().getUpStreamDataHandler();
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

}
