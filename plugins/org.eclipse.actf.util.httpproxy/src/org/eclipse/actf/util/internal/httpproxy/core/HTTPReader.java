/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.internal.httpproxy.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.util.httpproxy.core.IBufferRange;
import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.TimeoutException;
import org.eclipse.actf.util.httpproxy.util.Logger;


public abstract class HTTPReader {
    static final Logger LOGGER = Logger.getLogger(HTTPReader.class);
    
    public static final int INIT_NUM_HEADERS = 50;
    public static final char CR = 0x0d;
    public static final char LF = 0x0a;
    public static final char SP = 0x20;
    public static final char HT = 0x09;
        
    public static final int EOF = -1;
        
    private PushbackInputStream fIn;
    private long fLastReadTime = 0;
    protected int fLastByte = -1;

    protected HTTPReader(InputStream in) {
        if (in instanceof PushbackInputStream) {
            fIn = (PushbackInputStream) in;
        } else {
            fIn = new PushbackInputStream(in, 1);
        }
    }
    
    protected InputStream getInputStream() {
        return fIn;
    }
        
    protected final long getLastReadTime() {
        return fLastReadTime;
    }
        
    protected final int getLastByte() {
        return fLastByte;
    }

    private int getAvailableInput(long timeout) throws IOException, TimeoutException {
        int data;
        if (false) {
            if (timeout > 0) {
                int available;
                while ((available = fIn.available()) <= 0) {
                    if (available < 0) {
                        return -1;
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
            data = fIn.read();
        } else {
            while (true) {
                try {
                    data = fIn.read();
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
        }
        fLastReadTime = System.currentTimeMillis();
        return data;
    }

    protected final boolean nextByte(long timeout, OutputStream replica) throws IOException, TimeoutException {
        fLastByte = getAvailableInput(timeout);
        if (fLastByte != -1) {
            replica.write(fLastByte);
            return true;
        }
        return false;
    }

    private final boolean hasContinuedLine(long timeout) throws IOException, TimeoutException {
        // Assertion
        if (fLastByte != LF) {
            return false;
        }
        int nextChar = getAvailableInput(timeout);
        fIn.unread(nextChar);
        return (nextChar == SP || nextChar == HT);
    }
        
    protected final int readBytes(long timeout, OutputStream out, int len) throws IOException, TimeoutException {
        int i = 0;
        while (i < len) {
            if (!nextByte(timeout, out)) break;
            i++;
        }
        return i;
    }

    public static final int INIT_NUM_TRAILERS = 2;

    protected List<IHTTPHeader> readChunkedMessageTrailers(long timeout, HTTPMessageBuffer buf) throws IOException, TimeoutException {
        List<IHTTPHeader> trailers = null;
        while (true) {
            HeaderInBuffer header = readHeader(timeout, buf);
            if (header == null) {
                break;
            }
            if (trailers == null) {
                trailers = new ArrayList<IHTTPHeader>(INIT_NUM_TRAILERS);
            }
            trailers.add(header);
        }
        return trailers;
    }
    
    protected HeaderInBuffer readHeader(long timeout, HTTPMessageBuffer buf) throws IOException, TimeoutException {
        // Read 'field-name'
        IBufferRange fieldName = new BufferRange();
        readNextToken(timeout, buf, fieldName, ':');
        if (fLastByte == EOF || fLastByte == LF) {
            return null;
        }
        if (fLastByte != ':') {
            // Unexpected line
            throw new IOException("Unexpected char (" + fLastByte + ".) Expected was (':')");
        }

        skipSpaces(timeout, buf);
                        
        // Read 'field-value'
        IBufferRange fieldValue = new BufferRange();
        readNextToken(timeout, buf, fieldValue);
        while (hasContinuedLine(timeout)) {
            readContinuedLine(timeout, buf, fieldValue);
        }
                        
        HeaderInBuffer header = new HeaderInBuffer();
        header.init(buf, fieldName, fieldValue);
        return header;
    }
        
    private final int readContinuedLine(long timeout, HTTPMessageBuffer buf, IBufferRange range) throws IOException, TimeoutException {
        // Read until CRLF
        int start = buf.getLength();
        boolean cr = false;
        boolean crlf = false;
        while (fLastByte != EOF) {
            nextByte(timeout, buf);
            if (fLastByte == CR) {
                cr = true;
            } else if (fLastByte == LF && cr) {
                crlf = true;
                break;
            } else {
                cr = false;
            }
        }
        int length = buf.getLength() - start;
        if (crlf) {
            length -= 2;
        }
        range.setLength(range.getLength() + length);
        return fLastByte;
    }

    protected final void skipSpaceCRAndLFs(long timeout)
        throws IOException, TimeoutException {
        fLastByte = getAvailableInput(timeout);
        while ((fLastByte == SP)
               || (fLastByte == CR)
               || (fLastByte == LF)) {
            fLastByte = SocketTimeoutRetryInputStream.read(fIn);
        }
        fIn.unread(fLastByte);
    }
        
    protected final void skipSpaces(long timeout, OutputStream replica) throws IOException, TimeoutException {
        fLastByte = getAvailableInput(timeout);
        while (fLastByte == SP) {
            replica.write(fLastByte);
            fLastByte = SocketTimeoutRetryInputStream.read(fIn);
        }
        fIn.unread(fLastByte);
    }

    protected final int readNextToken(long timeout, HTTPMessageBuffer buf, IBufferRange marker) throws IOException, TimeoutException {
        return readNextToken(timeout, buf, marker, (char) 0);
    }
        
    protected final int readNextToken(long timeout, HTTPMessageBuffer buf, IBufferRange marker, char delim) throws IOException, TimeoutException {
        int start = buf.getLength();
        nextByte(timeout, buf);
        int length = 0;
        boolean cr = false;
        while (fLastByte != EOF) {
            if (delim != 0 && fLastByte == delim) {
                break;
            }
            if (fLastByte == CR) {
                cr = true;
            } else if (fLastByte == LF && cr) {
                length -= 1;
                break;
            } else {
                cr = false;
            }
            nextByte(timeout, buf);
            length += 1;
        }
        if (length > 0) { 
            marker.setStart(start);
            marker.setLength(length);
        }
        return fLastByte;
    }

    /*
    private final boolean isTokenChar(int ch) {
        if (ch < 0 || isCtrlChar(ch)) {
            return false;
        } else {
            return isSeperator(ch);
        }
    }

    private final boolean isCtrlChar(int ch) {
        return ((ch >= 0 && ch <= 0x1F) || ch == 0x7F);
    }

    private final boolean isTextChar(int ch) {
        if (ch == SP || ch == HT) {
            return true;
        } else {
            return !isCtrlChar(ch);
        }
    }
        
    private final boolean isSeperator(int ch) {
        switch (ch) {
        case HT: // 0x09
        case SP: // 0x20
        case '"': // 0x22
        case '(': // 0x28
        case ')': // 0x29
        case ',': // 0x2c
        case '/': // 0x2f
        case ':': // 0x3a
        case ';': // 0x3b
        case '<': // 0x3c
        case '=': // 0x3d
        case '>': // 0x3e
        case '?': // 0x3f
        case '@': // 0x40 
        case '[': // 0x5b
        case '\\': // 0x5c
        case ']': // 0x5d
        case '{': // 0x7b
        case '}': // 0x7d
            return true;
        default:
            return false;       
        }
    }
    */
        
    // ----------------------------------------------------------------
    // field-value    = *( field-content | LWS )
    // field-content  = <the OCTETs making up the field-value
    //                                  and consisting of either *TEXT or combinations
    //                                  of token, separators, and quoted-string>
    // TEXT           = <any OCTET except CTLs, but including LWS>
    // quoted-string  = ( <"> *(qdtext | quoted-pair ) <"> )
    // qdtext         = <any TEXT except <">>
    // quoted-pair    = "\" CHAR
    // CHAR           = <any US-ASCII character (octets 0 - 127)>
    // ----------------------------------------------------------------

    // CR LF
    protected void readCRLF(long timeout, OutputStream out) throws IOException, TimeoutException {
        nextByte(timeout, out);
        if (fLastByte != CR) {
            throw new IOException("Unexpected char (" + fLastByte +".) Expected was (" + Integer.toString(CR) + ")");
        }
        nextByte(timeout, out);
        if (fLastByte != LF) {
            throw new IOException("Unexpected char (" + fLastByte +".) Expected was (" + Integer.toString(LF) + ")");
        }
    }
    
    protected final String readNextToken(long timeout, OutputStream out) throws IOException, TimeoutException {
        return readNextToken(timeout, out, (char) 0);
    }
    
    protected final String readNextToken(long timeout, OutputStream out, char delim) throws IOException, TimeoutException {
        nextByte(timeout, out);
        if (fLastByte == EOF) {
            return null;
        }
        boolean cr = false;
        StringBuffer token = new StringBuffer();
        while (fLastByte != EOF) {
            if (delim != 0 && fLastByte == delim) {
                break;
            }
            if (fLastByte == CR) {
                cr = true;
            } else if (fLastByte == LF && cr) {
                break;
            } else {
                cr = false;
                token.append((char) fLastByte);
            }
            nextByte(timeout, out);
        }
        return token.toString();
    }
    
    public int readChunkedMessageBody(long timeout, OutputStream out) throws IOException, TimeoutException {
        final ChunkDecoder decoder = new ChunkDecoder(fIn);
        final ChunkEncoder encoder = new ChunkEncoder(out);
        decoder.setChunkListener(new ChunkListener() {
            public void newChankRead(int n, int size) {
                try {
                    encoder.setMaxChunkSize(size);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        try {
            int b;
            int n = 0;
            while ((b = decoder.read(timeout)) != -1) { 
                encoder.write(b);
                n += 1;
            }
            return n;
        } finally {
            encoder.close();
        }
    }
}
