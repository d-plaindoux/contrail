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

package org.contrail.stream.component.pipeline;

import java.util.concurrent.atomic.AtomicReference;

import org.contrail.stream.component.Components;
import org.contrail.stream.component.bound.InitialComponent;
import org.contrail.stream.flow.DataFlowAdapter;
import org.contrail.stream.flow.DataFlowFactory;
import org.contrail.stream.flow.exception.DataFlowException;

/**
 * <code>StringSourceComponent</code> is a simple upstream source component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class StringSourceComponent {

	/**
	 * Constructor
	 */
	public static InitialComponent<String, String> create(final AtomicReference<String> reference) {
		return Components.initial(DataFlowFactory.<String> closable(new DataFlowAdapter<String>() {
			public void handleData(String data) throws DataFlowException {
				reference.set(data);
			}
		}));
	}
}
