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
package org.eclipse.actf.util.httpproxy.proxy;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.actf.util.httpproxy.HTTPProxy;
import org.eclipse.actf.util.httpproxy.core.ClientConnection;
import org.eclipse.actf.util.httpproxy.core.ServerConnection;


public class HTTPProxyConnection extends ClientConnection {
    private final HTTPProxy fProxy;

    HTTPProxy getProxy() {
        return fProxy;
    }

    SWFSecretManager getSecretManager() {
        return fProxy.getSecretManager();
    }

    private final int timeout;

    void notifySuccessfulServerConnection(ServerConnection conn) {
    }

    public void init(ClientStateManager clientStateManager,
                     Socket clientSock,
                     long keepAlive,
                     int initServerGroupIdx) throws IOException {
        // setConnectionName("HTTPProxyConnection-" + clientSock.getPort());
        setConnectionName("[id:" + clientSock.getPort() + "] HTTPProxyConnection");
        HTTPRequestDispatcher dispatcher = new HTTPRequestDispatcher(this,
                                                                     clientStateManager,
                                                                     clientSock,
                                                                     getQueueSize(), timeout);
        dispatcher.setTranscoder(SWFTranscoder.newInstance(dispatcher.getDispatcherId()));
        initInternal(clientSock, keepAlive, timeout, dispatcher);
    }

    public HTTPProxyConnection(HTTPProxy fProxy, int queueSize, int timeout) {
        super(fProxy, queueSize);
        this.fProxy = fProxy;
        this.timeout = timeout;
    }

}
