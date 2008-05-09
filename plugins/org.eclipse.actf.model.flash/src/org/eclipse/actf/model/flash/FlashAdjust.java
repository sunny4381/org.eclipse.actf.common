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
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;



public class FlashAdjust {

    public static final String ERROR_OK =   "OK: "; //$NON-NLS-1$
    public static final String ERROR_NG =   "NG: "; //$NON-NLS-1$
    public static final String ERROR_NA =   "NA: "; //$NON-NLS-1$
    public static final String ERROR_WAIT = "WAIT: "; //$NON-NLS-1$
    
    private Variant varFlash = null;
    private OleAutomation flashAutomation = null;
    
    public FlashAdjust(Object flashObject/*, int validate*/) {
    	IDispatch idisp = HTMLElementUtil.getHtmlElementFromObject(flashObject);
    	varFlash = new Variant(new org.eclipse.swt.internal.ole.win32.IDispatch((int) idisp.getPtr()));
        if( null != varFlash && OLE.VT_DISPATCH==varFlash.getType() ) {
            flashAutomation = varFlash.getAutomation();
        }
    }
    
    public void dispose() {
        if( null!=varFlash ) varFlash.dispose();
    }
    
    public void adjust(String newId) {
        if( null==flashAutomation ) return;

        int[] idGetVariable = flashAutomation.getIDsOfNames(new String[]{"GetVariable"}); //$NON-NLS-1$
        if( null != flashAutomation.invoke(idGetVariable[0],new Variant[]{new Variant(ASBridge.ROOTLEVEL_PATH+".Eclipse_ACTF_is_available")}) || //$NON-NLS-1$
            null != flashAutomation.invoke(idGetVariable[0],new Variant[]{new Variant(ASBridge.BRIDGELEVEL_PATH+".Eclipse_ACTF_is_available")})) { //$NON-NLS-1$
            setErrorAttribute(ERROR_OK+"Flash DOM detected"); //$NON-NLS-1$
            return;
        }
        int[] idTagName = flashAutomation.getIDsOfNames(new String[]{"tagName"}); //$NON-NLS-1$
        if( null != idTagName ) {
            Variant varTagName = flashAutomation.getProperty(idTagName[0]);
            if( null!=varTagName && OLE.VT_BSTR==varTagName.getType() ) {
                String strTagName = varTagName.getString();
                if( !"OBJECT".equalsIgnoreCase(strTagName) ) { //$NON-NLS-1$
                    setErrorAttribute(ERROR_NG+strTagName+" tag is not supoported"); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }
            }
        }
        int[] idReadyState = flashAutomation.getIDsOfNames(new String[]{"ReadyState"}); //$NON-NLS-1$
        if( null!=idReadyState ) {
            Variant varReadyState = flashAutomation.getProperty(idReadyState[0]);
            if( null!=varReadyState && OLE.VT_I4==varReadyState.getType() ) {
                int readyState = varReadyState.getInt(); 
                if( readyState < 4 ) {
                    setErrorAttribute(ERROR_WAIT+"Flash movie is not ready (ReadyState="+readyState+")"); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }
            }
        }
        setErrorAttribute(ERROR_NA+"Flash DOM is not available"); //$NON-NLS-1$
    }

    private void setErrorAttribute(String message) {
        int[] idSetAttribute = flashAutomation.getIDsOfNames(new String[]{"setAttribute"}); //$NON-NLS-1$
        if( null != idSetAttribute ) {
            flashAutomation.invoke(idSetAttribute[0],new Variant[]{new Variant("aDesignerError"),new Variant(message)}); //$NON-NLS-1$
        }
    }
}
