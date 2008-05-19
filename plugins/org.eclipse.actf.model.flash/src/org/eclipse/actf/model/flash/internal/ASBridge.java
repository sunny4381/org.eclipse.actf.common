/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.internal;

import org.eclipse.actf.model.flash.FlashModelPlugin;
import org.eclipse.actf.model.flash.FlashPlayer;
import org.eclipse.actf.model.flash.IFlashConst;
import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.util.ASDeserializer;
import org.eclipse.actf.model.flash.util.ASSerializer;
import org.eclipse.swt.widgets.Display;

public class ASBridge implements IFlashConst {
	private FlashPlayer player;
	private String requestArgsPath;
	private String responseValuePath;

	private String contentId;
	private String secret;

	public static ASBridge getInstance(FlashPlayer player) {
		if ("true".equals(player.getVariable(PATH_ROOTLEVEL + PATH_IS_AVAILABLE))) { //$NON-NLS-1$ //$NON-NLS-2$
			return new ASBridge(player, PATH_ROOTLEVEL);
		} else if ("true".equals(player.getVariable(PATH_BRIDGELEVEL + PATH_IS_AVAILABLE))) { //$NON-NLS-1$ //$NON-NLS-2$
			return new ASBridge(player, PATH_BRIDGELEVEL);
		}
		return null;
	}

	private ASBridge(FlashPlayer player, String rootPath) {
		this.player = player;
		this.requestArgsPath = rootPath + PROP_REQUEST_ARGS;
		this.responseValuePath = rootPath + PROP_RESPONSE_VALUE;
		this.contentId = rootPath + PATH_CONTENT_ID;
	}

	public Object invoke(Object[] args) {
		int counter = 0;
		try {
			if (secret == null) {
				initSecret();
				if (secret == null)
					return null;
			}
			player.setVariable(responseValuePath, ""); //$NON-NLS-1$
			String argsStr = ASSerializer.serialize(secret, args);
			player.setVariable(requestArgsPath, argsStr);
			while (++counter < 100) {
				String value = player.getVariable(responseValuePath);
				if (null == value)
					return null;
				if (value.length() > 0) {
					ASDeserializer asd = new ASDeserializer(value);
					return asd.deserialize();
				}
				Display.getCurrent().readAndDispatch();// Yield.once();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (1 != counter) {
				System.out.println("Warning: counter=" + counter); //$NON-NLS-1$
				for (int i = 0; i < args.length; i++) {
					System.out.println("args[" + i + "]=" + args[i]); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return null;
	}

	private void initSecret() {
		try {
			String id = player.getVariable(contentId);
			if (id.length() == 0)
				return;
			IWaXcoding waxcoding = FlashModelPlugin.getDefault()
					.getIWaXcoding();
			this.secret = waxcoding.getSecret(id, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
