/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.comclutch.impl;

import java.util.UUID;

import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;
import org.eclipse.actf.util.win32.msaa.impl.IAccessibleImpl;

public class IUnknownImpl extends IResourceImpl implements IUnknown {
	/*
	 * private UUID guid; public UUID getGUID() { return guid; }
	 */

	private int internalRefCount;
	private long ptr;

	public long getPtr() {
		return ptr;
	}

	public IUnknown queryInterface(UUID iid) {
		IUnknown iunk = _queryInterface(ptr, iid.getMostSignificantBits(), iid
				.getLeastSignificantBits());
		//System.out.println("QueryI\t"+iunk.getPtr());

		if (iunk instanceof IUnknownImpl) {
			IUnknownImpl iunkImpl = (IUnknownImpl) iunk;
			if (iid.equals(IUnknown.IID_IOleContainer)) {
				IUnknown ioc = new IOleContainerImpl(iunkImpl);
				iunk.release();
				return ioc;
			} else if (iid.equals(IUnknown.IID_IWebBrowser2)) {
				IUnknown idisp = new IDispatchImpl(iunkImpl);
				iunk.release();
				return idisp;
			} else if (iid.equals(IUnknown.IID_IAccessible)) {
				IUnknown iacc = new IAccessibleImpl(iunkImpl);
				iunk.release();
				return iacc;
			} else if (iid.equals(IUnknown.IID_IServiceProvider)) {
				IUnknown isp = new IServiceProviderImpl(iunkImpl);
				iunk.release();
				return isp;
			}
		}
		return iunk;
	}

	@Override
	public void release() {
		//System.out.println("Release\t"+ptr);
		super.release();
		while (internalRefCount > 0) {
			_release(ptr);
			internalRefCount--;
		}
	}

	public int getTotalRefCount() {
		int r = _addRef(ptr);
		_release(ptr);
		return r - 1;
	}

	public IUnknownImpl(ResourceManager rm, long ptr, boolean permanent) {
		super(rm, permanent);
		this.ptr = ptr;
		this.internalRefCount = 1;
	}

	public IUnknownImpl(ResourceManager rm, IUnknown base) {
		this(rm, base.getPtr(), false);
		//System.out.println("new IU\t"+base.getPtr());
		base.addRef(base.getPtr());
	}

	protected IUnknownImpl(IUnknownImpl base) {
		this(base.getResourceManager(), base.ptr, false);
		addRef(this.ptr);
	}

	private native int _release(long ptr);

	private native int _addRef(long ptr);

	private native IUnknown _queryInterface(long ptr, long iidmsb, long iidlsb);

	private static native void _addReleaseWaitQueue(long ptr);

	@Override
	protected void finalize() {
		if (!isPermanent()) {
			while (internalRefCount > 0) {
				_addReleaseWaitQueue(ptr);
				internalRefCount--;
			}
		}
	}

	public static IUnknown newIUnknown(ResourceManager rm, long ptr,
			boolean permanent) {
		IUnknown iunk = new IUnknownImpl(rm, ptr, permanent);
		rm.addResource(iunk);
		return iunk;
	}

	public IUnknown newIUnknown(long ptr) {
		IUnknown iunk = (IUnknown) findInResource(ptr);
		if (iunk != null) {
			if (iunk instanceof IUnknownImpl) {
				((IUnknownImpl) iunk).internalRefCount++;
			}
		} else {
			iunk = new IUnknownImpl(getResourceManager(), ptr, false);
			addResource(iunk);
		}
		return iunk;
	}

	public void addRef(long ptr) {
		//System.out.println("AddRef\t"+ptr);
		_addRef(ptr);
	}
}
