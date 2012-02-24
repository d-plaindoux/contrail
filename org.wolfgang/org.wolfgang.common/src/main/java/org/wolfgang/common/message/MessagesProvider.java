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

package org.wolfgang.common.message;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The <code>MessagesProvider</code> was the central class used for message
 * generation
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class MessagesProvider {

	/**
	 * Class attributes referencing the internal message provider instance. It's
	 * a singleton
	 */
	private static MessagesProvider sharedInstance;

	/**
	 * Static method providing a message
	 * 
	 * @param category The message type
	 * @param key The message identifier
	 * @param parameters Parameters used to format the message
	 * @return a message formatter
	 */
	public static Message get(String category, String key) {
		if (sharedInstance == null) {
			sharedInstance = new MessagesProvider();
		}

		try {
			sharedInstance.loadMessages(category);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "[Cannot load resource bundle] " + e.getMessage());
		}

		return sharedInstance.message(category, key);
	}

	/**
	 * Map containing all resource bundles accessible using a string key.
	 */
	private final Map<String, ResourceBundle> resourceBundles;

	/**
	 * Constructor
	 */
	private MessagesProvider() {
		this.resourceBundles = new HashMap<String, ResourceBundle>();
	}

	/**
	 * Method called whether a property file referencing a resource bundle must
	 * be load
	 * 
	 * @param propertyFile The property file name
	 * @throws IOException Thrown if the file does not exist or cannot be
	 *             accessible
	 */
	private void loadMessages(String category) throws IOException {
		if (!this.resourceBundles.containsKey(category)) {
			this.resourceBundles.put(category, ResourceBundle.getBundle(category));
		}
	}

	/**
	 * Method called whether a message must be performed.
	 * 
	 * @param category The message category
	 * @param key The message key
	 * @param parameters The parameters to be used for by the message formatter
	 * @return a string
	 */
	private Message message(String category, String key) {
		final ResourceBundle resourceBundle = resourceBundles.get(category);
		if (resourceBundle != null) {
			final String message = resourceBundle.getString(key);
			if (message != null) {
				return new Message(message);
			} else {
				return new Message("message not found in [" + category + "] for [" + key + "] in locale ["
						+ Locale.getDefault().getLanguage() + "]");
			}
		} else {
			return new Message("messages not found for [" + category + "]");
		}
	}
}
