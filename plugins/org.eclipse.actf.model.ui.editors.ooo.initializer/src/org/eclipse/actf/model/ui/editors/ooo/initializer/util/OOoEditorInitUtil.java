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

import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.ui.PlatformUI;

public class OOoEditorInitUtil {

	private static boolean IS_INITIALIZED = false;
	
	public static boolean init() {
		return isOOoInstalled(false);
	}

	static public boolean isOOoInstalled(boolean showHelp) {

		if (IS_INITIALIZED) {
			return true;
		}

		String path = null;
		try {
			path = getOpenOfficePath();
		} catch (IllegalArgumentException e) {
		}
		if (path == null) {
			if (showHelp) {
				PlatformUI
						.getWorkbench()
						.getHelpSystem()
						.displayHelpResource(
								"/org.eclipse.actf.examples.adesigner.doc/docs/odf/install.html");
			}
			return false;
		}

		IS_INITIALIZED = true;
		System.setProperty("OOo_PROGRAM_PATH", path);

		return true;
	}

	/**
	 * get OpenOffice (later than 2.0) path
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("restriction")
	public static String getOpenOfficePath() throws IllegalArgumentException {

		final String entry = "SOFTWARE\\OpenOffice.org\\UNO\\InstallPath";

		final int[] hKey = new int[1];
		try {
			int rc = OS.RegOpenKeyEx(OS.HKEY_LOCAL_MACHINE, new TCHAR(
					OS.CP_INSTALLED, entry, true), 0, 0xF003F, hKey);
			if (0 != rc) {
				throw new IllegalArgumentException(
						"Failed to call RegOpenKeyEx.");
			}

			TCHAR buf = new TCHAR(OS.CP_INSTALLED, 256);
			final int[] len = new int[] { 256 };
			rc = OS.RegQueryValueEx(hKey[0], (TCHAR) null, 0, null, buf, len);
			if (0 != rc) {
				throw new IllegalArgumentException(
						"Failed to call RegQueryValueEx.");
			}

			return buf.toString(0, buf.strlen());
		} finally {
			if (hKey[0] != 0) {
				OS.RegCloseKey(hKey[0]);
			}
		}
	}

	public static boolean isInitialized() {
		return IS_INITIALIZED;
	}

}
