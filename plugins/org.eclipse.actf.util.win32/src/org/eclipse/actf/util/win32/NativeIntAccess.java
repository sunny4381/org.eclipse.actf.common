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



public class NativeIntAccess {
    private int pIntAddress = 0;

    public NativeIntAccess() {
        this(1);
    }
    
    public NativeIntAccess(int size) {
        pIntAddress = MemoryUtil.GlobalAlloc(4 * size);
    }

    public void dispose() {
        MemoryUtil.GlobalFree(pIntAddress);
    }

    public int getAddress() {
        return getAddress(0);
    }
    
    public int getAddress(int index) {
        return pIntAddress + index * 4;
    }

    public int getInt() {
        return getInt(0);
    }
    
    public int getInt(int index) {
        int[] pInt = new int[1];
        MemoryUtil.MoveMemory(pInt, getAddress(index), 4);
        return pInt[0];
    }

    public void setInt(int value) {
        setInt(0,value);
    }
    
    public void setInt(int index, int value) {
        int[] pInt = new int[1];
        pInt[0] = value;
        MemoryUtil.MoveMemory(getAddress(index), pInt, 4);
    }
}
