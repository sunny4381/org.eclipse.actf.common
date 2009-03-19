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

package org.eclipse.actf.model.internal.flash;

import org.eclipse.actf.model.flash.IASNode;
import org.eclipse.actf.model.flash.IFlashPlayer;

public class FlashStatusUtil {

	// messages for status text
	private static final String STATUS_OK = Messages.flash_player_ok; 
	private static final String STATUS_NG = Messages.flash_player_embed; 
	private static final String STATUS_NA = Messages.flash_player_no_xcode; 
	private static final String STATUS_WAIT = Messages.flash_player_loading; 
	private static final String STATUS_HTTPS = Messages.flash_player_https;

	// constants for check
	private static final String OBJECT = "OBJECT"; //$NON-NLS-1$
	private static final String TAG_NAME = "tagName"; //$NON-NLS-1$
	private static final String HTTPS = "https://"; //$NON-NLS-1$

	public static String getStatus(IFlashPlayer player) {
		if (null == player) {
			return null;
		}

		IASNode rootNode = player.getRootNode();
		if (null != rootNode) {
			return STATUS_OK;
		}

		// TODO check swf version here

		String tagName = player.getPlayerProperty(TAG_NAME);
		if (!OBJECT.equalsIgnoreCase(tagName)) {
			return STATUS_NG;
		}

		if (!player.isReady()) {
			return STATUS_WAIT;
		}

		if (player.getContentURL().startsWith(HTTPS)) {
			return STATUS_HTTPS;
		}

		return STATUS_NA;
	}
}
