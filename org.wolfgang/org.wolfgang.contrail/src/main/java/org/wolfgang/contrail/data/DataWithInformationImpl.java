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

package org.wolfgang.contrail.data;

/**
 * <code>DataWithInformation</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class DataWithInformationImpl<D> implements DataWithInformation<D> {

	/**
	 * The data information
	 */
	private final DataInformation dataInformation;

	/**
	 * The data
	 */
	private final D data;

	/**
	 * Constructor
	 * 
	 * @param dataInformation
	 * @param data
	 */

	public DataWithInformationImpl(DataInformation dataInformation, D data) {
		super();
		this.dataInformation = dataInformation;
		this.data = data;
	}

	/**
	 * Provides the attached information
	 * 
	 * @return a data information (never <code>null</code>)
	 */
	public DataInformation getDataInformation() {
		return this.dataInformation;
	}

	/**
	 * Provides the basic data
	 * 
	 * @return a data
	 */
	public D getData() {
		return this.data;
	}

}
