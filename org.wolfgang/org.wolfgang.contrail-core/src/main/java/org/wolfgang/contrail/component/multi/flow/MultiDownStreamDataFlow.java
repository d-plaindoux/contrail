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

package org.wolfgang.contrail.component.multi.flow;

import org.wolfgang.contrail.component.multi.MultiSourceComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>MultiUpStreamDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class MultiDownStreamDataFlow<U> implements DataFlow<U> {

	private final MultiSourceComponent<?, U> component;

	public MultiDownStreamDataFlow(MultiSourceComponent<?, U> component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(U data) throws DataFlowException {
		for (SourceComponentLink<?, U> link : component.getSourceComponentLinks()) {
			try {
				link.getSourceComponent().getDownStreamDataFlow().handleData(data);
			} catch (DataFlowException e) {
				// TODO
			}
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		for (SourceComponentLink<?, U> link : component.getSourceComponentLinks()) {
			try {
				link.getSourceComponent().getDownStreamDataFlow().handleClose();
			} catch (DataFlowCloseException e) {
				// TODO
			}
		}
	}

}
