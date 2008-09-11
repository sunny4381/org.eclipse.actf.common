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

import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IMessageBody;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public abstract class HTTPMessageReader extends HTTPReader {
    private static final Logger LOGGER = Logger.getLogger(HTTPMessageReader.class);
    
    protected HTTPMessageReader(InputStream in) {
        super(in);
    }
        
    protected abstract void readFirstLine(HTTPMessage msg, long timeout) throws IOException, TimeoutException;

    protected void processHeader(HTTPMessage msg, IHTTPHeader header) {
    }

    public void readMessage(HTTPMessage msg, boolean isBodyEmpty) throws IOException {
        try {
            readMessage(msg, 0, isBodyEmpty);
        } catch (TimeoutException e) {
            LOGGER.fatal("Impossible exception", e);
            System.exit(1);
        }
    }

    public boolean readMessage(HTTPMessage msg, long timeout, boolean isBodyEmpty)
        throws IOException, TimeoutException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("read: isBodyEmpty=" + isBodyEmpty);
        }
        // Read the first line
        readFirstLine(msg, timeout);

        int contentLength = -1;
        while (fLastByte >= 0) {
            HeaderInBuffer header = readHeader(timeout, msg.getBuffer());
            if (header == null) {
                // EOF
                break;
            }
            msg.addHeader(header);
            if (LOGGER.isDebugEnabled()) {
                String name = new String(header.getName());
                String value = new String(header.getValue());
                LOGGER.debug(name + ": " + value);
            }

            if (header.isFieldNameEqualsTo(IHTTPHeader.CONTENT_LENGTH_A)) {
                String v = new String(header.getValue());
                if (v != null && v.length() > 0) {
                    try {
                        contentLength = Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Illegal header fieald (CONTENT_LENGTH) ignored: " + v);
                    }
                }
            } else if (header.isFieldNameEqualsTo(IHTTPHeader.TRANSFER_ENCODING_A) 
                       && header.isFieldValueEqualsTo(IHTTPHeader.CHUNKED_A)) {
                msg.setChunkedEncoding(true);
            }
            processHeader(msg, header);
        }

        isBodyEmpty |= msg.isBodyEmpty();

        if (isBodyEmpty) {
            LOGGER.debug("The message body is empty.");
        } else {
            IMessageBody msgBody;
            if (msg.isChunkedEncoding()) {
                msgBody = new MessageBody(getInputStream());
            } else {
                msgBody = new MessageBody(getInputStream(), contentLength);
            }
            msg.setOriginalMessageBody(msgBody);
        }

        return true;
    }
}
