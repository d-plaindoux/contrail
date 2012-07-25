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

package org.wolfgang.contrail.ecosystem.model;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.wolfgang.common.message.MessagesProvider;

/**
 * <code>Flow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name="flow")
public class FlowModel implements Validation {

	/**
	 * <code>Item</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public static class Item {

		/**
		 * The name (never <code>null</code>)
		 */
		private final String name;

		/**
		 * The alias (can be <code>null</code>)
		 */
		private final String alias;

		/**
		 * The parameters
		 */
		private final String[] parameters;

		/**
		 * Constructor
		 * 
		 * @param name
		 * @param alias
		 */
		private Item(String alias, String name, String[] parameters) {
			super();
			this.alias = alias;
			this.name = name;
			this.parameters = parameters;
		}

		/**
		 * Constructor
		 * 
		 * @param name
		 */
		private Item(String name, String[] parameters) {
			this(null, name, parameters);
		}

		/**
		 * Return the value of name
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Return the value of alias
		 * 
		 * @return the alias
		 */
		public String getAlias() {
			return alias;
		}

		/**
		 * Predicate checking the alias availability
		 * 
		 * @return true if the alias is defined; false otherwise
		 */
		public boolean asAlias() {
			return alias != null;
		}

		/**
		 * Return the value of parameters
		 * 
		 * @return the parameters
		 */
		public String[] getParameters() {
			return parameters;
		}

		@Override
		public String toString() {
			return "Item [name=" + name + ", alias=" + alias + ", parameters=" + Arrays.toString(parameters) + "]";
		}
		
		
	}

	private String name;
	private String value;

	/**
	 * Constructor
	 */
	public FlowModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Method in charge of decomposing a flow representation
	 * 
	 * @param flow
	 *            The definition
	 * @return a string array (Never <code>null</code>)
	 */
	public static Item[] decompose(String flow) {
		if (flow == null) {
			return new Item[0];
		} else {
			final String[] flows = flow.split("\\s+");
			final Item[] items = new Item[flows.length];

			for (int i = 0; i < items.length; i++) {
				final String s = flows[i];
				final int indexEQ = s.indexOf('=');

				final String alias;
				final String model;

				if (indexEQ > 0) {
					alias = s.substring(0, indexEQ);
					model = s.substring(indexEQ + 1);
				} else {
					alias = null;
					model = s;
				}

				final String[] tokens = model.split("\\(|\\)|,"); // REGEXP ?
				assert tokens.length > 0;
				final String name = tokens[0];
				final String[] parameters = new String[tokens.length - 1];
				System.arraycopy(tokens, 1, parameters, 0, parameters.length);

				items[i] = new Item(alias, name, parameters);
			}

			return items;
		}
	}

	/**
	 * Return the value ofname
	 * 
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the value of value
	 * 
	 * @return the value
	 */
	@XmlValue
	public String getValue() {
		return value.trim();
	}

	/**
	 * Set the value of value
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void validate() throws ValidationException {
		if (this.name == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "name.undefined").format());
		} else if (this.value == null || this.value.trim().length() == 0) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "flow.undefined").format(name));
		}
	}
}
