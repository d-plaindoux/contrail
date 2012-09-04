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

package org.wolfgang.contrail.ecosystem.lang.delta;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.ecosystem.lang.EcosystemBuilderException;
import org.wolfgang.contrail.ecosystem.lang.EcosystemComponentBuilder;
import org.wolfgang.contrail.ecosystem.lang.EcosystemInterpretationException;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValueVisitor;
import org.wolfgang.contrail.ecosystem.lang.code.ComponentValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;

public class ParameterCodeConverter implements CodeValueVisitor<Object, Exception> {

	private final EcosystemComponentBuilder builder;

	/**
	 * Constructor
	 * 
	 * @param builder
	 */
	public ParameterCodeConverter(EcosystemComponentBuilder builder) {
		super();
		this.builder = builder;
	}

	@Override
	public Object visit(final ClosureValue value) throws Exception {
		return new ComponentFactoryListener() {
			@Override
			public void notifyCreation(Component component) throws CannotCreateComponentException {
				try {
					value.apply(null, new ConstantValue(component)).visit(builder);
				} catch (EcosystemInterpretationException e) {
					throw new CannotCreateComponentException(e);
				} catch (EcosystemBuilderException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};
	}

	@Override
	public Object visit(ComponentValue value) throws Exception {
		return builder.visit(value);
	}

	@Override
	public Object visit(ConstantValue value) throws Exception {
		return value.getValue();
	}

	@Override
	public Object visit(FlowValue value) throws Exception {
		return builder.visit(value);
	}
}