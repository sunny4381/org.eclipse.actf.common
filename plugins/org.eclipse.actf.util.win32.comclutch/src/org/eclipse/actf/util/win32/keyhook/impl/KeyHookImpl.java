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

package org.eclipse.actf.util.win32.keyhook.impl;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.actf.util.win32.keyhook.IKeyHook;
import org.eclipse.actf.util.win32.keyhook.IKeyHookListener;




public class KeyHookImpl implements IKeyHook {
    static {
        try {
            System.loadLibrary("KeyHook");
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    private static boolean initialized = false;
    private static HashSet<KeyHookImpl> hooks = new HashSet<KeyHookImpl>();

    private final IKeyHookListener keyHookListener;
    
    private static void register(KeyHookImpl inst) {
        hooks.add(inst);
    }

    synchronized public static boolean hookedKeyEntry(int idx, int vkey, int modifier, boolean isUp) {
        Iterator<KeyHookImpl> it = hooks.iterator();
        boolean filtered = false;
        while (it.hasNext()) {
            KeyHookImpl inst = it.next();
            filtered =  inst.keyHookListener.hookedKey(vkey, modifier, isUp) || filtered;
        }
        return filtered;
    }
    
    synchronized public void registerHookedKey(int vkey, int modifier) {
        filterKey(0, vkey, modifier);
    }

    synchronized public void hookAll(boolean flag) {
        filterAllKey(flag);
    }

    public KeyHookImpl(IKeyHookListener keyHookListener, long hwnd) {
        if (!initialized) {
            initialize(hwnd);
            initialized = true;
        }
        register(this);
        this.keyHookListener = keyHookListener;
    }

    private native void initialize(long hwnd);
    private native void filterKey(int idx, int vkey, int modifier);
    private native void filterAllKey(boolean flag);

    synchronized public void dispose() {
        hooks.remove(this);
    }

}


