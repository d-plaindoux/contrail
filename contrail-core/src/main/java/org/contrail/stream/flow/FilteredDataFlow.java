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

package org.contrail.stream.flow;

import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.flow.exception.DataFlowException;

/**
 * The <code>FilteredDataFlow.java</code> is a specific data flows handling
 * accepted data (based on filter)
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class FilteredDataFlow<D> implements DataFlow<D> {

	public interface Filter<D> {
		boolean accept(D data);
	}

	private final Filter<D> filter;
	private final DataFlow<D> delegated;

	FilteredDataFlow(Filter<D> filter, DataFlow<D> delegated) {
		super();
		this.filter = filter;
		this.delegated = delegated;
	}

	@Override
	public void handleData(D data) throws DataFlowException {
		if (this.filter.accept(data)) {
			this.delegated.handleData(data);
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		this.delegated.handleClose();
	}
}
