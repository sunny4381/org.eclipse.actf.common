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
package org.eclipse.actf.util.internal.httpproxy.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

public class SocketTimeoutRetryOutputStream extends OutputStream {
    public static void write(OutputStream os, byte[] buf) throws IOException {
        while (true) {
            try {
                os.write(buf);
                break;
            } catch(SocketTimeoutException e) {
            }
        }
    }

    public static void write(OutputStream os, byte[] buf, int offset, int len)
        throws IOException {
        while (true) {
            try {
                os.write(buf, offset, len);
                break;
            } catch(SocketTimeoutException e) {
            }
        }
    }

    public static void write(OutputStream os, int b) throws IOException {
        while (true) {
            try {
                os.write(b);
                break;
            } catch(SocketTimeoutException e) {
            }
        }
    }

    public void close() throws IOException {
        while (true) {
            try {
                outputStream.close();
                break;
            } catch(SocketTimeoutException e) {
            }
        }
    }

    public void flush() throws IOException {
        while (true) {
            try {
                outputStream.flush();
                break;
            } catch(SocketTimeoutException e) {
            }
        }
    }

    public void write(byte[] buf) throws IOException {
        write(outputStream, buf);
    }
    
    public void write(byte[] buf, int offset, int len) throws IOException {
        write(outputStream, buf, offset, len);
    }

    public void write(int b) throws IOException {
        write(outputStream, b);
    }

    private final OutputStream outputStream;
 
    SocketTimeoutRetryOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
