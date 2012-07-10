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

package org.wolfgang.contrail.ecosystem.model;

/**
 * <code>Flow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class Flow {

	/**
	 * Constructor
	 */
	private Flow() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Method in charge of decomposing a flow representation
	 * 
	 * @param flow
	 *            The definition
	 * @return a string array (Never <code>null</code>)
	 */
	public static String[] decompose(String flow) {
		if (flow == null) {
			return new String[0];
		} else {
			return flow.split("\\s+");
		}
	}
}
