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

package org.wolfgang.contrail.ecosystem.key;

/**
 * <code>Entry</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RegisteredUnitEcosystemKey implements UnitEcosystemKey {

	/**
	 * The unit ecosystem name
	 */
	private final String name;

	/**
	 * Constructor
	 * 
	 * @param upstream
	 *            The upstream class type
	 * @param downstream
	 *            The downstream class type
	 */
	public RegisteredUnitEcosystemKey(String name) {
		super();
		this.name = name;
	}

	/**
	 * @param ecosystemKey
	 * @return
	 */
	public boolean accept(RegisteredUnitEcosystemKey ecosystemKey) {
		return this.equals(ecosystemKey);
	}

	/**
	 * @param ecosystemKey
	 * @return
	 */
	public boolean accept(NamedUnitEcosystemKey ecosystemKey) {
		return ecosystemKey.getName().equals(this.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RegisteredUnitEcosystemKey)) {
			return false;
		}

		RegisteredUnitEcosystemKey other = (RegisteredUnitEcosystemKey) obj;

		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean filteredBy(RegisteredUnitEcosystemKey ecosystemKey) {
		return ecosystemKey.accept(this);
	}

	@Override
	public String toString() {
		return name;
	}
}