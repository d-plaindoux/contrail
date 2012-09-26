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

package org.wolfgang.contrail.ecosystem.lang.delta.converter;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.EvaluableValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;

/**
 * <code>ComponentConverter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ObjectConverter extends AbstractConverter<Object> {

	/**
	 * Constructor
	 * 
	 * @param type
	 */
	public ObjectConverter() {
		super(Object.class);
	}

	@Override
	public Object visit(ClosureValue value) throws ConversionException {
		return this;
	}

	@Override
	public Object visit(ConstantValue value) throws ConversionException {
		return value.getValue();
	}

	@Override
	public Object visit(EvaluableValue value) throws ConversionException {
		try {
			final Object result = value.getValue();
			if (Coercion.canCoerce(result, Component.class)) {
				return Coercion.coerce(result, Component.class);
			} else {
				throw new ConversionException("TODO : Not a component");
			}
		} catch (CannotCreateComponentException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public Object visit(FlowValue value) throws ConversionException {
		return this;
	}
}
