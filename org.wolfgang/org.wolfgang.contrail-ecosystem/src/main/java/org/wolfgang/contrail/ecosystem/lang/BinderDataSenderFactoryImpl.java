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
import org.wolfgang.contrail.component.bound.CannotCreateDataHandlerException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialUpStreamDataHandler;
import org.wolfgang.contrail.component.bound.UpStreamDataHandlerFactory;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>DataSenderFactoryImpl</code> is dedicated to the binder mechanism
 * creation
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class BinderDataSenderFactoryImpl<U, D> implements UpStreamDataHandlerFactory<U, D> {
	private final EcosystemFactoryImpl factory;
	private final CodeValue flow;

	/**
	 * Constructor
	 * 
	 * @param items
	 */
	BinderDataSenderFactoryImpl(EcosystemFactoryImpl factory, CodeValue flow) {
		super();
		this.factory = factory;
		this.flow = flow;
	}

	@Override
	public UpStreamDataHandler<U> create(DownStreamDataHandler<D> receiver) throws CannotCreateDataHandlerException {
		try {
			final InitialComponent<U, D> initialComponent = new InitialComponent<U, D>(receiver);
			factory.create(initialComponent, flow);
			return new InitialUpStreamDataHandler<U>(initialComponent);
		} catch (CannotCreateComponentException e) {
			throw new CannotCreateDataHandlerException(e);
		} catch (EcosystemBuilderException e) {
			throw new CannotCreateDataHandlerException(e);
		}
	}
}