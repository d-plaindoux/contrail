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

package org.wolfgang.contrail.link;

import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.factories.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.bound.handler.DataReceiver;
import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>DummySourceComponent</code> is a simple upstream source component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DummySourceComponent extends InitialComponent<Void, Void> {

	/**
	 * Constructor
	 */
	public DummySourceComponent() {
		super(new InitialDataReceiverFactory<Void, Void>() {
			@Override
			public DataReceiver<Void> create(InitialComponent<Void, Void> initial) {
				return new DataReceiver<Void>() {
					public void receiveData(Void data) throws DataHandlerException {
						// Ignore data
					}
				};
			}
		});
	}
}
