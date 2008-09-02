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

import org.eclipse.actf.util.win32.comclutch.impl.IDispatchImpl;
import org.eclipse.actf.util.win32.comclutch.impl.IUnknownImpl;

public abstract class Handler extends IUnknownImpl implements IDispatch {
    public abstract Object defaultHandler(Object[] args);

    protected Handler(ResourceManager rm) {
        super(rm, _createHandler(), false);
        _setObject(getPtr());
    }

    private static native long _createHandler();
    private native void _setObject(long ptr);

    public Object get(String prop) {
        return null;
    }

    public Object invoke(String method, Object[] args) {
        return defaultHandler(args);
    }

    public Object invoke0(String method) {
        return defaultHandler(null);
    }

    public Object invoke1(String method, Object arg1) {
        return defaultHandler(new Object[] { arg1 });
    }

    public void put(String prop, Object val) {
    }

    @Override
    public IUnknown newIUnknown(long ptr) {
        // Create a transient object.  Thus, don't manage it with ResourceManager.
        return new IUnknownImpl(null, ptr, true);
    }

    public IDispatch newIDispatch(long ptr) {
        // Create a transient object.  Thus, don't manage it with ResourceManager.
        return new IDispatchImpl(null, ptr, true);
    }
}
