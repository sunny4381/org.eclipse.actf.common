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
import java.io.OutputStream;

import org.eclipse.actf.util.httpproxy.core.IBufferRange;


public class HTTPResponseStreamMessage extends HTTPResponseMessage {
    // Request = Status-Line
    //                  *(( general-header
    //                    | request-header
    //                    | entity-header ) CRLF)
    //                  CRLF
    //                  [ message-body ]
    // Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF

    private IBufferRange fHTTPVersion;
    private IBufferRange fStatusCode;
    private IBufferRange fReasonPhrase;

    public HTTPResponseStreamMessage(long serial) {
        super(serial);
        fHTTPVersion = new BufferRange();
        fStatusCode = new BufferRange();
        fReasonPhrase = new BufferRange();
    }
        
    public IBufferRange getHTTPVersion() {
        return fHTTPVersion;
    }
        
    public String getHTTPVersionAsString() {
        return getBuffer().getAsString(fHTTPVersion);
    }
        
    public byte[] getHTTPVersionAsBytes() {
        return getBuffer().getAsBytes(fHTTPVersion);
    }
        
    public IBufferRange getStatusCode() {
        return fStatusCode;
    }
        
    public String getStatusCodeAsString() {
        return getBuffer().getAsString(fStatusCode);
    }

    public byte[] getStatusCodeAsBytes() {
        return getBuffer().getAsBytes(fStatusCode);
    }
        
    public IBufferRange getReasonPhrase() {
        return fReasonPhrase;
    }

    public String getReasonPhraseAsString() {
        return getBuffer().getAsString(fReasonPhrase);
    }
        
    public byte[] getReasonPhraseAsBytes() {
        return getBuffer().getAsBytes(fReasonPhrase);
    }
        
    protected void writeFirstLine(OutputStream out) throws IOException {
        HTTPMessageBuffer buf = getBuffer();
        buf.writeTo(out, fHTTPVersion);
        out.write(SP);
        buf.writeTo(out, fStatusCode);
        out.write(SP);
        buf.writeTo(out, fReasonPhrase);
        out.write(CR);
        out.write(LF);
    }
}
