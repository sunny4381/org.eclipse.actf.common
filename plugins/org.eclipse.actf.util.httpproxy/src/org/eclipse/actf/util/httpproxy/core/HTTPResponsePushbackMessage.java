/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.httpproxy.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.eclipse.actf.util.httpproxy.util.TimeoutException;




public class HTTPResponsePushbackMessage extends HTTPResponseInMemoryMessage {

     public HTTPResponsePushbackMessage(long serial,
                                        byte[] version,
                                        byte[] statusCode,
                                        byte[] reasonPhrase,
                                        MessageBody body,
                                        int pushbackBufferSize) {
        super(serial, version, statusCode, reasonPhrase);
        if (body != null) {
            InputStream is = body.getMessageBodyTimeoutInputStream();
            MessageBody msgBody;
            if (body.isChunkedEncoding()) {
                msgBody = new PushbackMessageBody(is, pushbackBufferSize);
            } else {
                msgBody = new PushbackMessageBody(is, pushbackBufferSize, body.getContentLength());
            }
            super.setOriginalMessageBody(msgBody);
        }
    }

    // Caution!!!! Without pushback option, body will be invalidated.
    public byte[] readBody(long timeout, boolean pushback)
        throws IOException, TimeoutException {
        PushbackMessageBody msgBody = (PushbackMessageBody) getMessageBody();
        if (msgBody == null) return null;
        InputStream body = msgBody.getMessageBodyInputStream();
        int contentLength = msgBody.getContentLength();
        if (body == null) return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (contentLength >= 0) {
            // normal encoding
            for (int i = 0; i < contentLength; i++) {
                os.write(body.read());
            }
        } else {
            // chunked encoding
            ChunkedMessageBodyReader chunkReader = new ChunkedMessageBodyReader(body);
            chunkReader.readChunkedMessage(timeout, os, this);
        }
        byte[] outBytes = os.toByteArray();
        if (pushback) {
            PushbackInputStream is = msgBody.getMessageBodyPushBackInputStream();
            is.unread(outBytes);
        }
        return outBytes;
    }


    public HTTPResponsePushbackMessage(HTTPResponseMessage base,
                                       int pushbackBufferSize) {
        this(base.getSerial(),
             base.getHTTPVersionAsBytes(),
             base.getStatusCodeAsBytes(),
             base.getReasonPhraseAsBytes(),
             base.getMessageBody(),
             pushbackBufferSize);
        setBaseHeaders(base);
    }
}
