/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class to obtain commonly used messages for ACTF Model Services
 */
public class ModelServiceMessages {
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private ModelServiceMessages() {
	}

	/**
	 * Gets a string for the given key from the resource bundle of this plugin.
	 * 
	 * @param key
	 *            target key
	 * 
	 * @return the string for the given key, or '!'+key+'!' if not available
	 * @see ResourceBundle#getString(String)
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
