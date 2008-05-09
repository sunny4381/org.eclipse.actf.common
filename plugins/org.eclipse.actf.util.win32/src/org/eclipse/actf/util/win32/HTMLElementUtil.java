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

package org.eclipse.actf.util.win32;

import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IServiceProvider;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;


public class HTMLElementUtil {
    
	public static IDispatch getHtmlElementFromObject(Object objUnknown) {
        if( objUnknown instanceof AccessibleObject ) {
        	objUnknown = ((AccessibleObject) objUnknown).getIAccessible();
        } else if (objUnknown instanceof IAccessibleObject) {
        	int ptr = ((IAccessibleObject) objUnknown).getPtr();
        	objUnknown = ComService.newIUnknown(ResourceManager.newResourceManager(null), ptr, false);
        }
        if( objUnknown instanceof IUnknown ) {
        	IServiceProvider sp = (IServiceProvider) ((IUnknown) objUnknown).queryInterface(IUnknown.IID_IServiceProvider);
        	if (sp == null)
        		return null;
        	IUnknown idisp = sp.queryService(IUnknown.IID_IHTMLElement, IUnknown.IID_IHTMLElement);
        	if (idisp == null)
        		return null;
        	return ComService.newIDispatch(idisp);
        }
        return null;
	}


	public static String getHtmlAttribute(Object objUnknown, String name) {
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
