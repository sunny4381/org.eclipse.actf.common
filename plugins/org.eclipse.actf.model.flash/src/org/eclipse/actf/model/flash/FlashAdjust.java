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

package org.eclipse.actf.model.flash;

import org.eclipse.actf.model.flash.internal.ASBridge;
import org.eclipse.actf.util.win32.HTMLElementUtil;
import org.eclipse.actf.util.win32.comclutch.IDispatch;



public class FlashAdjust {

    public static final String ERROR_OK =   "OK: "; //$NON-NLS-1$
    public static final String ERROR_NG =   "NG: "; //$NON-NLS-1$
    public static final String ERROR_NA =   "NA: "; //$NON-NLS-1$
    public static final String ERROR_WAIT = "WAIT: "; //$NON-NLS-1$
    
    private IDispatch idispFlash = null;
    
    public FlashAdjust(Object flashObject/*, int validate*/) {
    	idispFlash = HTMLElementUtil.getHtmlElementFromObject(flashObject);
    }
    
    public void dispose() {
    	if (idispFlash != null) idispFlash.release();
    }
    
    public void adjust(String newId) {
    	if (idispFlash == null) return;
    	if (idispFlash.invoke1("GetVariable",  ASBridge.ROOTLEVEL_PATH+".Eclipse_ACTF_is_available") != null ||
    		idispFlash.invoke1("GetVariable", ASBridge.BRIDGELEVEL_PATH+".Eclipse_ACTF_is_available") != null) {
            setErrorAttribute(ERROR_OK+"Flash DOM detected"); //$NON-NLS-1$
    		return;
    	}

    	String tagName = (String) idispFlash.get("tagName");
    	if (!"OBJECT".equals(tagName)) {
            setErrorAttribute(ERROR_NG+tagName+" tag is not supoported"); //$NON-NLS-1$ //$NON-NLS-2$
            return;
    	}

    	Integer readyState = (Integer) idispFlash.get("ReadyState");
    	if (readyState != null) {
    		if (readyState < 4) {
                setErrorAttribute(ERROR_WAIT+"Flash movie is not ready (ReadyState="+readyState+")"); //$NON-NLS-1$ //$NON-NLS-2$
                return;
    		}
    	}
    	
        setErrorAttribute(ERROR_NA+"Flash DOM is not available"); //$NON-NLS-1$
    }

    private void setErrorAttribute(String message) {
    	idispFlash.put("setAttribute", new Object[]{"aDesignerError", message});
    }
}
