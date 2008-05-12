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
import org.eclipse.actf.util.win32.impl.AccessibleObject;
import org.eclipse.actf.util.win32.msaa.IAccessible;
import org.eclipse.actf.util.win32.msaa.MSAA;

public class AccessibleObjectFactory {
	public static IAccessibleObject getAccessibleObjectFromWindow(long hwnd) {
		long iaccPtr = MSAA.getAccessibleObjectFromWindow(hwnd);
		IAccessible iacc = ComService.newIAccessible(ResourceManager.newResourceManager(null), iaccPtr, true);
		return new AccessibleObject(iacc);
	}

    public static IAccessibleObject getAccessibleObjectFromElement(IUnknown iunk) {
    	IServiceProvider isp = (IServiceProvider) iunk.queryInterface(IUnknown.IID_IServiceProvider);
    	if (isp != null) {
    		IUnknown iacc = isp.queryService(IUnknown.IID_IAccessible, IUnknown.IID_IAccessible);
    		if (iunk != null) {
    			return new AccessibleObject(ComService.newIAccessible(iacc));
    		}
    	}
    	return null;
    }
}
