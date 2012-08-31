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

package org.wolfgang.contrail.component.pipeline;

import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.StreamDataHandlerFactory;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerAdapter;

/**
 * <code>IntegerDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class IntegerDestinationComponent extends TerminalComponent<Integer, Integer> {

	/**
	 * Constructor
	 * 
	 * @throws CannotCreateDataHandlerException
	 */
	public IntegerDestinationComponent() throws CannotCreateDataHandlerException {
		super(new UpStreamDataHandlerFactory<Integer, Integer>() {
			@Override
			public UpStreamDataHandler<Integer> create(final DownStreamDataHandler<Integer> terminal) {
				return StreamDataHandlerFactory.<Integer> create(new UpStreamDataHandlerAdapter<Integer>() {
					@Override
					public void handleData(Integer data) throws DataHandlerException {
						terminal.handleData(data * data);
					}
				});
			}
		});
	}
}
