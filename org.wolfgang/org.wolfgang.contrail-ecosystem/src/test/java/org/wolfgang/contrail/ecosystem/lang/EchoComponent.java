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

package org.wolfgang.contrail.ecosystem.lang;

import java.io.IOException;

import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailTerminal;
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
@ContrailTerminal(name = "Test")
public class EchoComponent extends TerminalComponent {

	private static class LocalDataReceiverFactory implements DataReceiverFactory {
		private final String name;

		/**
		 * Constructor
		 * 
		 * @param name
		 */
		LocalDataReceiverFactory(String name) {
			super();
			this.name = name;
		}

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
					if (name != null) {
						sender.sendData(name + data);
					} else {
						sender.sendData(data);
					}
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
	@ContrailConstructor
	public EchoComponent(@ContrailArgument("name") String name) {
		super(new LocalDataReceiverFactory(name));
	}

}
