/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ie.win32;

import org.eclipse.actf.util.win32.RegistryUtil;

public class RegistryUtilIE extends RegistryUtil {

	public static final String IE_SETTINGS_KEY = "Software\\Microsoft\\Internet Explorer\\Settings", //$NON-NLS-1$
			IE_ANCHOR_COLOR = "Anchor Color", //$NON-NLS-1$
			IE_ANCHOR_COLOR_VISITED = "Anchor Color Visited"; //$NON-NLS-1$

	public static int getIERegistryInt(String valueString) {
		return getRegistryInt(HKEY_CURRENT_USER, IE_SETTINGS_KEY, valueString);
	}

	public static String getIERegistryString(String valueString) {
		return getRegistryString(HKEY_CURRENT_USER, IE_SETTINGS_KEY,
				valueString);
	}

}
