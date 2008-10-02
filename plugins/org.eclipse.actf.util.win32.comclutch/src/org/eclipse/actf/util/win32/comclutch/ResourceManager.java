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
package org.eclipse.actf.util.win32.comclutch;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * ResourceManager treats the life cycle of the native resources. All resources
 * are wrapped by Java object, so the resources can be managed by the garbage
 * collection system.
 */
public class ResourceManager {
	private final ResourceManager parent;
	private ArrayList<ResourceManager> childRMs;

	private static final int HASHSIZE = (1 << 13) - 1; // Mersenne 5.
	private WeakReference<IResource> resourceHashArray[] = new WeakReference[HASHSIZE];

	/**
	 * @param ptr
	 *            the reference pointer to be searched
	 * @return the instance of IResource having ptr. It returns null if resource
	 *         is not found.
	 */
	public IResource findInResource(long ptr) {
		int idx = (int) ptr % HASHSIZE;
		WeakReference<IResource> wr = resourceHashArray[idx];
		if (wr == null)
			return null;
		IResource r = wr.get();
		if (r == null)
			return null;
		if (r.getPtr() == ptr)
			return r;
		return null;
	}

	/**
	 * All resources will be released.
	 * 
	 * @param except
	 *            the resource to be excluded from the clearance
	 */
	public void releaseAll(IResource except) {
		int len = resourceHashArray.length;
		for (int i = 0; i < len; i++) {
			WeakReference<IResource> wr = resourceHashArray[i];
			if (wr == null)
				continue;
			resourceHashArray[i] = null;
			IResource r = wr.get();
			if (r != null) {
				r.release();
			}
		}
		addResource(except);

		if (childRMs != null) {
			Iterator<ResourceManager> itm = childRMs.iterator();
			while (itm.hasNext()) {
				itm.next().releaseAll(except);
			}
		}
	}

	/**
	 * @param resource
	 *            the resource to be removed from the manager
	 */
	public void removeResource(IResource resource) {
		long ptr = resource.getPtr();
		int idx = (int) ptr % HASHSIZE;
		WeakReference<IResource> wr = resourceHashArray[idx];
		if (wr == null)
			return;
		IResource r = wr.get();
		if (r == null) {
			resourceHashArray[idx] = null;
			return;
		}
		if (r.getPtr() != ptr)
			return;
		resourceHashArray[idx] = null;
		wr.clear();
		return;
	}

	/**
	 * @param target
	 *            the resource to be added to the manager
	 */
	public void addResource(IResource target) {
		long ptr = target.getPtr();
		int idx = (int) ptr % HASHSIZE;
		WeakReference<IResource> wr = new WeakReference<IResource>(target);
		resourceHashArray[idx] = wr;
		return;
	}

	/**
	 * @param parent
	 *            the parent resource manager
	 * @return the new instance of ResourceManager has the parent
	 */
	public static ResourceManager newResourceManager(ResourceManager parent) {
		ResourceManager rm = new ResourceManager(parent);
		if (parent != null) {
			if (parent.childRMs == null) {
				parent.childRMs = new ArrayList<ResourceManager>(1);
			}
			parent.childRMs.add(rm);
		}
		return rm;
	}

	private ResourceManager(ResourceManager parent) {
		this.parent = parent;
	}
}
