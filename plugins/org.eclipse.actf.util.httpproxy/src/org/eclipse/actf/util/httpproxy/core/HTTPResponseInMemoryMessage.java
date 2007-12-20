/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class HTTPResponseInMemoryMessage extends HTTPResponseMessage {
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

    public static final byte[] EMPTY_BODY = new byte[0];

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
        MessageBody msgBody = new MessageBody(new ByteArrayInputStream(body), body.length);
        super.setOriginalMessageBody(msgBody);
    }

    protected void setBaseHeaders(HTTPMessage base) {
        List l = base.getHeaders();
        Iterator it = l.iterator();
        while (it.hasNext()) {
            HTTPHeader h = (HTTPHeader) it.next();
            if (!h.isFieldNameEqualsTo(HTTPHeader.CONTENT_LENGTH_A)) {
                setHeader(h.getName(), h.getValue());
            }
        }
    }

    public HTTPResponseInMemoryMessage(HTTPResponseMessage base,
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
