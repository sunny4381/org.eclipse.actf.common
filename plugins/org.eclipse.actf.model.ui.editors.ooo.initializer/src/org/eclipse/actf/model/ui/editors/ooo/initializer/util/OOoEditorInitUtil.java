/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tatsuya ISHIHARA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui.editors.ooo.initializer.util;

import org.eclipse.actf.util.win32.RegistryUtil;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class for OpenOffice.org (OOo) Editor.
 */
public class OOoEditorInitUtil {

	private static boolean IS_INITIALIZED = false;
	private static final String ENTRY = "SOFTWARE\\OpenOffice.org\\UNO\\InstallPath"; //$NON-NLS-1$

	/**
	 * check existence of OOo.
	 * 
	 * @param showHelp
	 *            if true and OOo does not exist, Help page will be shown
	 * @return true if OOo exists
	 */
	static public boolean isOOoInstalled(boolean showHelp) {

		if (IS_INITIALIZED) {
			return true;
		}

		String path = getOpenOfficePath();
		if (path == null) {
			if (showHelp) {
				//TODO
				PlatformUI
						.getWorkbench()
						.getHelpSystem()
						.displayHelpResource(
								"/org.eclipse.actf.examples.adesigner.doc/docs/odf/install.html"); //$NON-NLS-1$
			}
			return false;
		}

		IS_INITIALIZED = true;
		System.setProperty("OOo_PROGRAM_PATH", path); //$NON-NLS-1$

		return true;
	}

	/**
	 * get OpenOffice.org (later than 2.0) path
	 * 
	 * @return the install path of OpenOffice.org
	 * @throws IllegalArgumentException
	 */
	public static String getOpenOfficePath() {

		return RegistryUtil.getRegistryString(RegistryUtil.HKEY_LOCAL_MACHINE,
				ENTRY, ""); //$NON-NLS-1$
	}

}
