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

/**
 * ComService provides functions to make COM objects and interact with COM
 * objects.
 * 
 * {@link ComService#initialize()} should be called before using COM interface
 * and {@link ComService#uninitialize()} should be called after the end of using
 * COM interface.
 */
public class ComService {
	public static final int CLSCTX_INPROC_SERVER = 0x1;

	public static final int CLSCTX_INPROC_HANDLER = 0x2;

	public static final int CLSCTX_LOCAL_SERVER = 0x4;

	public static final int CLSCTX_REMOTE_SERVER = 0x10;

	public static final int CLSCTX_ALL = 0x17;

	static {
		try {
			System.loadLibrary("JavaCom"); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a wrapped IUnknown object from ptr using resourceManager. The ptr
	 * should refers to an native IUnknown object.
	 * 
	 * @param resourceManager
	 * @param ptr
	 * @param permanent
	 *            the resource is permanent or not
	 * @return new IUnknown object
	 */
	static public IUnknown newIUnknown(ResourceManager resourceManager,
			long ptr, boolean permanent) {
		return IUnknownImpl.newIUnknown(resourceManager, ptr, permanent);
	}

	/**
	 * Create a wrapped IDispatch object from ptr using resourceManager The ptr
	 * should refers to an native IDispatch object.
	 * 
	 * @param resourceManager
	 * @param ptr
	 * @param permanent
	 *            the resource is permanent or not
	 * @return new IDispatch object
	 */
	static public IDispatch newIDispatch(ResourceManager resourceManager,
			long ptr, boolean permanent) {
		return IDispatchImpl.newIDispatch(resourceManager, ptr, permanent);
	}

	/**
	 * Create a wrapped IAccessible object from ptr using resourceManager The
	 * ptr should refers to an native IAccessible object.
	 * 
	 * @param resourceManager
	 * @param ptr
	 * @param permanent
	 *            the resource is permanent or not
	 * @return new IAccessible object
	 */
	static public IAccessible newIAccessible(ResourceManager resourceManager,
			long ptr, boolean permanent) {
		return IAccessibleImpl.newIAccessible(resourceManager, ptr, permanent);
	}

	/**
	 * Change the wrapper of the object from IUnknown to IDispatch
	 * 
	 * @param iunk
	 *            the target IUnknown object
	 * @return the re-wrapped IDispatch object
	 */
	static public IDispatch newIDispatch(IUnknown iunk) {
		if (iunk instanceof IUnknownImpl) {
			IDispatch idisp = new IDispatchImpl((IUnknownImpl) iunk);
			iunk.release();
			return idisp;
		}
		return null;
	}

	/**
	 * Change the wrapper of the object from IUnknown to IAccessible
	 * 
	 * @param iunk
	 *            the target IUnknown object
	 * @return the re-wrapped IAccessible object
	 */
	static public IAccessible newIAccessible(IUnknown iunk) {
		if (iunk instanceof IUnknownImpl) {
			IAccessible iacc = new IAccessibleImpl((IUnknownImpl) iunk);
			iunk.release();
			return iacc;
		}
		return null;
	}

	/**
	 * It should be call before using COM interface.
	 */
	static public void initialize() {
		_initialize();
	}

	/**
	 * It should be call after the end of using COM interface.
	 */
	static public void uninitialize() {
		_uninitialize();
	}

	/**
	 * It calls CoCreateInstance with the rclsid, (pUnkOuter = NULL), the
	 * dwClsContext, and (riid = IID_IUnknown). And it create new wrapped
	 * IUnknown obejct with the ppv;
	 * 
	 * see http://msdn.microsoft.com/en-us/library/ms686615.aspx
	 * 
	 * @param rm
	 * @param rclsid
	 * @param dwClsContext
	 * @return
	 */
	static public IUnknown coCreateInstance(ResourceManager rm, String rclsid,
			int dwClsContext) {
		long ptr = _coCreateInstance(rclsid, dwClsContext);
		return new IUnknownImpl(rm, ptr, false);
	}

	/**
	 * @param clsidFlash
	 * @return
	 */
	public static IDispatch createDispatch(String clsidFlash) {
		IUnknown iunk = coCreateInstance(ResourceManager
				.newResourceManager(null), clsidFlash, CLSCTX_ALL);
		return newIDispatch(iunk);
	}

	static private native void _initialize();

	static private native void _uninitialize();

	static private native long _coCreateInstance(String rclsid, int dwClsContext);

}
