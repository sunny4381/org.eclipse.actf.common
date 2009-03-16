/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Daisuke SATO
 *******************************************************************************/
package org.eclipse.actf.util.win32.comclutch.impl;

import java.util.HashMap;

import org.eclipse.actf.util.win32.comclutch.DispatchException;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.eclipse.actf.util.win32.comclutch.IResource;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;

public class IDispatchImpl extends IUnknownImpl implements IDispatch {
	private static final String IS_NOT_AVAILABLE = " is not available."; //$NON-NLS-1$

	private static final int LCID_SYSTEM_DEFAULT = 0x400;
	// private static final int LCID_USER_DEFAULT = 0x800;

	private static final int DISPATCH_METHOD = 1;
	private static final int DISPATCH_PROPERTYGET = 2;
	// private static final int DISPATCH_PROPERTYPUT = 4;
	// private static final int DISPATCH_PROPERTYPUTREF = 8;

	private HashMap<String, Long> nameCache;

	private long getDispID(String name) {
		if (nameCache == null) {
			nameCache = new HashMap<String, Long>(4);
		} else {
			Long ll = nameCache.get(name);
			if (ll != null)
				return ll.longValue();
		}
		long[] idl = _getIDsOfNames(getPtr(), new String[] { name },
				LCID_SYSTEM_DEFAULT);
		if (idl == null)
			return -1;
		long dispid = idl[0];
		nameCache.put(name.intern(), dispid);
		return dispid;
	}

	public void cacheDispIDs(String[] names) {
		if (nameCache == null) {
			nameCache = new HashMap<String, Long>(names.length);
		}
		long[] idl = _getIDsOfNames(getPtr(), names, LCID_SYSTEM_DEFAULT);
		for (int i = 0; i < idl.length; i++) {
			nameCache.put(names[i].intern(), idl[i]);
		}
	}

	public Object invoke(String method, Object[] args) {
		// System.err.println(method + " is invoked." + args);
		long id = getDispID(method);
		if (id == -1)
			throw new DispatchException(method + IS_NOT_AVAILABLE);
		return invoke(id, args);
	}

	public Object invoke(long id, Object[] args) {
		Object obj = _invoke(getPtr(), id, LCID_SYSTEM_DEFAULT,
				DISPATCH_METHOD, args);
		// System.err.println("->" + obj);
		return obj;
	}

	public Object invoke0(String method) {
		// System.err.println(method + " is invoked to " + this);
		long id = getDispID(method);
		if (id == -1)
			throw new DispatchException(method + IS_NOT_AVAILABLE);
		return invoke0(id);
	}

	public Object invoke0(long id) {
		return invoke(id, null);
	}

	public Object invoke1(String method, Object arg1) {
		// System.err.println(method + " is invoked to " + this + " with "+
		// arg1);
		long id = getDispID(method);
		if (id == -1)
			throw new DispatchException(method + IS_NOT_AVAILABLE);
		return invoke1(id, arg1);
	}

	public Object invoke1(long id, Object arg1) {
		return invoke(id, new Object[] { arg1 });
	}

	public Object get(String prop) {
		// System.err.println(prop + " is accessed for " + this);
		long id = getDispID(prop);
		if (id == -1)
			return null;
		return get(id);
	}

	public Object get(long id) {
		Object obj = _invoke(getPtr(), id, LCID_SYSTEM_DEFAULT,
				DISPATCH_PROPERTYGET, null);
		// System.err.println("->" + obj);
		return obj;
	}

	public Object get(String prop, Object[] args) {
		// System.err.print(prop + " is accessed for " + this + " with ");
		// for(int i=0; i<args.length; i++){
		// if(i != 0)
		// System.err.print(", ");
		// System.err.print(args[i]);
		// }
		// System.err.println();
		long id = getDispID(prop);
		if (id == -1)
			return null;
		return get(id, args);
	}

	public Object get(long id, Object[] args) {
		Object obj = _invoke(getPtr(), id, LCID_SYSTEM_DEFAULT,
				DISPATCH_PROPERTYGET, args);
		// System.err.println("->" + obj);
		return obj;
	}

	public void put(String prop, Object val) {
		long id = getDispID(prop);
		if (id == -1)
			throw new DispatchException(prop + IS_NOT_AVAILABLE);
		put(id, val);
	}

	public void put(long id, Object val) {
		_put(getPtr(), id, LCID_SYSTEM_DEFAULT, val);
	}

	public IDispatchImpl(ResourceManager rm, long ptr, boolean permanent) {
		super(rm, ptr, permanent);
	}

	public IDispatchImpl(ResourceManager rm, IUnknown base) {
		super(rm, base);
	}

	public IDispatchImpl(IUnknownImpl base) {
		super(base);
	}

	private native long[] _getIDsOfNames(long ptr, String[] names, int lcid);

	private native Object _invoke(long ptr, long dispid, int lcid, int flag,
			Object[] args);

	private native void _put(long ptr, long dispid, int lcid, Object val);

	public static IDispatch newIDispatch(ResourceManager rm, long ptr,
			boolean permanent) {
		IDispatch idisp = new IDispatchImpl(rm, ptr, permanent);
		rm.addResource(idisp);
		return idisp;
	}

	public IDispatch newIDispatch(long ptr) {
		// System.err.println("NN:" + ptr);
		IResource r = findInResource(ptr);
		if (r != null) {
			IDispatch x = (IDispatch) r;
			// System.err.println(r + "/RefCount:" + x.addRef());
			// x.release();
			return x;
		}
		IDispatch idisp = new IDispatchImpl(getResourceManager(), ptr, false);
		addResource(idisp);
		return idisp;
	}
}
