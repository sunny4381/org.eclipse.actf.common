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
package org.eclipse.actf.util.internal.httpproxy.core;

public class BufferRange {
    private int fStart;
    private int fLength;
        
    public BufferRange() {
    }
        
    public void reset() {
        fStart = 0;
        fLength = 0;
    }
        
    public void setStart(int start) {
        fStart = start;
    }
        
    public void setLength(int length) {
        fLength = length;
    }
        
    public int getStart() {
        return fStart;
    }
        
    public int getLength() {
        return fLength;
    }
        
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[').append(Integer.toString(fStart));
        sb.append(',').append(Integer.toString(fLength));
        sb.append(']');
        return sb.toString();
    }
}
