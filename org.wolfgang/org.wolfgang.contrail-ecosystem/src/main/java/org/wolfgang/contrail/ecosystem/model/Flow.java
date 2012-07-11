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

/**
 * <code>Flow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class Flow {

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
		 * Constructor
		 * 
		 * @param name
		 * @param alias
		 */
		private Item(String alias, String name) {
			super();
			this.alias = alias;
			this.name = name;
		}

		/**
		 * Constructor
		 * 
		 * @param name
		 */
		private Item(String name) {
			this(null, name);
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
	}

	/**
	 * Constructor
	 */
	private Flow() {
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
				final int indexOf = s.indexOf('=');
				if (indexOf > 0) {
					items[i] = new Item(s.substring(0, indexOf), s.substring(indexOf + 1));
				} else {
					items[i] = new Item(s);
				}
			}

			return items;
		}
	}
}
