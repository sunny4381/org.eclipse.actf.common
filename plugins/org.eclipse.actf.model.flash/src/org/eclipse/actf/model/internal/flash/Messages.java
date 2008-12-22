/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String flash_player_ok;
	public static String flash_player_loading;
	public static String flash_player_embed;
	public static String flash_player_no_dom;
	public static String flash_player_no_xcode;
	public static String flash_player_unknown;
	public static String flash_player_https;
	public static String flash_bad_flash_version;
	public static String flash_error_target_length;
	public static String flash_warning;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}