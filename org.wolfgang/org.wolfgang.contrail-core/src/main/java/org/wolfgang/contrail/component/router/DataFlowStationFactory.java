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

package org.wolfgang.contrail.component.router;

import org.wolfgang.contrail.reference.DirectReference;

/**
 * <code>DataFlowStationFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public abstract class DataFlowStationFactory {

	public abstract AbstractDataFlowStation create(RouterComponent component);

	public static DataFlowStationFactory forRouter(final DirectReference reference) {
		return new DataFlowStationFactory() {
			@Override
			public AbstractDataFlowStation create(RouterComponent component) {
				return new RouterDataFlowStation(component, reference, new RouterSourceTable());
			}
		};
	}

	public static DataFlowStationFactory forSwitch() {
		return new DataFlowStationFactory() {
			@Override
			public AbstractDataFlowStation create(RouterComponent component) {
				return new SwitchDataFlowStation(component, new RouterSourceTable());
			}
		};
	}
}
