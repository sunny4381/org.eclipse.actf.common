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
import java.io.PushbackInputStream;

public class PushbackMessageBody extends MessageBody {
    private final PushbackInputStream pushbackInputStream;

    /**
     * Create a message body of content length encoding.
     * 
     * @param msgBody
     * @param contentLength
     */
    PushbackMessageBody(InputStream msgBodyInputStream,
                        int pushbackBuffersize,
                        int contentLength) {
        super(msgBodyInputStream, contentLength);
        this.pushbackInputStream = new PushbackInputStream(super.getMessageBodyInputStream(),
                                                           pushbackBuffersize);
    }

    /**
     * Create a message body of chunked encoding.
     * 
     * @param msgBody
     */
    PushbackMessageBody(InputStream msgBodyInputStream, int pushbackBuffersize) {
        super(msgBodyInputStream);
        this.pushbackInputStream = new PushbackInputStream(super.getMessageBodyInputStream(),
                                                           pushbackBuffersize);
    }

    public InputStream getMessageBodyInputStream() {
        return pushbackInputStream;
    }

    public PushbackInputStream getMessageBodyPushBackInputStream() {
        return pushbackInputStream;
    }
}
