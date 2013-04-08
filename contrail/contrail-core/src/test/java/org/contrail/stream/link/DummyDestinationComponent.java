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

package org.contrail.stream.link;

import org.contrail.stream.component.ComponentDataFlowFactory;
import org.contrail.stream.component.bound.TerminalComponent;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.DataFlowAdapter;
import org.contrail.stream.flow.exception.CannotCreateDataFlowException;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.flow.exception.DataFlowException;

/**
 * <code>DummyDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DummyDestinationComponent extends TerminalComponent<Void, Void> {

	/**
	 * Constructor
	 * 
	 * @throws CannotCreateDataFlowException
	 */
	public DummyDestinationComponent() throws CannotCreateDataFlowException {
		super(new ComponentDataFlowFactory<Void, Void>() {
			@Override
			public DataFlow<Void> create(final DataFlow<Void> sender) {
				return new DataFlowAdapter<Void>() {
					@Override
					public void handleData(Void data) throws DataFlowException {
						sender.handleData(data);
					}

					@Override
					public void handleClose() throws DataFlowCloseException {
						sender.handleClose();
					}
				};
			}
		});
	}
}
