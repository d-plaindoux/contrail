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

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.common.utils.ExceptionUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.EvaluableValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>ComponentConverter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ComponentConverter extends AbstractConverter<Component> {

	private final ComponentLinkManager linkManager;

	/**
	 * Constructor
	 * 
	 * @param type
	 */
	public ComponentConverter(ComponentLinkManager linkManager) {
		super(Component.class);
		this.linkManager = linkManager;
	}

	@Override
	public Component visit(ConstantValue value) throws ConversionException {
		final Object result = value.getValue();
		if (Coercion.canCoerce(result, Component.class)) {
			return Coercion.coerce(result, Component.class);
		} else {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "component.required");
			throw new ConversionException(message.format(result));
		}
	}

	@Override
	public Component visit(EvaluableValue value) throws ConversionException {
		try {
			final Object result = value.getValue();
			if (Coercion.canCoerce(result, Component.class)) {
				return Coercion.coerce(result, Component.class);
			} else if (Coercion.canCoerce(result, CodeValue.class)) {
				// An internal method providing an evaluable code so we continue
				return Coercion.coerce(result, CodeValue.class).visit(this);
			} else {
				return super.visit(value);
			}
		} catch (CannotCreateComponentException e) {
			throw new ConversionException(ExceptionUtils.getInitialCause(e));
		}
	}

	@Override
	public Component visit(FlowValue value) throws ConversionException {
		try {
			return value.getValues(linkManager, this);
		} catch (ComponentConnectionRejectedException e) {
			throw new ConversionException(ExceptionUtils.getInitialCause(e));
		}
	}
}
