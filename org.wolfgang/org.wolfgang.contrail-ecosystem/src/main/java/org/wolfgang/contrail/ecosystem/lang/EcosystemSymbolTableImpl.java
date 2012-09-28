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

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;

/**
 * <code>EcosystemSymbolTableImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EcosystemSymbolTableImpl implements EcosystemSymbolTable {

	private final EcosystemSymbolTable parentSymbolTable;

	private Map<String, EcosystemImportation<?>> importations;
	private Map<String, CodeValue> definitions;

	{
		this.importations = new HashMap<String, EcosystemImportation<?>>();
		this.definitions = new HashMap<String, CodeValue>();
	}

	/**
	 * Constructor
	 */
	public EcosystemSymbolTableImpl() {
		super();
		this.parentSymbolTable = null;
	}

	/**
	 * The parent ecosystem symbol table
	 * 
	 * @return
	 */
	public EcosystemSymbolTable getParentSymbolTable() {
		return parentSymbolTable;
	}

	/**
	 * Constructor
	 * 
	 * @param parentSymbolTable
	 */
	public EcosystemSymbolTableImpl(EcosystemSymbolTable parentSymbolTable) {
		super();
		this.parentSymbolTable = parentSymbolTable;
	}

	@Override
	public boolean hasImportation(String name) {
		if (this.importations.containsKey(name)) {
			return true;
		} else if (this.parentSymbolTable != null) {
			return this.parentSymbolTable.hasImportation(name);
		} else {
			return false;
		}
	}

	@Override
	public EcosystemImportation<?> getImportation(String name) {
		if (this.importations.containsKey(name)) {
			return this.importations.get(name);
		} else if (this.parentSymbolTable != null) {
			return this.parentSymbolTable.getImportation(name);
		} else {
			return null;
		}
	}

	public void putImportation(String name, EcosystemImportation<?> importation) {
		this.importations.put(name, importation);
	}

	@Override
	public boolean hasDefinition(String name) {
		if (this.definitions.containsKey(name)) {
			return true;
		} else if (this.parentSymbolTable != null) {
			return this.parentSymbolTable.hasDefinition(name);
		} else {
			return false;
		}
	}

	@Override
	public CodeValue getDefinition(String name) {
		if (this.definitions.containsKey(name)) {
			return this.definitions.get(name);
		} else if (this.parentSymbolTable != null) {
			return this.parentSymbolTable.getDefinition(name);
		} else {
			return null;
		}
	}

	public void putDefinition(String name, CodeValue value) {
		this.definitions.put(name, value);
	}
}
