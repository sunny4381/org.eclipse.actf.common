/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Daisuke SATO
 *******************************************************************************/

package org.eclipse.actf.model.flash;

import org.eclipse.actf.model.flash.internal.ASBridge;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class FlashAdjust {

	public static final String ERROR_OK = "OK: "; //$NON-NLS-1$
	public static final String ERROR_NG = "NG: "; //$NON-NLS-1$
	public static final String ERROR_NA = "NA: "; //$NON-NLS-1$
	public static final String ERROR_WAIT = "WAIT: "; //$NON-NLS-1$

	private IDispatch idispFlash = null;

	public FlashAdjust(FlashPlayer flashPlayer) {
		idispFlash = flashPlayer.getDispatch();
	}

	public void dispose() {
		if (idispFlash != null)
			idispFlash.release();
	}

	public void adjust(String newId) {
		if (idispFlash == null)
			return;
		Object o1 = null, o2 = null;
		try {
			o1 = idispFlash.invoke("GetVariable", new Object[] {ASBridge.ROOTLEVEL_PATH + ".Eclipse_ACTF_is_available"});
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			o2 = idispFlash.invoke("GetVariable", new Object[] {ASBridge.BRIDGELEVEL_PATH	+ ".Eclipse_ACTF_is_available"});

		} catch (Exception e) {
			//e.printStackTrace();
		}
		if (o1 != null || o2 != null) {
			setErrorAttribute(ERROR_OK+"Flash DOM detected"); //$NON-NLS-1$
			return;
		}

		String tagName = (String) idispFlash.get("tagName");
		if (!"OBJECT".equals(tagName)) {
			setErrorAttribute(ERROR_NG + tagName + " tag is not supoported"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		Integer readyState = (Integer) idispFlash.get("ReadyState");
		if (readyState != null) {
			if (readyState < 4) {
				setErrorAttribute(ERROR_WAIT
						+ "Flash movie is not ready (ReadyState=" + readyState + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}

		setErrorAttribute(ERROR_NA + "Flash DOM is not available"); //$NON-NLS-1$
	}

	private void setErrorAttribute(String message) {
		idispFlash.invoke("setAttribute",
				new Object[] { "aDesignerError", message });
	}
}
