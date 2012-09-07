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
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValueVisitor;
import org.wolfgang.contrail.ecosystem.lang.code.ComponentValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;

/**
 * <code>StringConverter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class AbstractConverter<E> implements Converter<E>, CodeValueVisitor<E, ConversionException> {

	private static Message CLOSURE;
	private static Message CONSTANT;
	private static Message COMPONENTS;

	static {
		CLOSURE = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "cannot.convert.closure");
		CONSTANT = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "cannot.convert.constant");
		COMPONENTS = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "cannot.convert.components");
	}

	protected final Class<E> type;

	/**
	 * Constructor
	 * 
	 * @param type
	 */
	protected AbstractConverter(Class<E> type) {
		super();
		this.type = type;
	}

	@Override
	public final E performConversion(CodeValue value) throws ConversionException {
		return value.visit(this);
	}

	@Override
	public E visit(ClosureValue value) throws ConversionException {
		throw new ConversionException(CLOSURE.format(type.getName(), this.getClass().getSimpleName()));
	}

	@Override
	public E visit(ConstantValue value) throws ConversionException {
		throw new ConversionException(CONSTANT.format(type.getName(), this.getClass().getSimpleName()));
	}

	@Override
	public E visit(ComponentValue value) throws ConversionException {
		throw new ConversionException(COMPONENTS.format(type.getName(), this.getClass().getSimpleName()));
	}

	@Override
	public E visit(FlowValue value) throws ConversionException {
		throw new ConversionException(COMPONENTS.format(type.getName(), this.getClass().getSimpleName()));
	}
}
