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
import java.io.OutputStream;

import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public class ChunkedMessageBodyReader extends HTTPReader {
    static final Logger LOGGER = Logger.getLogger(ChunkedMessageBodyReader.class);
    
    public ChunkedMessageBodyReader(InputStream in) {
        super(in);
    }
    
    public int readChunkedMessage(long timeout, OutputStream out, HTTPMessage msg) throws IOException, TimeoutException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("readChunkedMessage: timeout=" + timeout);
        }
        int totalChunkSize = readChunkedMessageBody(timeout, out);

        HTTPMessageBuffer buf = msg.getBuffer();
        HeaderInBuffer header = readHeader(timeout, buf);
        while (header != null) {
            msg.addTrailingHeader(header);
            header = readHeader(timeout, buf);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("readChunkedMessage: totalChunkSize=" + totalChunkSize);
        }
        return totalChunkSize;
    }
}
