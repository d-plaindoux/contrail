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

package org.contrail.stream.link;

import org.contrail.stream.component.ComponentDisconnectionRejectedException;
import org.contrail.stream.component.DestinationComponent;
import org.contrail.stream.component.SourceComponent;

/**
 * <code>ComponentLinkFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ComponentLinkFactory {

	/**
	 * Constructor
	 */
	private ComponentLinkFactory() {
		super();
	}

	/**
	 * Undefined source component link
	 */
	public static <U, D> SourceComponentLink<U, D> unboundSourceComponentLink() {
		return new SourceComponentLink<U, D>() {

			@Override
			public void dispose() throws ComponentDisconnectionRejectedException {
				// Nothing
			}

			@Override
			public SourceComponent<U, D> getSourceComponent() {
				throw new IllegalAccessError();
			}
		};
	}

	/**
	 * Undefined destination component link
	 */
	public static <U, D> DestinationComponentLink<U, D> unboundDestinationComponentLink() {
		return new DestinationComponentLink<U, D>() {

			@Override
			public void dispose() throws ComponentDisconnectionRejectedException {
				// Nothing
			}

			@Override
			public DestinationComponent<U, D> getDestinationComponent() {
				throw new IllegalAccessError();
			}
		};
	}

	/**
	 * @param componentLink
	 * @return
	 */
	public static boolean isUndefined(SourceComponentLink<?, ?> componentLink) {
		try {
			if (componentLink != null) {
				componentLink.getSourceComponent();
				return false;
			}
		} catch (IllegalAccessError e) {
			// Ignore
		}
		
		return true;
	}

	/**
	 * @param componentLink
	 * @return
	 */
	public static boolean isUndefined(DestinationComponentLink<?, ?> componentLink) {
		try {
			if (componentLink != null) {
				componentLink.getDestinationComponent();
				return false;
			}
		} catch (IllegalAccessError e) {
			// Ignore
		}

		return true;
	}

}
