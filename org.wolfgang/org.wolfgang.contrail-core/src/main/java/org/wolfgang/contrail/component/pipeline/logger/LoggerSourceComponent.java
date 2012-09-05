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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

/**
 * <code>LoggerSourceComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailPipeline(name = "LoggerSource")
public class LoggerSourceComponent<U, D> extends AbstractPipelineComponent<U, D, U, D> {

	/**
	 * The downstream data handler
	 */
	private final UpStreamDataFlow<U> upStreamDataHandler;

	/**
	 * The internal logger
	 */
	private final Logger logger;

	private final String prefix;

	{
		this.upStreamDataHandler = DataFlows.<U> closable(new UpStreamDataFlow<U>() {
			@Override
			public void handleData(final U data) throws DataFlowException {
				try {
					logger.info(prefix + " Handle Data("+ this.hashCode() + "){" + String.valueOf(data) + "}");
					getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(data);
				} catch (DataFlowException e) {
					logger.log(Level.WARNING, "DataFlowException("+ this.hashCode() + ")", e);
					throw e;
				}
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				try {
					logger.log(Level.INFO, prefix + " Handle Close("+ this.hashCode() + ")");
					getDestinationComponentLink().getDestinationComponent().closeUpStream();
				} catch (DataFlowCloseException e) {
					logger.log(Level.WARNING, "DataFlowCloseException("+ this.hashCode() + ")", e);
					throw e;
				}
			}
		});
	}

	/**
	 * Constructor
	 */
	@ContrailConstructor
	public LoggerSourceComponent(@ContrailArgument("name") String prefix) {
		super();
		this.prefix = prefix;
		if (prefix == null) {
			this.logger = Logger.getAnonymousLogger();
		} else {
			this.logger = Logger.getLogger(prefix);
		}
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		return this.upStreamDataHandler;
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		return this.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow();
	}

}
