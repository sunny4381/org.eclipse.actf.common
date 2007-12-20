/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.win32;

import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.IDispatch;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.Variant;



public class NativeVariantAccess {
    private int size;

    private int pVariantAddress;

    public NativeVariantAccess() {
        this(1);
    }

    public NativeVariantAccess(int size) {
        this.size = size;
        this.pVariantAddress = MemoryUtil.GlobalAlloc(Variant.sizeof * size);
        for (int i = 0; i < size; i++) {
            COM.VariantInit(getAddress(i));
        }
    }

    public void dispose() {
        for (int i = 0; i < size; i++) {
            COM.VariantClear(getAddress(i));
        }
        MemoryUtil.GlobalFree(pVariantAddress);
    }

    public int getAddress() {
        return getAddress(0);
    }

    public int getAddress(int index) {
        return pVariantAddress + Variant.sizeof * index;
    }

    public short getType() {
        return getType(0);
    }

    public short getType(int index) {
        short[] pType = new short[1];
        MemoryUtil.MoveMemory(pType, getAddress(index), 2);
        return pType[0];
    }

    public int getInt() {
        return getInt(0);
    }

    public int getInt(int index) {
        if (OLE.VT_I4 == getType(index)) {
            int[] pInt = new int[1];
            MemoryUtil.MoveMemory(pInt, getAddress(index) + 8, 4);
            return pInt[0];
        }
        return -1;
    }

    public IDispatch getDispatch() {
        return getDispatch(0);
    }

    public IDispatch getDispatch(int index) {
        if (OLE.VT_DISPATCH == getType(index)) {
            int[] pInt = new int[1];
            MemoryUtil.MoveMemory(pInt, getAddress(index) + 8, 4);
            return new IDispatch(pInt[0]);
        }
        return null;
    }

    public String getString() {
        return getString(0);
    }

    public String getString(int index) {
        if (OLE.VT_BSTR == getType(index)) {
            int[] hMem = new int[1];
            MemoryUtil.MoveMemory(hMem, getAddress(index) + 8, 4);
            if (0 != hMem[0]) {
                int size = COM.SysStringByteLen(hMem[0]);
                if (size > 0) {
                    char[] buffer = new char[(size + 1) / 2];
                    MemoryUtil.MoveMemory(buffer, hMem[0], size);
                    return new String(buffer);
                }
            }
        }
        return null;
    }
    
    public Variant getVariant() {
    	return getVariant(0);
    }
    
    public Variant getVariant(int index) {
        switch( getType(index) ) {
            case OLE.VT_I4:
                return new Variant(getInt(index));
            case OLE.VT_BSTR:
                return new Variant(getString(index));
            case OLE.VT_EMPTY:
                return new Variant();
        }
        return null;
    }

}
