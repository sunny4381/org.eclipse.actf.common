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
package org.eclipse.actf.util.httpproxy.core;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

public class SocketTimeoutRetryInputStream extends FilterInputStream {
    public static int read(InputStream is) throws IOException {
        while (true) {
            try {
                return is.read();
            } catch (SocketTimeoutException e) {
            }
        }
    }

    public static int read(InputStream is, byte[] buf, int offset, int length) throws IOException {
        while (true) {
            try {
                return is.read(buf, offset, length);
            } catch (SocketTimeoutException e) {
            }
        }
    }

    public static int read(InputStream is, byte[] buf) throws IOException {
        while (true) {
            try {
                return is.read(buf);
            } catch (SocketTimeoutException e) {
            }
        }
    }
        
    public int available() throws IOException {
        while (true) {
            try {
                return in.available();
            } catch (SocketTimeoutException e) {
            }
        }
    }

    public void close() throws IOException {
        while (true) {
            try {
                in.close();
                break;
            } catch (SocketTimeoutException e) {
            }
        }
    }

    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public int read() throws IOException {
        return read(in);
    }

    public int read(byte[] buf) throws IOException {
        return read(in, buf);
    }

    public int read(byte[] buf, int offset, int len) throws IOException {
        return read(in, buf, offset, len);
    }

    public void reset() throws IOException {
        while (true) {
            try {
                in.reset();
                break;
            } catch (SocketTimeoutException e) {
            }
        }
    }

    public long skip(long n) throws IOException {
        while (true) {
            try {
                return in.skip(n);
            } catch (SocketTimeoutException e) {
            }
        }
    }

    SocketTimeoutRetryInputStream(InputStream is) {
        super(is);
    }
}
