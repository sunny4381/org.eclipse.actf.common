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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.actf.util.httpproxy.core.IBufferRange;
import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IMessageBody;

public class HTTPRequestMessage extends HTTPMessage implements IHTTPRequestMessage{
    // Request = Request-Line
    //                  *(( general-header
    //                    | request-header
    //                    | entity-header ) CRLF)
    //                  CRLF
    //                  [ message-body ]

    private IBufferRange fMethod;
    private IBufferRange fRequestURI;
    private String modifiedRequestURI;
    private IBufferRange fHTTPVersion;

    public HTTPRequestMessage(long serial) {
        super(serial);
        fMethod = new BufferRange();
        fRequestURI = new BufferRange();
        fHTTPVersion = new BufferRange();
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#getMethod()
	 */
    public IBufferRange getMethod() {
        return fMethod;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#getMethodAsBytes()
	 */
    public byte[] getMethodAsBytes() {
        return getBuffer().getAsBytes(fMethod);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#getMethodAsString()
	 */
    public String getMethodAsString() {
        return getBuffer().getAsString(fMethod);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#getRequestURI()
	 */
    public IBufferRange getRequestURI() {
        if (modifiedRequestURI != null) return null;
        return fRequestURI;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#setRequestURIString(java.lang.String)
	 */
    public void setRequestURIString(String newRequestURI) {
        this.modifiedRequestURI = newRequestURI;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#getOriginalRequestURIString()
	 */
    public String getOriginalRequestURIString() {
        return getBuffer().getAsString(fRequestURI);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#getRequestURIString()
	 */
    public String getRequestURIString() {
        if (modifiedRequestURI != null) return modifiedRequestURI;
        return getOriginalRequestURIString();
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#getHTTPVersion()
	 */
    public IBufferRange getHTTPVersion() {
        return fHTTPVersion;
    }

    public byte[] getHTTPVersionAsBytes() {
        return getBuffer().getAsBytes(fHTTPVersion);
    }

    public String getHTTPVersionAsString() {
        return getBuffer().getAsString(fHTTPVersion);
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#isMethodEqualsTo(byte[])
	 */
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

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#isResponseBodyEmpty()
	 */
    public boolean isResponseBodyEmpty() {
        return (IHTTPHeader.METHOD_CONNECT.equals(getMethodAsString())
                || IHTTPHeader.METHOD_HEAD.equals(getMethodAsString()));
    }

    private boolean contentLengthInvalidMessage;
    final void recalculateContentLength() {
        IMessageBody body = getMessageBody();
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

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage#isConnectionShutdownRequired()
	 */
    public boolean isConnectionShutdownRequired() {
        if (!isConnectionToBeClosed()) return false;
        IMessageBody body = getMessageBody();
        if (body.isChunkedEncoding()) return false;
        if (!contentLengthInvalidMessage && (body.getContentLength() >= 0)) return false;
        return true;
    }

    protected void writeBodyWithoutContentLength(long timeout,
                                                 IMessageBody msgBody,
                                                 OutputStream out) {
        // do nothing.  We regard body is empty.
        return;
    }

}
