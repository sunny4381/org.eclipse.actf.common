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
package org.eclipse.actf.model.flash.proxy;

import org.eclipse.actf.model.internal.flash.proxy.ProxyPlugin;
import org.eclipse.actf.model.internal.flash.proxy.preferences.ProxyPreferenceConstants;
import org.eclipse.actf.model.internal.flash.proxy.ui.CacheClearDialog;
import org.eclipse.actf.model.internal.flash.proxy.ui.actions.DeleteCacheAction;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * The utilities to control Internet cache to access object model of Flash
 * content.
 * 
 */
public class FlashCacheUtil {

	public static boolean cacheChecked = false;

	/**
	 * This method shows a dialog to confirm users' preference for cache clear.
	 * If users already selected the "Remove cache without confirmation" option
	 * in the preference, this method will start to remove cache. If users
	 * select "Do not clear the cache", this method will not remove cache. This
	 * method should be called after application launch (postWindowOpen phase in
	 * WorkbenchWindowAdvisor).
	 */
	public static void checkCache() {

		ProxyPlugin proxyPlugin = ProxyPlugin.getDefault();
		IPreferenceStore preferenceStore = proxyPlugin.getPreferenceStore();

		String setting = preferenceStore
				.getString(ProxyPreferenceConstants.P_CACHE_CLEAR);

		boolean clearCacheFlag = true && !cacheChecked;

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		Shell shell = window.getShell();
		if (setting.equals(ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP)
				&& !cacheChecked) {

			CacheClearDialog dialog = null;

			dialog = new CacheClearDialog(shell, Platform.getProduct()
					.getName());
			try {
				// int result =
				dialog.open();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String select = dialog.getSelection();

			if (select
					.equals(ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP_AND_CACHE_CLEAR)) {
				preferenceStore.setValue(
						ProxyPreferenceConstants.P_CACHE_CLEAR,
						ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP);
			} else if (select
					.equals(ProxyPreferenceConstants.CONFIRM_AND_CACHE_CLEAR)) {
				preferenceStore.setValue(
						ProxyPreferenceConstants.P_CACHE_CLEAR,
						ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP);
			} else if (select
					.equals(ProxyPreferenceConstants.CONFIRM_AND_NO_OPERATION)) {
				preferenceStore.setValue(
						ProxyPreferenceConstants.P_CACHE_CLEAR,
						ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP);
				clearCacheFlag = false;
			}
		} else if (setting
				.equals(ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP)
				&& cacheChecked) {
			clearCacheFlag = false;
		} else if (setting.equals(ProxyPreferenceConstants.NO_CACHE_CLEAR)) {
			clearCacheFlag = false;
		}

		if (clearCacheFlag) {
			clearCache(false, window);
			// DeleteCacheAction dca = new DeleteCacheAction();
			// dca.init(window);
			// dca.run(null);
		}
	}

	/**
	 * This method clears Internet cache in background if users select "Remove
	 * cache without confirmation" option in the preference. It is useful to
	 * clear cache in preWindowOpen phase in WorkbenchWindowAdvisor.
	 */
	public static void clearCacheForStartup() {

		String setting = ProxyPlugin.getDefault().getPreferenceStore()
				.getString(ProxyPreferenceConstants.P_CACHE_CLEAR);

		if (setting.equals(ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP)) {
			clearCache(false, null);
			// DeleteCacheAction dca = new DeleteCacheAction();
			// dca.run(null);
		}

	}

	/**
	 * Method to clear SWF cache
	 * 
	 * @param background
	 *            background processing flag
	 * @param window
	 *            active WorkbenchWindow for progress dialog. use null for
	 *            silent mode.
	 */
	public static void clearCache(boolean background, IWorkbenchWindow window) {
		DeleteCacheAction dca = new DeleteCacheAction(background);
		dca.init(window);
		dca.run(null);
	}

}
