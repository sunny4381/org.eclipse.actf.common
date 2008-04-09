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

import java.io.IOException;
import java.io.OutputStream;


public final class HeaderInBuffer extends HTTPHeader {
    private HTTPMessageBuffer fBuffer;
    private BufferRange fName;
    private BufferRange fValue;
    private byte[] fReplacedValue;
	
    public HeaderInBuffer() {
        fName = null;
        fValue = null;
        fReplacedValue = null;
    }
	
    public void init(HTTPMessageBuffer buffer, BufferRange name, BufferRange value) {
        fBuffer = buffer;
        fName = name;
        fValue = value;
        fReplacedValue = null;
    }
	
    public void reset() {
        fName = null;
        fValue = null;
        fReplacedValue = null;
    }
	
    public BufferRange getNameAsBufferRange() {
        return fName;
    }
	
    public BufferRange getValueAsBufferRange() {
        return fValue;
    }
	
    public boolean isFieldNameEqualsTo(byte[] name) {
        return fBuffer.equalsIgnoreCase(fName, name);
    }
	
    public boolean isFieldValueEqualsTo(byte[] value) {
        return fBuffer.equals(fValue, value);
    }
	
    public byte[] getName() {
        return fBuffer.getAsBytes(fName);
    }
	
    public byte[] getValue() {
        return fBuffer.getAsBytes(fValue);
    }

    public void setValue(byte[] value) {
        fValue = null;
        fReplacedValue = value;
    }
	
    protected void write(OutputStream out) throws IOException {
        fBuffer.writeTo(out, fName);
        writeHeaderSeparator(out);
        if (fValue != null) {
            fBuffer.writeTo(out, fValue);
        } else if (fReplacedValue != null) {
            out.write(fReplacedValue);
        }
    }
}
