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

package org.eclipse.actf.util.win32;

import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.ole.win32.OLE;



public class COMUtil {

    static {
        try {
            System.loadLibrary("AccessibiltyWin32Library"); //$NON-NLS-1$
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    public static int createDispatch(GUID rclsid) {
        int[] ppv = new int[1];
        int result = COM.CoCreateInstance(rclsid, 0, COM.CLSCTX_INPROC_HANDLER | COM.CLSCTX_INPROC_SERVER
                | COM.CLSCTX_LOCAL_SERVER | COM.CLSCTX_REMOTE_SERVER, COM.IIDIDispatch, ppv);
        if (result != OLE.S_OK)
            OLE.error(OLE.ERROR_CANNOT_CREATE_OBJECT, result);
        return ppv[0];
    }

    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0);
    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0, int arg1);
    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0, int arg1, int arg2);
    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0, int arg1, int arg2, int arg3);
    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0, int arg1, int arg2, int arg3, int arg4);
    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0, int arg1, int arg2, int arg3, int arg4, int arg5);
    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6);
    public static final native int VtblCall(int fnNumber, int ppVtbl, int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7);

    public static GUID IIDFromString(String lpsz) {
        int length = lpsz.length();
        char[] buffer = new char[length + 1];
        lpsz.getChars(0, length, buffer, 0);
        GUID lpiid = new GUID();
        if (COM.IIDFromString(buffer, lpiid) == OLE.S_OK)
            return lpiid;
        return null;
    }
}
