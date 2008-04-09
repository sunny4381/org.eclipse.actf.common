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
package org.eclipse.actf.util.httpproxy.core.impl;

import java.io.IOException;
import java.io.OutputStream;


public class HTTPMalformedResponseMessage extends HTTPResponseMessage {
    private IOException exception;

    public IOException getIOException() {
        return exception;
    }

    HTTPMalformedResponseMessage(long serial, IOException exception) {
        super(serial);
        this.exception = exception;
    }

    public String getHTTPVersionAsString() {
        return null;
    }

    public byte[] getHTTPVersionAsBytes() {
        return null;
    }

    public String getStatusCodeAsString() {
        return null;
    }

    public byte[] getStatusCodeAsBytes() {
        return null;
    }

    public String getReasonPhraseAsString() {
        return null;
    }

    public byte[] getReasonPhraseAsBytes() {
        return null;
    }

    protected void writeFirstLine(OutputStream out) throws IOException {
        throw new IOException("HTTPMalformedResponseMessage cannot be output.");
    }
}
