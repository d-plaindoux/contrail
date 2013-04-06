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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The <code>MessagesProvider</code> was the central class used for message
 * generation.
 * <p>
 * <b>Evolution</b> This should use a dependency injection mechanism
 * </p>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class MessagesProvider extends SecurityManager {

	public interface MessagesProviderWithClassLoader {
		public Message get(String category, String key);
	}

	/**
	 * Class attributes referencing the internal message provider instance. It's
	 * a singleton
	 */
	private static MessagesProvider sharedInstance;

	public static MessagesProviderWithClassLoader from(final Object data) {
		final ClassLoader loader;
		
		if (data instanceof ClassLoader) {
			loader = (ClassLoader) data;
		} else if (data instanceof Class) {
			loader = ((Class<?>) data).getClassLoader();
		} else {
			loader = data.getClass().getClassLoader();
		}

		return new MessagesProviderWithClassLoader() {
			@Override
			public Message get(String category, String key) {
				return MessagesProvider.message(loader, category, key);
			}
		};
	}

	/**
	 * Static method providing a message
	 * 
	 * @param category
	 *            The message type
	 * @param key
	 *            The message identifier
	 * @param parameters
	 *            Parameters used to format the message
	 * @return a message formatter
	 */
	public static synchronized Message message(String category, String key) {
		if (sharedInstance == null) {
			sharedInstance = new MessagesProvider();
		}

		try {
			sharedInstance.loadMessages(sharedInstance.getClass().getClassLoader(), category);
		} catch (MissingResourceException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "[Cannot load resource bundle] " + e.getMessage() + " [key=" + key + "]");
		}

		return sharedInstance.getMessage(category, key);
	}

	/**
	 * Static method providing a message
	 * 
	 * @param category
	 *            The message type
	 * @param key
	 *            The message identifier
	 * @param parameters
	 *            Parameters used to format the message
	 * @return a message formatter
	 */
	private static synchronized Message message(ClassLoader loader, String category, String key) {
		if (sharedInstance == null) {
			sharedInstance = new MessagesProvider();
		}

		try {
			sharedInstance.loadMessages(loader, category);
		} catch (MissingResourceException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "[Cannot load resource bundle] " + e.getMessage() + " [key=" + key + "]");
		}

		return sharedInstance.getMessage(category, key);
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
	 * @param propertyFile
	 *            The property file name
	 */
	private void loadMessages(ClassLoader classLoader, String category) {
		if (!this.resourceBundles.containsKey(category)) {
			try {
				this.resourceBundles.put(category, ResourceBundle.getBundle(category, Locale.getDefault(), classLoader));
			} catch (MissingResourceException e) {
				Logger.getAnonymousLogger().log(Level.WARNING, "[Cannot load resource bundle] " + e.getMessage() + " switching to " + Locale.ENGLISH);
				this.resourceBundles.put(category, ResourceBundle.getBundle(category, Locale.ENGLISH, classLoader));
			}
		}
	}

	/**
	 * Method called whether a message must be performed.
	 * 
	 * @param category
	 *            The message category
	 * @param key
	 *            The message key
	 * @param parameters
	 *            The parameters to be used for by the message formatter
	 * @return a string
	 */
	private Message getMessage(String category, String key) {
		final ResourceBundle resourceBundle = resourceBundles.get(category);
		if (resourceBundle != null) {
			try {
				return new Message(resourceBundle.getString(key));
			} catch (MissingResourceException e) {
				return new Message("message not found in [" + category + "] for [" + key + "]");
			}
		} else {
			return new Message("message bundle not found for [" + category + "]");
		}
	}
}
