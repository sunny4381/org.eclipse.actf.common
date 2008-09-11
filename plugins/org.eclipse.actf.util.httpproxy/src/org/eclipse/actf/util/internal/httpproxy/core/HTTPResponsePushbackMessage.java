/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.internal.httpproxy.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponsePushbackMessage;
import org.eclipse.actf.util.httpproxy.core.IMessageBody;
import org.eclipse.actf.util.httpproxy.core.IPushbackMessageBody;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public class HTTPResponsePushbackMessage extends HTTPResponseInMemoryMessage implements IHTTPResponsePushbackMessage{

     public HTTPResponsePushbackMessage(long serial,
                                        byte[] version,
                                        byte[] statusCode,
                                        byte[] reasonPhrase,
                                        IMessageBody body,
                                        int pushbackBufferSize) {
        super(serial, version, statusCode, reasonPhrase);
        if (body != null) {
            InputStream is = body.getMessageBodyTimeoutInputStream();
            IMessageBody msgBody;
            if (body.isChunkedEncoding()) {
                msgBody = new PushbackMessageBody(is, pushbackBufferSize);
            } else {
                msgBody = new PushbackMessageBody(is, pushbackBufferSize, body.getContentLength());
            }
            super.setOriginalMessageBody(msgBody);
        }
    }

    // Caution!!!! Without pushback option, body will be invalidated.
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPResponsePushbackMessage#readBody(long, boolean)
	 */
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


    public HTTPResponsePushbackMessage(IHTTPResponseMessage base,
                                       int pushbackBufferSize) {
        this(base.getSerial(),
             base.getHTTPVersionAsBytes(),
             base.getStatusCodeAsBytes(),
             base.getReasonPhraseAsBytes(),
             base.getMessageBody(),
             pushbackBufferSize);
        setBaseHeaders(base);
    }

	public IPushbackMessageBody getPushbackMessageBody() {
        return (PushbackMessageBody) getMessageBody();
	}
}
