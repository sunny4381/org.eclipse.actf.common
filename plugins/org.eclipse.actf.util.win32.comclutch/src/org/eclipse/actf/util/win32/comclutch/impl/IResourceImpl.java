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

import org.eclipse.actf.util.win32.comclutch.IResource;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;

public abstract class IResourceImpl implements IResource {
    private ResourceManager resourceManager;
    private final boolean permanent;

    public boolean isPermanent() {
        return permanent;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void addResource(IResource target) {
        if (resourceManager != null) {
            resourceManager.addResource(target);
        }
    }

    public void release() {
        if (resourceManager != null) {
            resourceManager.removeResource(this);
        }
    }

    public IResource findInResource(long ptr) {
        if (resourceManager != null) {
            return resourceManager.findInResource(ptr);
        }
        return null;
    }

    @Override
    public int hashCode() {
        long x = getPtr();
        return (int) ((x & 0xFFFFFFFF) ^ (x >> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IResource)) return false;
        IResource r = (IResource) o;
        return (getPtr() == r.getPtr());
    }

    @Override
    public String toString() {
        return super.toString() + " PTR:" + getPtr();
    }

    public IResourceImpl(ResourceManager rm, boolean permanent) {
        this.resourceManager = rm;
        this.permanent = permanent;
    }

    

}

