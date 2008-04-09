/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core.impl;

public class ByteBuffer {
    private int fIdx;
    private byte[] fBuffer;
        
    public ByteBuffer() {
        fIdx = 0;
        fBuffer = null;
    }
        
    public void init(int bufferSize) {
        fIdx = 0;
        fBuffer = new byte[bufferSize];
    }
        
    public final void add(byte data) {
        if (fIdx >= fBuffer.length) {
            byte[] newBuffer = new byte[fBuffer.length * 2];
            System.arraycopy(fBuffer, 0, newBuffer, 0, fBuffer.length);
            fBuffer = newBuffer;
        }
        fBuffer[fIdx++] = data;
    }
        
    public byte[] close() {
        if (fIdx <= 0) {
            return null;
        }
        byte[] buf = new byte[fIdx];
        System.arraycopy(fBuffer, 0, buf, 0, fIdx);
        return buf;
    }
}
