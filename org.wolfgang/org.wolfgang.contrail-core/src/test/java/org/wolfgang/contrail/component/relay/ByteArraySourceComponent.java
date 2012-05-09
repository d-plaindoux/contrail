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

import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialDataReceiverFactory;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>ByteArraySourceComponent</code> is a simple upstream source component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ByteArraySourceComponent extends InitialComponent<byte[], byte[]> {

	/**
	 * Constructor
	 */
	public ByteArraySourceComponent(final OutputStream outputStream) {
		super(new InitialDataReceiverFactory<byte[], byte[]>() {
			@Override
			public DataReceiver<byte[]> create(InitialComponent<byte[], byte[]> initial) {
				return new DataReceiver<byte[]>() {
					public void receiveData(byte[] data) throws DataHandlerException {
						try {
							outputStream.write(data);
						} catch (IOException e) {
							throw new DataHandlerException(e);
						}
					}
					
					@Override
					public void close() throws IOException {
						outputStream.close();								
					}
				};
			}
		});
	}
}
