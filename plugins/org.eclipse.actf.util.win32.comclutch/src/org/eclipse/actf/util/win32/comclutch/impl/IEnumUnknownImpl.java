/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.win32.comclutch.impl;

import org.eclipse.actf.util.win32.comclutch.IEnumUnknown;
import org.eclipse.actf.util.win32.comclutch.IResource;
import org.eclipse.actf.util.win32.comclutch.IUnknown;
import org.eclipse.actf.util.win32.comclutch.ResourceManager;



public class IEnumUnknownImpl extends IUnknownImpl implements IEnumUnknown {

    public IEnumUnknownImpl(ResourceManager rm, long ptr, boolean permanent) {
        super(rm, ptr, permanent);
    }

    public IEnumUnknownImpl(IUnknownImpl iunk) {
        this(iunk.getResourceManager(), iunk.getPtr(), false);
    }

    public IUnknown[] next(int num) {
        return _next(getPtr(), num);
    }

    public boolean reset() {
        return _reset(getPtr());
    }

    public boolean skip(int num) {
        return _skip(getPtr(), num);
    }

    public IEnumUnknown _clone() {
        IUnknown iunk = __clone(getPtr());
        return newIEnumUnknown(iunk.getPtr());
    }
    
    private native IUnknown[] _next(long ptr, int num); 
    private native boolean _reset(long ptr);
    private native boolean _skip(long ptr, int num);
    private native IUnknown __clone(long ptr);

    public IEnumUnknown newIEnumUnknown(long ptr) {
        IResource r = findInResource(ptr);
        if (r != null) {
            IEnumUnknown x = (IEnumUnknown) r;
            return x;
        }
        IEnumUnknown ieu = new IEnumUnknownImpl(getResourceManager(), ptr, false);
        addResource(ieu);
        return ieu;
    }
}
