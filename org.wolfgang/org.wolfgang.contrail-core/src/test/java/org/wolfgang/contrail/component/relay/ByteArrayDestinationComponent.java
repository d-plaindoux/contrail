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

package org.wolfgang.contrail.component.relay;

import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

/**
 * <code>ByteArrayDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ByteArrayDestinationComponent extends TerminalComponent<byte[], byte[]> {

	/**
	 * Constructor
	 * 
	 * @throws CannotCreateDataFlowException
	 */
	public ByteArrayDestinationComponent() throws CannotCreateDataFlowException {
		super(new UpStreamDataFlowFactory<byte[], byte[]>() {
			@Override
			public UpStreamDataFlow<byte[]> create(final DownStreamDataFlow<byte[]> sender) {
				return DataFlows.<byte[]> closable(new UpStreamDataFlowAdapter<byte[]>() {
					@Override
					public void handleData(byte[] data) throws DataFlowException {
						sender.handleData(data);
					}
				});
			}
		});
	}
}
