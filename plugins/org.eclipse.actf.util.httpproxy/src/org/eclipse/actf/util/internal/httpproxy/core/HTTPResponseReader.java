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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.actf.util.httpproxy.core.TimeoutException;
import org.eclipse.actf.util.httpproxy.util.Logger;


public class HTTPResponseReader extends HTTPMessageReader {
    private static final Logger LOGGER = Logger.getLogger(HTTPResponseReader.class);

    @SuppressWarnings("unused")
	private final int id;

    // Request = Status-Line
    // *(( general-header
    // | request-header
    // | entity-header ) CRLF)
    // CRLF
    // [ message-body ]
    // Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF

    public HTTPResponseReader(int id, InputStream in) {
        super(in);
        this.id = id;
    }
        
    @SuppressWarnings("nls")
	protected void readFirstLine(HTTPMessage msg, long timeout) throws IOException, TimeoutException {
        // Request-Line = Method SP Request-URI SP HTTP-Version CRLF
        HTTPResponseStreamMessage response = (HTTPResponseStreamMessage) msg;
        HTTPMessageBuffer buf = msg.getBuffer();

        readNextToken(timeout, buf, response.getHTTPVersion(), SP);
        if (getLastByte() != SP) {
            throw new IOException("Unexpected char (expected=SP): " + Integer.toString(getLastByte()));
        }
        skipSpaces(timeout, buf);
        readNextToken(timeout, buf, response.getStatusCode(), SP);
        if (getLastByte() != SP) {
            throw new IOException("Unexpected char (expected=SP): " + Integer.toString(getLastByte()));
        }
        skipSpaces(timeout, buf);
        readNextToken(timeout, buf, response.getReasonPhrase());
        if (getLastByte() != LF) {
            throw new IOException("Unexpected char (expected=LF): " + Integer.toString(getLastByte()));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Response-RFL:" + response.getHTTPVersionAsString()
                         + " " + response.getStatusCodeAsString()
                         + " " + response.getReasonPhraseAsString());
        }
    }

    /*
    protected HTTPMessage readMessageBody(HTTPMessage msg, int contentLength, long timeout)
        throws IOException, TimeoutException {
        if (msg.isChunkedEncoding()) {
            if (LOGGER.isDebugEnabled()) {
                DEBUG("ChankedEncoding:" + msg.getSerial() + "\n");
            }
            readChunkedMessageBody(0, msg);
        } else if (contentLength >= 0) {
            if (LOGGER.isDebugEnabled()) {
                DEBUG("Content-Length:" + contentLength + " " + msg.getSerial() + "\n");
            }
            int readBytes = readBytes(0, msg, contentLength);
            msg.markMessageBodyEnd();
            if (readBytes != contentLength) {
                FATAL("Illegal message body: read=" + readBytes + ", contentLength=" + contentLength);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                DEBUG("End is marked by CS." + msg.getSerial() + "\n");
            }
            readBytes(0, msg);
            msg.markMessageBodyEnd();
        }
        return msg;
    }

    private final void WARNING(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("[id:").append(id).append("] HTTPResponseReader: ");
        sb.append(msg);
        LOGGER.warning(sb.toString());
    }

    private final void DEBUG(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("[id:").append(id).append("] HTTPResponseReader: ");
        sb.append(msg);
        LOGGER.debug(sb.toString());
    }

    private final void FATAL(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("[id:").append(id).append("] HTTPResponseReader: ");
        sb.append(msg);
        LOGGER.fatal(sb.toString());
    }
    */

}
