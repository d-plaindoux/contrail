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

import java.io.IOException;
import java.io.OutputStream;

import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.DownStreamDataFlowFactory;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

/**
 * <code>ByteArraySourceComponent</code> is a simple upstream source component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ByteArraySourceComponent {

	/**
	 * Constructor
	 * 
	 * @throws CannotCreateDataFlowException
	 */
	public static InitialComponent<byte[], byte[]> create(final OutputStream outputStream) throws CannotCreateDataFlowException {
		return Components.initial(new DownStreamDataFlowFactory<byte[], byte[]>() {
			@Override
			public DownStreamDataFlow<byte[]> create(UpStreamDataFlow<byte[]> initial) {
				return DataFlows.<byte[]> closable(new DownStreamDataFlowAdapter<byte[]>() {
					public void handleData(byte[] data) throws DataFlowException {
						try {
							outputStream.write(data);
						} catch (IOException e) {
							throw new DataFlowException(e);
						}
					}

					@Override
					public void handleClose() throws DataFlowCloseException {
						try {
							outputStream.close();
						} catch (IOException e) {
							throw new DataFlowCloseException(e);
						}
					}

					@Override
					public void handleLost() throws DataFlowCloseException {
						handleClose();
					}
				});
			}
		});
	}
}
