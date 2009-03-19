/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Daisuke SATO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash.util;

import org.eclipse.actf.model.flash.FlashPlayerFactory;
import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.model.internal.flash.Messages;
import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ibm.icu.text.MessageFormat;

/**
 * Utility class to detect FlashPlayer
 */
public class FlashDetect {

	private static String strVersion = null;

	static {
		try {
			IDispatch idisp = ComService
					.createDispatch(IFlashConst.CLSID_FLASH);
			String rawVersion = FlashPlayerFactory
					.getPlayerFromIDsipatch(idisp).getPlayerVersion();
			// String rawVersion = (String)
			// idisp.invoke1(IFlashConst.PLAYER_GET_VARIABLE,
			// IFlashConst.PLAYER_VERSION);
			int sep = rawVersion.indexOf(' ');
			if (sep > 0) {
				strVersion = rawVersion.substring(sep + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show information dialog for FlashPlayer if the version is not supported.
	 */
	public static void showDialog() {
		if (null == strVersion)
			return;
		int sep = strVersion.indexOf(',');
		if (sep > 0) {
			try {
				if (Integer.parseInt(strVersion.substring(0, sep)) < 8) {
					MessageDialog.openWarning(Display.getCurrent()
							.getActiveShell(), Messages.flash_warning,
							MessageFormat.format(
									Messages.flash_bad_flash_version,
									new Object[] { strVersion }));
				}
			} catch (Exception e) {
			}
		}
		strVersion = null;
	}

}
