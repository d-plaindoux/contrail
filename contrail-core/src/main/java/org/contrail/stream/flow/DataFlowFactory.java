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

/**
 * <code>StreamDataHandlerFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class DataFlowFactory {

	/**
	 * Constructor
	 */
	private DataFlowFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a closable data flow
	 * 
	 * @param dataHandler
	 * @return
	 */
	public static <U> DataFlow<U> closable(DataFlow<U> dataHandler) {
		return new ClosableDataFlow<U>(dataHandler);
	}

	/**
	 * Creates a filtered data flow
	 * 
	 * @param filter
	 * @param dataHandler
	 * @return
	 */
	public static <U> DataFlow<U> filtered(FilteredDataFlow.Filter<U> filter, DataFlow<U> dataHandler) {
		return new FilteredDataFlow<U>(filter, dataHandler);
	}
}
