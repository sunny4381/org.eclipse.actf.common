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

import java.io.InputStream;

public class MessageBody {
    private final InputStream fMessageBody;
    private final SocketTimeoutRetryInputStream fMessageBodySocketTimeoutRetry;

    private int fContentLength;

    private boolean isChunkedEncoding;

    /**
     * Create a message body of content length encoding.
     * 
     * @param msgBody
     * @param contentLength
     */
    public MessageBody(InputStream msgBody, int contentLength) {
        fMessageBody = msgBody;
        fMessageBodySocketTimeoutRetry = new SocketTimeoutRetryInputStream(msgBody);
        fContentLength = contentLength;
        isChunkedEncoding = false;
    }

    /**
     * Create a message body of chunked encoding.
     * 
     * @param msgBody
     */
    public MessageBody(InputStream msgBody) {
        fMessageBody = msgBody;
        fMessageBodySocketTimeoutRetry = new SocketTimeoutRetryInputStream(msgBody);
        fContentLength = -1;
        isChunkedEncoding = true;
    }

    /**
     * Returns content length of this message body. If no content length is
     * specified, e.g. when chunked encoding, -1 is returned.
     * 
     * return content length, or -1 if no content length is specified.
     */
    public int getContentLength() {
        return fContentLength;
    }

    public boolean isChunkedEncoding() {
        return isChunkedEncoding;
    }

    public InputStream getMessageBodyTimeoutInputStream() {
        return fMessageBody;
    }

    public InputStream getMessageBodyInputStream() {
        return fMessageBodySocketTimeoutRetry;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MessageBody[");
        if (isChunkedEncoding) {
            sb.append("chunked");
        } else {
            sb.append("ContentLength=").append(fContentLength);
        }
        sb.append(']');
        return sb.toString();
    }
}
