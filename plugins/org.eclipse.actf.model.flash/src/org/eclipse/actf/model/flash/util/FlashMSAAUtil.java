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
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.flash.FlashPlayerFactory;
import org.eclipse.actf.model.flash.IFlashPlayer;
import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.FlashMSAAObjectFactory;
import org.eclipse.actf.util.win32.WindowUtil;
import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IServiceProvider;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.msaa.MSAA;

/**
 * Utility class for accessible objects of Flash.
 */
public class FlashMSAAUtil {

	private static boolean SCAN_ALL = false;
	private static boolean SHOW_OFFSCREEN = false;

	/**
	 * @param iacc
	 *            the address of IAccessible
	 * @return whether the target is a Flash object or not.
	 */
	public static boolean isFlash(int iacc) {
		FlashMSAAObject acc = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromPtr(iacc);
		return isFlash(acc);
	}

	/**
	 * @param iacc
	 *            the address of IAccessible
	 * @return whether the <i>accObject</i> is an invisible Flash object or
	 *         not. Invisible means that the Flash object is in window-less
	 *         mode.
	 */
	public static boolean isInvisibleFlash(int iacc) {
		FlashMSAAObject acc = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromPtr(iacc);
		return isInvisibleFlash(acc);
	}

	/**
	 * @param hwnd
	 *            the root window to be used to obtain descendant FlashPlayers.
	 * @return array {@link IFlashPlayer} that are descendant of the root
	 *         window. It is same as getFlashPlayers(hwnd, false, false).
	 */
	public static IFlashPlayer[] getFlashPlayers(int hwnd) {
		return getFlashPlayers(hwnd, SHOW_OFFSCREEN, SCAN_ALL);
	}

	/**
	 * @param hwnd
	 *            the root window to be used to obtain descendant FlashPlayers.
	 * @param showOffScreen
	 *            whether the objects being off screen are collected or not.
	 * @param scanAll
	 *            whether the window-less Flash objects are collected or not.
	 * @return array {@link IFlashPlayer} that are descendant of the root
	 *         window.
	 */
	public static IFlashPlayer[] getFlashPlayers(int hwnd,
			boolean showOffScreen, boolean scanAll) {
		FlashMSAAObject acc = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromWindow(hwnd);
		FlashMSAAObject[] accs = getFlashElements(acc, showOffScreen, scanAll);
		IFlashPlayer[] ret = new IFlashPlayer[accs.length];
		for (int i = 0; i < accs.length; i++) {
			ret[i] = FlashPlayerFactory.getPlayerFromObject(accs[i]);
		}
		return ret;
	}

	/**
	 * @param accObject
	 *            the accessible object to be checked.
	 * @return whether the <i>accObject</i> is a Flash object or not.
	 */
	public static boolean isFlash(FlashMSAAObject accObject) {
		return isFlashClass(accObject.getClassName())
				|| isInvisibleFlash(accObject);
	}

	/**
	 * @param accObject
	 *            the accessible object to be checked.
	 * @return whether the <i>accObject</i> is an invisible Flash object or
	 *         not. Invisible means that the Flash object is in window-less
	 *         mode.
	 */
	public static boolean isInvisibleFlash(FlashMSAAObject accObject) {
		if (MSAA.ROLE_SYSTEM_CLIENT == accObject.getAccRole()) {
			String description = accObject.getAccDescription();
			if (null != description && description.startsWith("PLUGIN: type=")) { //$NON-NLS-1$
				return null != FlashMSAAUtil.getHtmlAttribute(accObject,
						"WMode"); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * Check if the class name relates to Flash content
	 * 
	 * @param className
	 *            target class name
	 * @return true if class name is <i>MacromediaFlashPlayerActiveX</i> or
	 *         <i>ShockwaveFlashPlugin</i>
	 */
	public static boolean isFlashClass(String className) {
		return "MacromediaFlashPlayerActiveX".equals(className) || //$NON-NLS-1$
				"ShockwaveFlashPlugin".equals(className); //$NON-NLS-1$
	}

	/**
	 * @param root
	 *            the root element to be used to obtain its children.
	 * @return the children of the <i>inputElement</i>. It is same as
	 *         getFlashElements(inputElement, false, false).
	 */
	public static FlashMSAAObject[] getFlashElements(FlashMSAAObject root) {
		return getFlashElements(root, SHOW_OFFSCREEN, SCAN_ALL);
	}

	/**
	 * @param root
	 *            the root element to be traversed to collect Flash objects.
	 * @param showOffScreen
	 *            whether the objects being off screen are collected or not.
	 * @param scanAll
	 *            whether the window-less Flash objects are collected or not.
	 * @return the collected Flash objects.
	 */
	public static FlashMSAAObject[] getFlashElements(FlashMSAAObject root,
			boolean showOffScreen, boolean scanAll) {
		FlashFinder finder = new FlashFinder();
		finder.find(root, showOffScreen, scanAll);
		return finder.getResults();
	}

	/**
	 * @return the scan mode of Flash finder. If it is true then the window-less
	 *         Flash objects are also collected.
	 */
	public static boolean isScanAll() {
		return SCAN_ALL;
	}

	/**
	 * @param scan_all
	 *            the scan mode of Flash finder. If it is true then the
	 *            window-less Flash objects are also collected.
	 */
	public static void setScanAll(boolean scan_all) {
		SCAN_ALL = scan_all;
	}

	/**
	 * @return the scan mode of Flash finder. If it is true then the off screen
	 *         objects are also collected.
	 */
	public static boolean isShowOffscreen() {
		return SHOW_OFFSCREEN;
	}

	/**
	 * @param show_offscreen
	 *            the scan mode of Flash finder. If it is true then the off
	 *            screen objects are also collected.
	 */
	public static void setShowOffscreen(boolean show_offscreen) {
		SHOW_OFFSCREEN = show_offscreen;
	}

	private static class FlashFinder {
		private boolean showOffScreen = false;

		private List<FlashMSAAObject> result = new ArrayList<FlashMSAAObject>();

		public void find(FlashMSAAObject accObject, boolean showOffScreen,
				boolean scanAll) {
			this.showOffScreen = showOffScreen;
			if (scanAll || 0 != findFlashWindow((int) accObject.getWindow())) {
				findChildren(accObject);
			}
		}

		private static int findFlashWindow(int hwnd) {
			if (0 != hwnd) {
				if (FlashMSAAUtil.isFlashClass(WindowUtil
						.GetWindowClassName(hwnd))) {
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

		private void findChildren(FlashMSAAObject[] inputElements) {
			for (int i = 0; i < inputElements.length; i++) {
				findChildren((FlashMSAAObject) inputElements[i]);
			}
		}

		private void findChildren(FlashMSAAObject accObject) {
			if (null != accObject) {
				int role = accObject.getAccRole();
				if ((MSAA.ROLE_SYSTEM_WINDOW == role || MSAA.ROLE_SYSTEM_CLIENT == role)
						&& FlashMSAAUtil.isFlash(accObject)) {
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

		public FlashMSAAObject[] getResults() {
			FlashMSAAObject[] resultArray = new FlashMSAAObject[result.size()];
			return result.toArray(resultArray);
		}

	}

	/**
	 * @param iacc
	 *            the address of IAccessible
	 * @return the HTML element interface of the <i>objUnknown</i>.
	 */
	public static IDispatch getHtmlElementFromPtr(int iacc) {
		FlashMSAAObject acc = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromPtr(iacc);
		return getHtmlElementFromObject(acc);
	}

	/**
	 * @param iacc
	 *            the address of IAccessible
	 * @param name
	 *            target attribute name
	 * @return the attribute value string.
	 */
	public static String getHtmlAttribute(int iacc, String name) {
		FlashMSAAObject acc = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromPtr(iacc);
		return getHtmlAttribute(acc, name);
	}

	/**
	 * @param objUnknown
	 *            the object to be used to obtain the interface of HTML element.
	 *            It must be an instance of IAccessibleObject or an instance of
	 *            IUnknown
	 * @return the HTML element interface of the <i>objUnknown</i>.
	 * @see IAccessibleObject
	 * @see IUnknown
	 */
	public static IDispatch getHtmlElementFromObject(Object objUnknown) {
		if (objUnknown instanceof FlashMSAAObject) {
			objUnknown = ((FlashMSAAObject) objUnknown).getIAccessible();
		}
		if (objUnknown instanceof IUnknown) {
			IServiceProvider sp = (IServiceProvider) ((IUnknown) objUnknown)
					.queryInterface(IUnknown.IID_IServiceProvider);
			if (sp == null)
				return null;
			IUnknown idisp = null;
			try {
				idisp = sp.queryService(IUnknown.IID_IHTMLElement,
						IUnknown.IID_IHTMLElement);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (idisp == null)
				return null;
			return ComService.newIDispatch(idisp);
		}
		return null;
	}

	/**
	 * @param objUnknown
	 *            the object to be used to obtain the interface of HTML element.
	 *            It must be an instance of IAccessibleObject or an instance of
	 *            IUnknown
	 * @param name
	 *            the attribute name to be used.
	 * @return the attribute value string.
	 * @see #getHtmlElementFromObject(Object)
	 */
	private static String getHtmlAttribute(Object objUnknown, String name) {
		IDispatch varElement = getHtmlElementFromObject(objUnknown);
		if (varElement == null)
			return null;
		try {
			return (String) varElement.get(name);
		} catch (Exception e) {
		}
		return null;
	}

}
