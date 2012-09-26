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

package org.wolfgang.contrail.ecosystem.lang.code;

import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.ecosystem.lang.EcosystemImportation;

/**
 * <code>ObjectValue</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EvaluableValue implements CodeValue {
	private final Map<String, CodeValue> environement;
	private final EcosystemImportation<?> entry;

	private Object value;

	/**
	 * Constructor
	 * 
	 * @param environement
	 * @param entry
	 */
	public EvaluableValue(Map<String, CodeValue> environement, EcosystemImportation<?> entry) {
		super();
		this.environement = environement;
		this.entry = entry;
	}

	public Object getValue() throws CannotCreateComponentException {
		if (value == null) {
			try {
				this.value = entry.create(environement);
			} catch (CannotCreateComponentException e) {
				throw e;
			} catch (Exception e) {
				throw new CannotCreateComponentException(e);
			}
		}

		return value;
	}

	@Override
	public <T, E extends Exception> T visit(CodeValueVisitor<T, E> visitor) throws E {
		return visitor.visit(this);
	}
}