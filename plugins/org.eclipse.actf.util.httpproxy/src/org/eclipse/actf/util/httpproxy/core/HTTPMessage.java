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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public abstract class HTTPMessage {
    static final Logger LOGGER = Logger.getLogger(HTTPMessage.class);

    public static final int INIT_NUM_HEADERS = 50;
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 1024;
        
    public static final char CR = 0x0d;
    public static final char LF = 0x0a;
    public static final char SP = 0x20;
    public static final char HT = 0x09;
        
    private transient long fSerial;
    private HTTPMessageBuffer fBuffer;
    private MessageBody fOriginalMessageBody;
    private MessageBody fTransformedMessageBody = null;
    private List fHeaders = new ArrayList(INIT_NUM_HEADERS);
    private List fTrailingHeaders;
    private boolean isChunkedEncoding = false;
    
    private long fTid = 0;
        
    public HTTPMessage(long serial) {
        this(serial, DEFAULT_INITIAL_BUFFER_SIZE);
    }
        
    public HTTPMessage(long serial, int initBufferSize) {
        fSerial = serial;
        fBuffer = new HTTPMessageBuffer(initBufferSize);
        fOriginalMessageBody = null;
    }

    protected HTTPMessage(long serial, byte[] body) {
        fSerial = serial;
        fBuffer = new HTTPMessageBuffer();
        setOriginalMessageBody(new MessageBody(new ByteArrayInputStream(body), body.length));
    }
     
    public long getSerial() {
        return fSerial;
    }
        
    public abstract String getHTTPVersionAsString();
        
    public abstract byte[] getHTTPVersionAsBytes();

    protected abstract boolean isBodyEmpty();

    final void setOriginalMessageBody(MessageBody msgBody) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setOriginalMessageBody: " + msgBody);
        }
        updateContentLengthHeader(msgBody);
        fOriginalMessageBody = msgBody;
    }
    
    final void updateContentLengthHeader(MessageBody msgBody) {
        int oldContentLength = (fOriginalMessageBody == null) ? -1 : fOriginalMessageBody.getContentLength();
        int contentLength = (msgBody == null) ? -1 : msgBody.getContentLength();
        if (contentLength >= 0) {
            setHeader(HTTPHeader.CONTENT_LENGTH_A, Integer.toString(contentLength).getBytes());
        } else if (oldContentLength >= 0) {
            removeHeader(HTTPHeader.CONTENT_LENGTH_A);
        }
    }
    
    protected MessageBody getOriginalMessageBody() {
        return fOriginalMessageBody;
    }
    
    protected MessageBody getTransformedMessageBody() {
        return fTransformedMessageBody;
    }
    
    public MessageBody getMessageBody() {
        return (fTransformedMessageBody != null) ? fTransformedMessageBody : fOriginalMessageBody;
    }
    
    protected abstract void writeFirstLine(OutputStream out) throws IOException;

    protected HTTPMessageBuffer getBuffer() {
        return fBuffer;
    }
        
    public final List getHeaders() {
        return fHeaders;
    }

    public final HTTPHeader removeHeader(byte[] name) {
        int nheaders = fHeaders.size();
        for (int i = 0; i < nheaders; i++) {
            HTTPHeader h = (HTTPHeader) fHeaders.get(i);
            if (!h.isRemoved() && h.isFieldNameEqualsTo(name)) {
                h.setRemoved(true);
                return h;
            }
        }
        return null;
    }
    
    public final void setHeader(byte[] name, byte[] value) {
        HeaderToAdd header = new HeaderToAdd();
        header.init(name, value);
        removeHeader(name);
        fHeaders.add(header);
    }
    
    final void addHeader(HeaderInBuffer header) {
        fHeaders.add(header);
    }

    public final void addTrailingHeader(HTTPHeader trailer) {
        if (fTrailingHeaders == null) {
            fTrailingHeaders = new ArrayList(INIT_NUM_HEADERS);
        }
        fTrailingHeaders.add(trailer);
    }

    public final HTTPHeader getHeader(byte[] name) {
        int len = fHeaders.size();
        for (int i = 0; i < len; i++) {
            HTTPHeader header = (HTTPHeader) fHeaders.get(i);
            if (header.isFieldNameEqualsTo(name)) {
                return header;
            }
        }
        return null;
    }
        
    public final byte[] getHeaderAsBytes(byte[] name) {
        HTTPHeader header = getHeader(name);
        return (header == null) ? null : header.getValue();
    }
        
    public final boolean isChunkedEncoding() {
        return isChunkedEncoding;
    }
        
    public final void setChunkedEncoding(boolean isChunked) {
        isChunkedEncoding = isChunked;
    }
    
    public final void setTid(long tid) {
        fTid = tid;
    }
        
    public long getTid() {
        return fTid;
    }
        
    /*
    private void DEBUG(int ch) {
        System.out.print("Buf[" + fIdx + "]=");
        if (ch < 0) {
            System.out.print("" + ch);
        } else {
            int high = ch >>> 4;
            int low = (ch & 0x0f);
            if (high < 10) {
                high = '0' + high;
            } else {
                high = 'a' + high - 10;
            }
            if (low < 10) {
                low = '0' + low;
            } else {
                low = 'a' + low - 10;
            }
            System.out.print((char) high);
            System.out.print((char) low);
        }
        if (ch > 0x20) {
            System.out.print(" (" + (char) ch + ")");
        }
        System.out.println();
    }
    */
    
    protected void writeHeaders(OutputStream out) throws IOException {
        writeFirstLine(out);
        for (Iterator it = fHeaders.iterator(); it.hasNext(); ) {
            HTTPHeader header = (HTTPHeader) it.next();
            header.writeLine(out);
        }
        out.write(CR);
        out.write(LF);
    }

    protected abstract void writeBodyWithoutContentLength(long timeout,
                                                          MessageBody msgBody,
                                                          OutputStream out)
        throws IOException, TimeoutException;
    
    public void writeBody(long timeout, MessageBody msgBody, OutputStream out) throws IOException, TimeoutException {
        if (msgBody == null) {
            return;
        }
        InputStream body = msgBody.getMessageBodyInputStream();
        int contentLength = msgBody.getContentLength();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("super.writeBody: contentLength=" + contentLength
                         + ", body=" + body
                         + ", isChunkedEncoding=" + msgBody.isChunkedEncoding());
        }
        if (body != null) {
            if (contentLength >= 0) {
                // normal encoding
                for (int i = 0; i < contentLength; i++) {
                    int b = body.read();
                    if (b < 0) {
                        LOGGER.debug("Unexpected connection shutdown...:" + i + ":" + body);
                    }
                    out.write(b);
                }
            } else if (msgBody.isChunkedEncoding()){
                // chunked encoding
                ChunkedMessageBodyReader chunkReader = new ChunkedMessageBodyReader(body);
                chunkReader.readChunkedMessage(timeout, out, this);
            } else {
                writeBodyWithoutContentLength(timeout, msgBody, out);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("super.writeBody: done");
        }
    }

    protected MessageBody transformMessageBody(long timeout, MessageBody src)
        throws IOException, TimeoutException {
        return null;
    }
    
    public void write(long timeout, OutputStream out) throws IOException, TimeoutException {
        if (fTransformedMessageBody == null) {
            fTransformedMessageBody = transformMessageBody(timeout, fOriginalMessageBody);
            if (fTransformedMessageBody != null) {
                updateContentLengthHeader(fTransformedMessageBody);
            }
        }
        
        writeHeaders(out);

        writeBody(timeout, getMessageBody(), out);
    }

    /*
    public final void writeTrailingHeaders(OutputStream out) throws IOException {
        boolean output = false;
        for (Iterator it = fTrailingHeaders.iterator(); it.hasNext(); ) {
            HTTPHeader header = (HTTPHeader) it.next();
            header.write(out);
            output = true;
        }
        if (output) {
            out.write(CR);
            out.write(LF);
        }
    }
    */

    public boolean isHTTPVersion1_1() {
        if (Arrays.equals(HTTPHeader.HTTP_VERSION_1_1_A, getHTTPVersionAsBytes())) return true;
        return false;
    }

    private static final byte[] CLOSE_A = "close".getBytes();
    private static final byte[] KEEP_ALIVE_A = "Keep-Alive".getBytes();

    public void setConnectionHeader(boolean keepalive) {
        if (keepalive) {
            setHeader(HTTPHeader.CONNECTION_A, "Keep-Alive".getBytes());
        } else {
            setHeader(HTTPHeader.CONNECTION_A, "close".getBytes());
        }
    }

    public boolean isConnectionToBeClosed() {
        HTTPHeader h = getHeader(HTTPHeader.CONNECTION_A);
        if (isHTTPVersion1_1()) {
            // When HTTP version is 1.1, we assume the connection is reused by default.
            if (h == null) return false;
            return h.compareValueIgnoreCase(CLOSE_A);
        } else {
            // When HTTP version is 1.0, we assume the connection will be shutdown by default.
            if (h == null) return true;
            if (h.compareValueIgnoreCase(KEEP_ALIVE_A)) return false;
            return true;
        }
    }
        
    public String toString() {
        return "HTTPMessage " + fSerial + ":" + fBuffer.toString();
    }
}
