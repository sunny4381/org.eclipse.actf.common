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

import org.eclipse.actf.util.win32.keyhook.ISendEvent;



public class SendEventImpl implements ISendEvent {
    static {
        try {
            System.loadLibrary("KeyHook");
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private static final SendEventImpl instance = new SendEventImpl();
    public static ISendEvent getSendEvent() {
        return instance;
    }

    public boolean postKey(int vkey, boolean isUp) {
        return _postKeyMessage(0, vkey, isUp);
    }

    public boolean postMouse(int x, int y, boolean isUp) {
        return _postMouseMessage(0, 0, x, y, isUp, 0);
    }

    public boolean postMouseToWindow(long hwnd, int x, int y, boolean isUp) {
        return _postMouseMessage(hwnd, 0, x, y, isUp, 0);
    }

    public boolean focusWindow(long hwnd) {
        return _focusWindow(hwnd);
    }

    public boolean postKeyToWindow(long hwnd, int vkey, boolean isUp) {
        return _postKeyMessage(hwnd, vkey, isUp);
    }
    
    public long findWindow(String className, String windowName) {
        return _findWindow(className, windowName);
    }

    private native boolean _postKeyMessage(long hwnd, int vkey, boolean isUp);
    private native boolean _postMouseMessage(long hwnd, int button, int x, int y, boolean isUp, int flags);
    private native boolean _focusWindow(long hwnd);
    private native long _findWindow(String className, String windowName);

    private SendEventImpl() {
    }
}
