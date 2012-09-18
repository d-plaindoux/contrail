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

import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
import org.wolfgang.contrail.ecosystem.annotation.ContrailLibrary;
import org.wolfgang.contrail.ecosystem.annotation.ContrailMethod;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

/**
 * <code>TestComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
@ContrailLibrary
public class EchoComponent extends TerminalComponent {

	private static class LocalDataReceiverFactory implements UpStreamDataFlowFactory {
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

		@SuppressWarnings("unchecked")
		@Override
		public UpStreamDataFlow create(final DownStreamDataFlow sender) {
			return DataFlows.closable(new UpStreamDataFlowAdapter() {
				@Override
				public void handleData(Object data) throws DataFlowException {
					if (name == null) {
						sender.handleData(data);
					} else {
						sender.handleData(name + data);
					}
				}

				@Override
				public void handleClose() throws DataFlowCloseException {
					sender.handleClose();
				}
			});
		}
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 * @throws CannotCreateDataFlowException
	 */
	public EchoComponent() throws CannotCreateDataFlowException {
		this(null);
	}

	@SuppressWarnings("unchecked")
	public EchoComponent(String name) throws CannotCreateDataFlowException {
		super(new LocalDataReceiverFactory(name));
	}

	@ContrailMethod
	public static EchoComponent echo() throws CannotCreateDataFlowException {
		return new EchoComponent();
	}

	@ContrailMethod
	public static EchoComponent echoWithPrefix(@ContrailArgument("name") String name) throws CannotCreateDataFlowException {
		return new EchoComponent(name);
	}
}
