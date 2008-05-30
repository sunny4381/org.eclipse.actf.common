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

import org.eclipse.actf.model.flash.ASNode;
import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.model.flash.IFlashPlayer;

public class FlashAdjust {

	private IFlashPlayer player = null;

	public FlashAdjust(IFlashPlayer flashPlayer) {
		player = flashPlayer;
	}

	public void dispose() {
		// if (idispFlash != null)
		// idispFlash.release();
	}

	public void adjust(String newId) {
		if(null==player){
			return;
		}
		
		String tagName = player.getPlayerProperty("tagName");
		if (!"OBJECT".equalsIgnoreCase(tagName)) {
			setErrorAttribute(IFlashConst.ERROR_NG + tagName
					+ " tag is not supoported"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		ASNode rootNode = player.getRootNode();
		if (null != rootNode) {
			setErrorAttribute(IFlashConst.ERROR_OK + "Flash DOM detected"); //$NON-NLS-1$
			return;
		}

		if (!player.isReady()) {
			setErrorAttribute(IFlashConst.ERROR_WAIT
					+ "Flash movie is not ready"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		setErrorAttribute(IFlashConst.ERROR_NA + "Flash DOM is not available"); //$NON-NLS-1$
	}

	private void setErrorAttribute(String message) {
		//System.out.println(message);
		player.setPlayerProperty(IFlashConst.ATTR_ERROR, message);
	}
}
