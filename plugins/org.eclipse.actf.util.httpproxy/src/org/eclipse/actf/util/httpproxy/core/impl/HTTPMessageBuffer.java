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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class HTTPMessageBuffer extends ByteArrayOutputStream {
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 1024;
        
    public HTTPMessageBuffer() {
        super(DEFAULT_INITIAL_BUFFER_SIZE);
    }

    public HTTPMessageBuffer(int initBufferSize) {
        super(initBufferSize);
    }

    protected HTTPMessageBuffer(byte[] body) {
        super.buf = body;
        super.count = body.length;
    }

    public byte[] getByteArray() {
        return super.buf;
    }
        
    public final int getLength() {
        return super.count;
    }

    public String getAsString(BufferRange range) {
        return new String(super.buf, range.getStart(), range.getLength());
    }

    public byte[] getAsBytes(BufferRange range) {
        int len = range.getLength();
        if (len <= 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[len];
        System.arraycopy(super.buf, range.getStart(), bytes, 0, len);
        return bytes;
    }
        
    public final void writeTo(OutputStream out, BufferRange range) throws IOException {
        out.write(super.buf, range.getStart(), range.getLength());
    }

    public boolean equals(BufferRange range, byte[] data) {
        int len = data.length;
        if (len != range.getLength()) {
            return false;
        }
        int offset = range.getStart();
        byte[] buffer = super.buf;
        for (int i = len - 1; i >= 0; i--) {
            if (data[i] != buffer[offset + i]) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsIgnoreCase(BufferRange range, byte[] data) {
        int len = data.length;
        if (len != range.getLength()) {
            return false;
        }
        int offset = range.getStart();
        byte[] buffer = super.buf;
        for (int i = len - 1; i >= 0; i--) {
            if (HTTPHeader.toUpcase(data[i])
                != HTTPHeader.toUpcase(buffer[offset + i])) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return new String(super.buf, 0, super.count);
    }
}
