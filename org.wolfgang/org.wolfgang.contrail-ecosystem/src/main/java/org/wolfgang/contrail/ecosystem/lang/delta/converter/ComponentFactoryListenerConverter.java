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

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.ecosystem.lang.EcosystemCodeValueGeneratorException;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>ComponentFactoryListenerConverter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ComponentFactoryListenerConverter extends AbstractConverter<ComponentFactoryListener> {

	private final ComponentLinkManager linkManager;

	/**
	 * Constructor
	 */
	public ComponentFactoryListenerConverter(ComponentLinkManager linkManager) {
		super(ComponentFactoryListener.class);
		this.linkManager = linkManager;
	}

	@Override
	public ComponentFactoryListener visit(final ClosureValue value) throws ConversionException {
		return new ComponentFactoryListener() {
			@Override
			public void notifyCreation(Component component) throws CannotCreateComponentException {
				try {
					final CodeValue result = value.apply(new ConstantValue(component));
					result.visit(new ComponentConverter(linkManager));
				} catch (EcosystemCodeValueGeneratorException e) {
					throw new CannotCreateComponentException(e);
				} catch (ConversionException e) {
					throw new CannotCreateComponentException(e);
				}
			}
		};
	}
}
