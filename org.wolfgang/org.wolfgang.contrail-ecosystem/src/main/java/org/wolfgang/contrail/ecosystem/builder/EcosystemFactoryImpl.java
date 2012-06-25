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

package org.wolfgang.contrail.ecosystem.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.wolfgang.contrail.ecosystem.Ecosystem;

/**
 * <code>EcosystemFactoryImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EcosystemFactoryImpl implements EcosystemFactory {

	/**
	 * The imports used for factories resolution
	 */
	private final Collection<String> imports;

	/**
	 * Initial binders
	 */
	private final Collection<DataFlow> binders;

	/**
	 * Internal components
	 */
	private final Collection<DataFlow> internalComponents;

	/* initialization */
	{
		this.imports = new ArrayList<String>();
		this.binders = new ArrayList<DataFlow>();
		this.internalComponents = new ArrayList<DataFlow>();
		this.imports.add(""); // Empty import to be used first 
	}

	/**
	 * Constructor
	 */
	public EcosystemFactoryImpl() {
		super();
	}

	@Override
	public void addImport(String nameSpace) {
		this.imports.add(nameSpace);
	}

	@Override
	public void addBinderDataFlow(DataFlow flow) {
		this.binders.add(flow);
	}

	@Override
	public void addInternalDataFlow(DataFlow flow) {
		this.internalComponents.add(flow);
	}

	private boolean validate(DataFlow dataFlow) {
		return true;
	}

	@Override
	public boolean validate() {
		for (DataFlow dataFlow : binders) {
			if (!validate(dataFlow)) {
				return false;
			}
		}

		for (DataFlow dataFlow : internalComponents) {
			if (!validate(dataFlow)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Ecosystem generateEcosystem() {
		return null;
	}
}
