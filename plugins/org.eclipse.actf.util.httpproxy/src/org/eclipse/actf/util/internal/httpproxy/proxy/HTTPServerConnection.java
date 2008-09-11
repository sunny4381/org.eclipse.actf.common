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

package org.eclipse.actf.util.internal.httpproxy.proxy;

import org.eclipse.actf.util.internal.httpproxy.core.HTTPResponseMessage;
import org.eclipse.actf.util.internal.httpproxy.core.HTTPResponseStreamMessage;
import org.eclipse.actf.util.internal.httpproxy.core.ServerConnection;


public class HTTPServerConnection extends ServerConnection {
    private HTTPServerConnection(String host, int port, HTTPRequestDispatcher dispatcher, int retryTime, int timeout) {
        super("[id:" + dispatcher.getDispatcherId() + "] HTTPServerConnection", host, port, 0, 0, dispatcher,
                retryTime, timeout);
    }

    static HTTPServerConnection newConnection(HTTPRequestDispatcher dispatcher, String host, int port, int timeout) {
        return new HTTPServerConnection(host, port, dispatcher, timeout, timeout);
    }

    protected HTTPResponseMessage createHTTPResponseMessage(long msgSerial) {
        return new HTTPResponseStreamMessage(msgSerial);
    }
}
