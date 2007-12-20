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



public class NativeStringAccess {
    private int pBSTRAddress = 0;

    private int[] hMem = new int[1];

    public NativeStringAccess() {
        pBSTRAddress = MemoryUtil.GlobalAlloc(4);
    }

    public void dispose() {
        if (0 != hMem[0]) {
            COM.SysFreeString(hMem[0]);
        }
        MemoryUtil.GlobalFree(pBSTRAddress);
    }

    public int getAddress() {
        return pBSTRAddress;
    }

    public String getString() {
        MemoryUtil.MoveMemory(hMem, pBSTRAddress, 4);
        if (0 != hMem[0]) {
            int size = COM.SysStringByteLen(hMem[0]);
            if (size > 0) {
                char[] buffer = new char[(size + 1) / 2];
                MemoryUtil.MoveMemory(buffer, hMem[0], size);
                return new String(buffer);
            }
        }
        return null;//""; //$NON-NLS-1$
    }

    public void setString(String text) {
        MemoryUtil.MoveMemory(hMem, pBSTRAddress, 4);
        if (0 != hMem[0]) {
            COM.SysFreeString(hMem[0]);
        }
        char[] data = (text + "\0").toCharArray(); //$NON-NLS-1$
        int ptr = COM.SysAllocString(data);
        COM.MoveMemory(pBSTRAddress, new int[] { ptr }, 4);
        MemoryUtil.MoveMemory(hMem, pBSTRAddress, 4);
    }
}
