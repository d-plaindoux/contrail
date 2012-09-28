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

import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
import org.wolfgang.contrail.ecosystem.annotation.ContrailLibrary;
import org.wolfgang.contrail.ecosystem.annotation.ContrailMethod;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;

/**
 * <code>Echo</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailLibrary(name = "Echo")
public class Echo {

	public Echo(ContextFactory contextFactory) {
		super();
	}

	@ContrailMethod
	public EchoComponent echo() throws CannotCreateDataFlowException {
		return new EchoComponent();
	}

	@ContrailMethod
	public EchoComponent echo(final @ContrailArgument("name") String name) throws CannotCreateDataFlowException {
		return new EchoComponent(name);
	}

}
