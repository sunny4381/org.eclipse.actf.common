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

package org.eclipse.actf.util.win32;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.util.win32.msaa.MSAA;

public class FlashUtil {

	private static boolean SCAN_ALL = false;
	private static boolean SHOW_OFFSCREEN = false;

	public static boolean isFlash(IAccessibleObject accObject) {
		return isFlashClass(accObject.getClassName())
				|| isInvisibleFlash(accObject);
	}

	public static boolean isInvisibleFlash(IAccessibleObject accObject) {
		if (MSAA.ROLE_SYSTEM_CLIENT == accObject.getAccRole()) {
			String description = accObject.getAccDescription();
			if (null != description && description.startsWith("PLUGIN: type=")) { //$NON-NLS-1$
				return null != HTMLElementUtil.getHtmlAttribute(accObject,
						"WMode"); //$NON-NLS-1$
			}
		}
		return false;
	}

	public static boolean isFlashClass(String className) {
		return "MacromediaFlashPlayerActiveX".equals(className) || //$NON-NLS-1$
				"ShockwaveFlashPlugin".equals(className); //$NON-NLS-1$
	}

	public static IAccessibleObject[] getFlashElements(
			IAccessibleObject inputElement) {
		return getFlashElements(inputElement, SHOW_OFFSCREEN, SCAN_ALL);
	}

	public static IAccessibleObject[] getFlashElements(
			IAccessibleObject accObject, boolean showOffScreen,
			boolean scanAll) {
		FlashFinder finder = new FlashFinder();
		finder.find(accObject, showOffScreen, scanAll);
		return finder.getResults();
	}

	public static boolean isScanAll() {
		return SCAN_ALL;
	}

	public static void setScanAll(boolean scan_all) {
		SCAN_ALL = scan_all;
	}

	public static boolean isShowOffscreen() {
		return SHOW_OFFSCREEN;
	}

	public static void setShowOffscreen(boolean show_offscreen) {
		SHOW_OFFSCREEN = show_offscreen;
	}

	private static class FlashFinder {
		private boolean showOffScreen = false;

		private List<IAccessibleObject> result = new ArrayList<IAccessibleObject>();

		public void find(IAccessibleObject accObject, boolean showOffScreen,
				boolean scanAll) {
			this.showOffScreen = showOffScreen;
			if (scanAll || 0 != findFlashWindow((int) accObject.getWindow())) {
				findChildren(accObject);
			}
		}

		private static int findFlashWindow(int hwnd) {
			if (0 != hwnd) {
				if (FlashUtil.isFlashClass(WindowUtil.GetWindowClassName(hwnd))) {
					return hwnd;
				}
				for (int hwndChild = WindowUtil.GetChildWindow(hwnd); 0 != hwndChild; hwndChild = WindowUtil
						.GetNextWindow(hwndChild)) {
					int hwndFound = findFlashWindow(hwndChild);
					if (0 != hwndFound) {
						return hwndFound;
					}
				}
			}
			return 0;
		}

		private void findChildren(IAccessibleObject[] inputElements) {
			for (int i = 0; i < inputElements.length; i++) {
				findChildren((IAccessibleObject) inputElements[i]);
			}
		}

		private void findChildren(IAccessibleObject accObject) {
			if (null != accObject) {
				int role = accObject.getAccRole();
				if ((MSAA.ROLE_SYSTEM_WINDOW == role || MSAA.ROLE_SYSTEM_CLIENT == role)
						&& FlashUtil.isFlash(accObject)) {
					result.add(accObject);
				} else {
					switch (accObject.getAccState()
							& (MSAA.STATE_INVISIBLE | MSAA.STATE_OFFSCREEN)) {
					case MSAA.STATE_INVISIBLE | MSAA.STATE_OFFSCREEN:
						if (!showOffScreen)
							break;
					case 0:
						findChildren(accObject.getChildren());
						break;
					}
				}
			}
		}

		public IAccessibleObject[] getResults() {
			IAccessibleObject[] resultArray = new IAccessibleObject[result.size()];
			return result.toArray(resultArray);
		}

	}

}
