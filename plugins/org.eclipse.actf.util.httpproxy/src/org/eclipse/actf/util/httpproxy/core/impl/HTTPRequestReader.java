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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public class HTTPRequestReader extends HTTPMessageReader {
    static final Logger LOGGER = Logger.getLogger(HTTPRequestReader.class);

    // Request = Request-Line
    //                  *(( general-header
    //                    | request-header
    //                    | entity-header ) CRLF)
    //                  CRLF
    //                  [ message-body ]

    public HTTPRequestReader(InputStream in) {
        super(in);
    }

    protected void readFirstLine(HTTPMessage msg, long timeout) throws IOException, TimeoutException {
        // Request-Line = Method SP Request-URI SP HTTP-Version CRLF
        IHTTPRequestMessage req = (IHTTPRequestMessage) msg;
        HTTPMessageBuffer buf = msg.getBuffer();

        // Although skipping whitespaces is not actually correct, a message sometimes starts with whitespaces.
        skipSpaceCRAndLFs(timeout);

        readNextToken(timeout, buf, req.getMethod(), SP);
        if (getLastByte() == -1) {
        	throw new HTTPConnectionException("Request connection is closed.");
        }
        if (getLastByte() != SP) {
            throw new IOException("Unexpected char (expected=SP): "
                                  + Integer.toString(getLastByte())
                                  + "/" + buf.getAsString(req.getMethod()));
        }
        skipSpaces(timeout, buf);
        readNextToken(timeout, buf, req.getRequestURI(), SP);
        if (getLastByte() != SP) {
            throw new IOException("Unexpected char (expected=SP): " + Integer.toString(getLastByte()));
        }
        skipSpaces(timeout, buf);
        readNextToken(timeout, buf, req.getHTTPVersion());
        if (getLastByte() != LF) {
            throw new IOException("Unexpected char (expected=LF): " + Integer.toString(getLastByte()));
        }
    }
}
