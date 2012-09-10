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

import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailTerminal;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

/**
 * <code>TestComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
@ContrailTerminal
public class EchoComponent extends TerminalComponent {

	private static UpStreamDataFlowFactory DATA_RECEIVER_FACTORY = new UpStreamDataFlowFactory() {
		@SuppressWarnings("unchecked")
		@Override
		public UpStreamDataFlow create(final DownStreamDataFlow sender) {
			return DataFlows.reverse(sender);
		}
	};

	/**
	 * Constructor
	 * 
	 * @param receiver
	 * @throws CannotCreateDataFlowException
	 */
	@SuppressWarnings("unchecked")
	@ContrailConstructor
	public EchoComponent() throws CannotCreateDataFlowException {
		super(DATA_RECEIVER_FACTORY);
	}

}
