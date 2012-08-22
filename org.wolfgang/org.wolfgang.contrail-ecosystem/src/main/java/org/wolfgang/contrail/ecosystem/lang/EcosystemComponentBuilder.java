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

package org.wolfgang.contrail.ecosystem.lang;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValueVisitor;
import org.wolfgang.contrail.ecosystem.lang.code.ComponentValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>Interpret</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class EcosystemComponentBuilder implements CodeValueVisitor<Component, EcosystemBuilderException> {

	private final EcosystemInterpreter interpret;
	private final ComponentLinkManager linkManager;

	private Component current;

	/**
	 * Constructor
	 * 
	 * @param environment
	 */
	EcosystemComponentBuilder(EcosystemInterpreter interpret, ComponentLinkManager linkManager, Component current) {
		super();
		this.interpret = interpret;
		this.linkManager = linkManager;
		this.current = current;
	}

	/**
	 * Method called when a link must be established between two components
	 * 
	 * @param source
	 *            The source (Can be <code>null</code>)
	 * @param destination
	 *            The destination (Never <code>null</code>)
	 * @return the destination
	 * @throws ComponentConnectionRejectedException
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Component link(final Component source, final Component destination) throws ComponentConnectionRejectedException {
		if (source != null) {
			try {
				linkManager.connect((SourceComponent) source, (DestinationComponent) destination);
			} catch (ClassCastException e) {
				throw new ComponentConnectionRejectedException(e);
			}
		}

		return destination;
	}

	@Override
	public Component visit(ClosureValue value) throws EcosystemBuilderException {
		return current;
	}

	@Override
	public Component visit(ComponentValue value) throws EcosystemBuilderException {
		try {
			current = link(current, value.getComponent());
		} catch (ComponentConnectionRejectedException e) {
			throw new EcosystemBuilderException(e);
		} catch (CannotCreateComponentException e) {
			throw new EcosystemBuilderException(e);
		}

		return current;
	}

	@Override
	public Component visit(ConstantValue value) throws EcosystemBuilderException {
		return current;
	}

	@Override
	public Component visit(FlowValue value) throws EcosystemBuilderException {
		for (CodeValue item : value.getValues()) {
			item.visit(this);
		}

		return current;
	}

}