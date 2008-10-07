/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.flash;

import org.eclipse.actf.model.flash.util.FlashMSAAUtil;
import org.eclipse.actf.util.win32.FlashMSAAObject;
import org.eclipse.actf.util.win32.FlashMSAAObjectFactory;
import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;

/**
 * Factory class for {@link IFlashPlayer}
 */
public class FlashPlayerFactory {

	/**
	 * Create {@link IFlashPlayer} instance from pointer
	 * 
	 * @param ptr
	 *            target pointer
	 * @return {@link IFlashPlayer} instance or null if not available
	 */
	public static IFlashPlayer getPlayerFromPtr(int ptr) {
		IUnknown accObject = ComService.newIUnknown(ResourceManager
				.newResourceManager(null), ptr, true);
		IDispatch idisp = FlashMSAAUtil.getHtmlElementFromObject(accObject);
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}

	/**
	 * Create {@link IFlashPlayer} instance from window
	 * 
	 * @param hwnd
	 *            target window handle
	 * @return {@link IFlashPlayer} instance or null if not available
	 */
	public static IFlashPlayer getPlayerFromWindow(int hwnd) {
		FlashMSAAObject accObject = FlashMSAAObjectFactory
				.getFlashMSAAObjectFromWindow(hwnd);
		IDispatch idisp = FlashMSAAUtil.getHtmlElementFromObject(accObject);
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}

	/**
	 * Create {@link IFlashPlayer} instance from {@link FlashMSAAObject}
	 * 
	 * @param accObject
	 *            target {@link FlashMSAAObject}
	 * @return {@link IFlashPlayer} instance or null if not available
	 */
	public static IFlashPlayer getPlayerFromObject(FlashMSAAObject accObject) {
		IDispatch idisp = FlashMSAAUtil.getHtmlElementFromObject(accObject);
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}

	/**
	 * Create {@link IFlashPlayer} instance from {@link IDispatch}
	 * 
	 * @param idisp
	 *            target {@link IDispatch}
	 * @return {@link IFlashPlayer} instance or null if not available
	 */
	public static IFlashPlayer getPlayerFromIDsipatch(IDispatch idisp) {
		if (null != idisp) {
			return new FlashPlayer(idisp);
		}
		return null;
	}

}
