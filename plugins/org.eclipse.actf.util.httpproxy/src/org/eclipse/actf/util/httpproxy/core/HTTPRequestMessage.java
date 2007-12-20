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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTTPRequestMessage extends HTTPMessage {
    // Request = Request-Line
    //                  *(( general-header
    //                    | request-header
    //                    | entity-header ) CRLF)
    //                  CRLF
    //                  [ message-body ]

    private BufferRange fMethod;
    private BufferRange fRequestURI;
    private String modifiedRequestURI;
    private BufferRange fHTTPVersion;

    public HTTPRequestMessage(long serial) {
        super(serial);
        fMethod = new BufferRange();
        fRequestURI = new BufferRange();
        fHTTPVersion = new BufferRange();
    }
    
    public BufferRange getMethod() {
        return fMethod;
    }

    public byte[] getMethodAsBytes() {
        return getBuffer().getAsBytes(fMethod);
    }

    public String getMethodAsString() {
        return getBuffer().getAsString(fMethod);
    }

    public BufferRange getRequestURI() {
        if (modifiedRequestURI != null) return null;
        return fRequestURI;
    }

    public void setRequestURIString(String newRequestURI) {
        this.modifiedRequestURI = newRequestURI;
    }

    public String getOriginalRequestURIString() {
        return getBuffer().getAsString(fRequestURI);
    }

    public String getRequestURIString() {
        if (modifiedRequestURI != null) return modifiedRequestURI;
        return getOriginalRequestURIString();
    }
        
    public BufferRange getHTTPVersion() {
        return fHTTPVersion;
    }

    public byte[] getHTTPVersionAsBytes() {
        return getBuffer().getAsBytes(fHTTPVersion);
    }

    public String getHTTPVersionAsString() {
        return getBuffer().getAsString(fHTTPVersion);
    }
        
    public boolean isMethodEqualsTo(byte[] method) {
        return getBuffer().equals(fMethod, method);
    }

    protected void writeFirstLine(OutputStream out) throws IOException {
        HTTPMessageBuffer buf = getBuffer();
        buf.writeTo(out, fMethod);
        out.write(SP);
        if (modifiedRequestURI == null) {
            buf.writeTo(out, fRequestURI);
        } else {
            out.write(modifiedRequestURI.getBytes());
        }
        out.write(SP);
        buf.writeTo(out, fHTTPVersion);
        out.write(CR);
        out.write(LF);
    }

    protected boolean isBodyEmpty() {
        return false;
    }

    public boolean isResponseBodyEmpty() {
        return (HTTPHeader.METHOD_CONNECT.equals(getMethodAsString())
                || HTTPHeader.METHOD_HEAD.equals(getMethodAsString()));
    }

    private boolean contentLengthInvalidMessage;
    final void recalculateContentLength() {
        MessageBody body = getMessageBody();
        if (body.getContentLength() == -1) {
            contentLengthInvalidMessage = true;
            InputStream is = body.getMessageBodyInputStream();
            if (false) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int b;
                try {
                    while (true) {
                        b = is.read();
                        if (b < 0) break;
                        os.write(b);
                    }
                } catch (IOException e) {
                }
                byte[] bodyByteArray = os.toByteArray();
                ByteArrayInputStream newIs = new ByteArrayInputStream(bodyByteArray);
                setOriginalMessageBody(new MessageBody(newIs, bodyByteArray.length));
            } else {
                // We regard body is empty.
                setOriginalMessageBody(new MessageBody(is, 0));
            }
        }
    }

    public boolean isConnectionShutdownRequired() {
        if (!isConnectionToBeClosed()) return false;
        MessageBody body = getMessageBody();
        if (body.isChunkedEncoding()) return false;
        if (!contentLengthInvalidMessage && (body.getContentLength() >= 0)) return false;
        return true;
    }

    protected void writeBodyWithoutContentLength(long timeout,
                                                 MessageBody msgBody,
                                                 OutputStream out) {
        // do nothing.  We regard body is empty.
        return;
    }

}
