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

package org.wolfgang.contrail.flow;

import java.util.LinkedList;

import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.flow.exception.DataFlowException;

/**
 * The <code>BufferedDataFlow</code> is a specific data flows storing
 * information in a given buffer.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class BufferedDataFlow<D> implements DataFlow<D> {

	private final LinkedList<D> datas;

	{
		this.datas = new LinkedList<D>();
	}

	@Override
	public void handleData(D data) throws DataFlowException {
		this.datas.addLast(data);
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		this.datas.clear();
	}

	public boolean hasNextData() {
		return !this.datas.isEmpty();
	}

	public D getNextData() {
		return this.datas.removeFirst();
	}
}
