/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32;

import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IServiceProvider;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;
import org.eclipse.actf.util.win32.msaa.IAccessible;
import org.eclipse.actf.util.win32.msaa.MSAA;

/**
 * The factory class of IAccessibleObject
 */
public class FlashMSAAObjectFactory {
	private static ResourceManager resouceManager = null;
	
	/**
	 * @param hwnd
	 *            the window handle to be used to obtain IAccessibleObject
	 * @return the instance of the IAccessibleObject corresponding to the <i>hwnd</i>
	 * @see IAccessibleObject
	 */
	public static FlashMSAAObject getFlashMSAAObjectFromWindow(long hwnd) {
		if (resouceManager == null) {
			resouceManager = ResourceManager.newResourceManager(null);
		}
		long iaccPtr = MSAA.getAccessibleObjectFromWindow(hwnd);
		IAccessible iacc = ComService.newIAccessible(resouceManager, iaccPtr, true);
		return new FlashMSAAObject(iacc);
	}

	/**
	 * @param iacc
	 *            the address of IAccessible 
	 * @return the instance of the IAccessibleObject corresponding to the <i>hwnd</i>
	 * @see IAccessibleObject
	 */
	public static FlashMSAAObject getFlashMSAAObjectFromPtr(long address) {
		if (resouceManager == null) {
			resouceManager = ResourceManager.newResourceManager(null);
		}
		IAccessible iacc = ComService.newIAccessible(resouceManager, address, true);
		return new FlashMSAAObject(iacc);
	}

	/**
	 * @param iunk
	 *            the IUnknown interface of the HTML element to be used to
	 *            obtain IAccessibleObject
	 * @return the instance of the IAccessibleObject corresponding to the <i>iunk</i>
	 */
	public static FlashMSAAObject getFlashMSAAObjectFromElement(IUnknown iunk) {
		IServiceProvider isp = (IServiceProvider) iunk
				.queryInterface(IUnknown.IID_IServiceProvider);
		if (isp != null) {
			IUnknown iacc = isp.queryService(IUnknown.IID_IAccessible,
					IUnknown.IID_IAccessible);
			if (iunk != null) {
				return new FlashMSAAObject(ComService.newIAccessible(iacc));
			}
		}
		return null;
	}
}
