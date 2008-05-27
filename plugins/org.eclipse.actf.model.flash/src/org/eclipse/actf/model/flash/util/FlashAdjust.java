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

import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.model.flash.IFlashPlayer;
import org.eclipse.actf.util.win32.comclutch.IDispatch;

public class FlashAdjust {

	private IDispatch idispFlash = null;
	IFlashPlayer player = null;

	public FlashAdjust(IFlashPlayer flashPlayer) {
		idispFlash = flashPlayer.getDispatch();
		player = flashPlayer;
	}

	public void dispose() {
		// if (idispFlash != null)
		// idispFlash.release();
	}

	public void adjust(String newId) {
		if (idispFlash == null)
			return;
		Object o1 = null, o2 = null;
		try {
			o1 = idispFlash.invoke(IFlashConst.PLAYER_GET_VARIABLE,
					new Object[] { IFlashConst.PATH_ROOTLEVEL
							+ IFlashConst.PATH_IS_AVAILABLE });
		} catch (Exception e) {
			// e.printStackTrace();
		}
		try {
			o2 = idispFlash.invoke(IFlashConst.PLAYER_GET_VARIABLE,
					new Object[] { IFlashConst.PATH_BRIDGELEVEL
							+ IFlashConst.PATH_IS_AVAILABLE });

		} catch (Exception e) {
			// e.printStackTrace();
		}
		if (o1 != null || o2 != null) {
			setErrorAttribute(IFlashConst.ERROR_OK + "Flash DOM detected"); //$NON-NLS-1$
			return;
		}

		String tagName = (String) idispFlash.get("tagName");
		if (!"OBJECT".equals(tagName)) {
			setErrorAttribute(IFlashConst.ERROR_NG + tagName
					+ " tag is not supoported"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		Integer readyState = (Integer) idispFlash.get("ReadyState");
		if (readyState != null) {
			if (readyState < 4) {
				setErrorAttribute(IFlashConst.ERROR_WAIT
						+ "Flash movie is not ready (ReadyState=" + readyState + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}

		setErrorAttribute(IFlashConst.ERROR_NA + "Flash DOM is not available"); //$NON-NLS-1$
	}

	private void setErrorAttribute(String message) {
		player.setPlayerProperty(IFlashConst.ATTR_ERROR, message);
	}
}
