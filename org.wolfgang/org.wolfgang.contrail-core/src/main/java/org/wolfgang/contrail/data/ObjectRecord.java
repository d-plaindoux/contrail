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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.wolfgang.common.utils.Coercion;

/**
 * <code>ObjectRecord</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ObjectRecord {

	private Map<String, Object> attributes;

	{
		this.attributes = new HashMap<String, Object>();
	}

	public ObjectRecord() {
		// Nothing
	}

	public Set<String> getNames() {
		return this.attributes.keySet();
	}

	public Object get(String name) {
		return this.attributes.get(name);
	}

	public boolean has(String name, Class<?> type) {
		final Object object = this.get(name);
		return Coercion.canCoerce(object, type);
	}

	public boolean hasOrNull(String name, Class<?> type) {
		final Object object = this.get(name);
		return object == null || Coercion.canCoerce(object, type);
	}

	public <T> T get(String name, Class<T> type) {
		final Object object = this.get(name);
		if (Coercion.canCoerce(object, type)) {
			return Coercion.coerce(object, type);
		} else {
			return null;
		}
	}
	
	public ObjectRecord set(String name, Object object) {		
		this.attributes.put(name, object);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectRecord other = (ObjectRecord) obj;
		if (this.attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!this.attributes.equals(other.attributes))
			return false;
		return true;
	}
}
