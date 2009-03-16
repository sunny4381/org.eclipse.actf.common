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
package org.eclipse.actf.model.internal.flash.proxy;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String DeleteCacheAction_0;
	public static String proxy_clear_tip;
	public static String proxy_clear;
	public static String proxy_source;
	public static String proxy_global;
	public static String proxy_time;
	public static String proxy_type;
	public static String proxy_none;
	public static String proxy_show_fine;
	public static String proxy_message;
	public static String proxy_copy;
	public static String proxy_swfmethod;
	public static String proxy_swfmethod_none;
	public static String proxy_swfmethod_bootloader;
	public static String proxy_swfmethod_transcoder;
	public static String proxy_pref_description;
	public static String proxy_deleting_cache;
	public static String proxy_session;
	public static String proxy_swf_version;
	public static String proxy_timeout;
	public static String proxy_port;
	public static String proxy_dialog_text;
	public static String proxy_dialog_ok;
	public static String proxy_cache_clear;
	public static String proxy_cache_confirmation1;
	public static String proxy_cache_clear_when_startup_and_cache_clear;
	public static String proxy_confirm_and_cache_clear;
	public static String proxy_confirm_and_no_operation;
	public static String proxy_cache_clear_when_startup;
	public static String proxy_confirm_cache_clear;
	public static String proxy_no_cache_clear;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}