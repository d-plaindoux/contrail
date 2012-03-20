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

package org.wolfgang.contrail.component.Intermediate;

import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>IntegerDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class IntegerDestinationComponent extends TerminalComponent<Integer, Integer> {

	/**
	 * Constructor
	 */
	public IntegerDestinationComponent() {
		super(new TerminalDataReceiverFactory<Integer, Integer>() {
			@Override
			public DataReceiver<Integer> create(final TerminalComponent<Integer, Integer> terminal) {
				return new DataReceiver<Integer>() {
					@Override
					public void receiveData(Integer data) throws DataHandlerException {
						terminal.getDataSender().sendData(data * data);
					}
				};
			}
		});
	}
}