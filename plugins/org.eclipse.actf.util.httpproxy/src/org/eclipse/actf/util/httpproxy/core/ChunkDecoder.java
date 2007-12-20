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
package org.eclipse.actf.util.httpproxy.core;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public class ChunkDecoder extends FilterInputStream {
    static final Logger LOGGER = Logger.getLogger(ChunkDecoder.class);

    public static final char CR = 0x0d;
    public static final char LF = 0x0a;
    public static final char SP = 0x20;
    public static final char HT = 0x09;
    public static final int EOF = -1;

    private int fChunkSize = 0;
    private int fNumChunk = 0;
    private int fReadBytes = 0;
    private long fLastReadTime = 0;
    private ChunkListener fChunkListener = null;
//    private OutputStream fReplica = null;

    public ChunkDecoder(InputStream in) {
        super(in);
    }

    public void setChunkListener(ChunkListener l) {
        if (fChunkListener != null && l != null) {
            throw new IllegalStateException("ChunkListener is already set: " + fChunkListener);
        }
        fChunkListener = l;
    }
    
    public int getChunkSize() {
        if (fReadBytes == 0) {
            throw new IllegalStateException("ChunkSize is unknown");
        }
        return fChunkSize;
    }
    
//    public ChunkDecoder(InputStream in, OutputStream replica) {
//        super(in);
//        fReplica = replica;
//    }
    
    private int getAvailableInput(long timeout) throws IOException, TimeoutException {
        int data;
        while (true) {
            try {
                data = in.read();
//                if (data >= 0 && fReplica != null) {
//                    fReplica.write(data);
//                }
                break;
            } catch (SocketTimeoutException e) {
                if (timeout > 0) {
                    if (fLastReadTime == 0) {
                        fLastReadTime = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - fLastReadTime > timeout) {
                        fLastReadTime = System.currentTimeMillis();
                        throw new TimeoutException("HTTPReader.getAvailableInput");
                    }
                }
            }
        }
        fLastReadTime = System.currentTimeMillis();
        return data;
/*
        if (timeout > 0) {
            int available;
            while ((available = in.available()) <= 0) {
                if (available < 0) {
                    return EOF;
                }
                // available == 0
                if (fLastReadTime == 0) {
                    fLastReadTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - fLastReadTime > timeout) {
                        fLastReadTime = System.currentTimeMillis();
                        throw new TimeoutException("HTTPReader.getAvailableInput");
                    } else {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            }
        }
        data = in.read();
        fLastReadTime = System.currentTimeMillis();
        return data;
*/
    }
    
    private int getAvailableInput(long timeout, byte b[], int off, int len) throws IOException, TimeoutException {
        if (len <= 0) {
            return 0;
        }
        int available;
        int nread = 0;
        while ((available = in.available()) <= 0) {
            if (available < 0) {
                return EOF;
            }
            // available == 0
            try {
                int data = in.read();
                b[off++] = (byte) data;
                nread = 1;
                len -= 1;
                available = in.available();
                break;
            } catch (SocketTimeoutException e) {
                if (timeout > 0) {
                    if (fLastReadTime == 0) {
                        fLastReadTime = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - fLastReadTime > timeout) {
                        fLastReadTime = System.currentTimeMillis();
                        throw new TimeoutException();
                    }
                }
            }
        }
        if (available == 0) {
            return nread;
        }
        if (len > available) {
            len = available;
        }
        int read = in.read(b, off, len);
        fLastReadTime = System.currentTimeMillis();
        return read + nread;
    }

    private String readChunkSizeLine(long timeout) throws IOException, TimeoutException {
        int data = getAvailableInput(timeout);
        if (data == EOF) {
            return null;
        }
        boolean cr = false;
        boolean crlf = false;
        StringBuffer token = new StringBuffer();
        while (data != EOF) {
            switch (data) {
            case CR:
                cr = true;
                break;
            case LF:
                if (cr) {
                    crlf = true;
                    break;
                }
            case ';':
            case SP:
            case HT:
                cr = false;
                break;
            default:
                cr = false;
                token.append((char) data);
            }
            if (crlf) {
                if (token.length() > 0) {
                    break;
                }
                cr = false;
                crlf = false;
            }
            data = getAvailableInput(timeout);
        }
        return token.toString();
    }
    
    // 1*(HEX) CR LF
    private void readChunkSize(long timeout) throws IOException, TimeoutException {
        String token = readChunkSizeLine(timeout);
        fNumChunk += 1;
        //String token = readNextToken(timeout, out);
        if (token == null || token.length() == 0) {
            fChunkSize = EOF;
        } else {
            token = token.trim();
            try {
                fChunkSize = Integer.parseInt(token, 16); // Parse a HEX number
                if (fChunkListener != null) {
                    fChunkListener.newChankRead(fNumChunk, fChunkSize);
                }
            } catch (NumberFormatException e) {
                fChunkSize = -1;
                throw new IOException("Invalid chunk size line: '" + token + "'");
            }
        }
    }

    public int read(long timeout) throws IOException, TimeoutException {
        if (fReadBytes == fChunkSize) {
            fReadBytes = 0;
            readChunkSize(timeout);
        }
        if (fChunkSize == EOF || fChunkSize == 0) {
            fChunkSize = EOF;
            return EOF;
        }
        int data = getAvailableInput(timeout);
        fReadBytes += 1;
        return data;
    }
    
    public int read() throws IOException {
        try {
            return read(0);
        } catch (TimeoutException e) {
            throw new RuntimeException("Impossible exception");
        }
    }

    public int read(long timeout, byte b[], int off, int len) throws IOException, TimeoutException {
        if (fReadBytes == fChunkSize) {
            fReadBytes = 0;
            readChunkSize(timeout);
        }
        if (fChunkSize == EOF || fChunkSize == 0) {
            fChunkSize = EOF;
            return EOF;
        }
        int max = fChunkSize - fReadBytes;
        if (len > max) {
            len = max;
        }
        int read = getAvailableInput(timeout, b, off, len);
        fReadBytes += read;
        return read;
    }
    
    public int read(byte b[], int off, int len) throws IOException {
        try {
            return read(0, b, off, len);
        } catch (TimeoutException e) {
            throw new RuntimeException("Impossible exception");
        }
    }

    public void close() throws IOException {
        in.close();
    }
    
    public int available() throws IOException {
        int available = in.available();
        if (available <= 0) {
            return available;
        }
        // available > 0
        if (fReadBytes == fChunkSize) {
            fReadBytes = 0;
            try {
                readChunkSize(1);
            } catch (TimeoutException e) {
                return 0;
            }
        }
        // fReadBytes < fChunkSize
        if (fChunkSize == EOF || fChunkSize == 0) {
            fChunkSize = EOF;
            return 0;
        }
        available = in.available();
        int left = fChunkSize - fReadBytes;
        return (available < left) ? available : left;
    }
    
    public long skip(long n) throws IOException {
        for (long i = 0; i < n; i++) {
            int data = this.read();
            if (data < 0) {
                return i;
            }
        }
        return n;
    }
    
    public synchronized void mark(int readlimit) {
        // nop
    }
    
    public synchronized void reset() throws IOException {
        // nop
    }
    
    public boolean markSupported() {
        return false;
    }
}
