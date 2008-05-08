/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.IMessageBody;

public class HTTPResponseInMemoryMessage extends HTTPResponseMessage implements IHTTPResponseMessage{
    // Request = Status-Line
    // *(( general-header
    // | request-header
    // | entity-header ) CRLF)
    // CRLF
    // [ message-body ]
    // Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF

    private final byte[] version;

    private final byte[] statusCode;

    private final byte[] reasonPhrase;

    protected HTTPResponseInMemoryMessage(long serial,
                                          byte[] version,
                                          byte[] statusCode,
                                          byte[] reasonPhrase) {
        super(serial);
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public HTTPResponseInMemoryMessage(long serial,
                                       byte[] version,
                                       byte[] statusCode,
                                       byte[] reasonPhrase,
                                       byte[] body) {
        this(serial, version, statusCode, reasonPhrase);
        IMessageBody msgBody = new MessageBody(new ByteArrayInputStream(body), body.length);
        super.setOriginalMessageBody(msgBody);
    }

    protected void setBaseHeaders(IHTTPMessage base) {
        for (IHTTPHeader h : base.getHeaders()) {
            if (!h.isFieldNameEqualsTo(IHTTPHeader.CONTENT_LENGTH_A)) {
                setHeader(h.getName(), h.getValue());
            }
        }
    }

    public HTTPResponseInMemoryMessage(IHTTPResponseMessage base,
                                       byte[] body) {
        this(base.getSerial(),
             base.getHTTPVersionAsBytes(),
             base.getStatusCodeAsBytes(),
             base.getReasonPhraseAsBytes(),
             body);
        setBaseHeaders(base);
    }

    public String getHTTPVersionAsString() {
        return new String(version);
    }

    public byte[] getHTTPVersionAsBytes() {
        return version;
    }

    public String getStatusCodeAsString() {
        return new String(statusCode);
    }

    public byte[] getStatusCodeAsBytes() {
        return statusCode;
    }

    public String getReasonPhraseAsString() {
        return new String(reasonPhrase);
    }

    public byte[] getReasonPhraseAsBytes() {
        return reasonPhrase;
    }

    protected void writeFirstLine(OutputStream out) throws IOException {
        out.write(version);
        out.write(SP);
        out.write(statusCode);
        out.write(SP);
        out.write(reasonPhrase);
        out.write(CR);
        out.write(LF);
    }
}
