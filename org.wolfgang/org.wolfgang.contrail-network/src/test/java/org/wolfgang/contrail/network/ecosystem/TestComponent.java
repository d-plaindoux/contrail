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

package org.wolfgang.contrail.network.ecosystem;

import java.io.IOException;

import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataReceiverFactory;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>TestComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class TestComponent extends TerminalComponent {

	private static DataReceiverFactory DATA_RECEIVER_FACTORY = new DataReceiverFactory() {
		@Override
		public DataReceiver create(final DataSender sender) {
			return new DataReceiver() {
				@Override
				public void close() throws IOException {
					sender.close();
				}

				@Override
				@SuppressWarnings("unchecked")
				public void receiveData(Object data) throws DataHandlerException {
					sender.sendData(data);
				}
			};
		}
	};

	/**
	 * Constructor
	 * 
	 * @param receiver
	 */
	@SuppressWarnings("unchecked")
	public TestComponent() {
		super(DATA_RECEIVER_FACTORY);
	}

}
