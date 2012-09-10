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

package org.wolfgang.contrail.ecosystem.lang.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>EcosystemModel</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "ecosystem")
public class EcosystemModel {

	/**
	 * Importations
	 */
	private List<Import> importations;

	/**
	 * Definitions
	 */
	private List<Definition> definitions;

	{
		this.importations = new ArrayList<Import>();
		this.definitions = new ArrayList<Definition>();
	}

	/**
	 * Constructor
	 */
	public EcosystemModel() {
		super();
	}

	/**
	 * Return the value of importations
	 * 
	 * @return the importations
	 */
	@XmlElement(name = "import")
	public List<Import> getImportations() {
		return importations;
	}

	/**
	 * Set the value of importations
	 * 
	 * @param importations
	 *            the importations to set
	 */
	public void add(Import importation) {
		this.importations.add(importation);
	}

	/**
	 * Return the value of definitions
	 * 
	 * @return the definitions
	 */
	@XmlElement(name = "define")
	public List<Definition> getDefinitions() {
		return definitions;
	}

	/**
	 * Set the value of definitions
	 * 
	 * @param definitions
	 *            the definitions to set
	 */
	public void add(Definition definition) {
		this.definitions.add(definition);
	}

	/**
	 * Method called to decode a ecosystem
	 * 
	 * @param stream
	 *            The input stream containing the ecosystem definition
	 * @return an ecosystem (Never <code>null</code>)
	 * @throws JAXBException
	 *             throws if the ecosystem cannot be decoded
	 */
	public static EcosystemModel decode(InputStream stream) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(EcosystemModel.class);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		return (EcosystemModel) unmarshaller.unmarshal(stream);
	}
}
