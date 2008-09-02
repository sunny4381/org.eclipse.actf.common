/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.comclutch;

import org.eclipse.actf.util.win32.comclutch.impl.IDispatchImpl;
import org.eclipse.actf.util.win32.comclutch.impl.IUnknownImpl;
import org.eclipse.actf.util.win32.msaa.IAccessible;
import org.eclipse.actf.util.win32.msaa.impl.IAccessibleImpl;

public class ComService {
	public static final int CLSCTX_INPROC_SERVER = 0x1;

	public static final int CLSCTX_INPROC_HANDLER = 0x2;

	public static final int CLSCTX_LOCAL_SERVER = 0x4;
	
	public static final int CLSCTX_REMOTE_SERVER = 0x10;

	public static final int CLSCTX_ALL= 0x17;

    static {
        try {
            System.loadLibrary("JavaCom");
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    static public IUnknown newIUnknown(ResourceManager resourceManager, long ptr, boolean permanent) {
        return IUnknownImpl.newIUnknown(resourceManager, ptr, permanent);
    }

    static public IDispatch newIDispatch(ResourceManager resourceManager, long ptr, boolean permanent) {
        return IDispatchImpl.newIDispatch(resourceManager, ptr, permanent);
    }

    static public IAccessible newIAccessible(ResourceManager resourceManager, long ptr, boolean permanent) {
        return IAccessibleImpl.newIAccessible(resourceManager, ptr, permanent);
    }
    
    static public IDispatch newIDispatch(IUnknown iunk) {
    	if (iunk instanceof IUnknownImpl) {
    		IDispatch idisp = new IDispatchImpl((IUnknownImpl) iunk);
    		iunk.release();
        	return idisp;
    	}
    	return null;
	}

    static public IAccessible newIAccessible(IUnknown iunk) {
    	if (iunk instanceof IUnknownImpl) {
    		IAccessible iacc = new IAccessibleImpl((IUnknownImpl) iunk);
    		iunk.release();
        	return iacc;
    	}
    	return null;
	}

    static public void initialize() {
        _initialize();
    }
    static public void uninitialize() {
        _uninitialize();
    }
    
    static public IUnknown coCreateInstance(ResourceManager rm, String rclsid, int dwClsContext){
        long ptr = _coCreateInstance(rclsid, dwClsContext);
        return new IUnknownImpl(rm, ptr, false);
    }

	public static IDispatch createDispatch(String clsidFlash) {
		IUnknown iunk = coCreateInstance(ResourceManager.newResourceManager(null), clsidFlash, CLSCTX_ALL);
		return newIDispatch(iunk);
	}
    
    static private native void _initialize();
    static private native void _uninitialize();
    static private native long _coCreateInstance(String rclsid, int dwClsContext);

}
