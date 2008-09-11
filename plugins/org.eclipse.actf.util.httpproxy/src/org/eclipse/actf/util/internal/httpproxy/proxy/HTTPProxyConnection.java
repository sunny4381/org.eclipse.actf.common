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
package org.eclipse.actf.util.internal.httpproxy.proxy;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.actf.util.httpproxy.ExternalProxyConfig;
import org.eclipse.actf.util.httpproxy.HTTPProxy;
import org.eclipse.actf.util.httpproxy.proxy.IClientStateManager;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoderFactory;
import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;
import org.eclipse.actf.util.internal.httpproxy.core.ClientConnection;
import org.eclipse.actf.util.internal.httpproxy.core.ServerConnection;


public class HTTPProxyConnection extends ClientConnection implements IHTTPProxyConnection {
    private final HTTPProxy fProxy;

    HTTPProxy getProxy() {
        return fProxy;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection#getSecretManager()
	 */
    public ISecretManager getSecretManager() {
        return fProxy.getSecretManager();
    }
    
    ExternalProxyConfig getExternalProxyConfig(){
    	return fProxy.getExternalProxyConfig();
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection#getListenPort()
	 */
    public int getListenPort(){
    	return fProxy.getListenPort();
    }

    private final int timeout;

    void notifySuccessfulServerConnection(ServerConnection conn) {
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection#init(org.eclipse.actf.util.httpproxy.ClientStateManager, java.net.Socket, long, int)
	 */
    public void init(IClientStateManager clientStateManager,
                     Socket clientSock,
                     long keepAlive,
                     int initServerGroupIdx) throws IOException {
        // setConnectionName("HTTPProxyConnection-" + clientSock.getPort());
        setConnectionName("[id:" + clientSock.getPort() + "] HTTPProxyConnection");
        HTTPRequestDispatcher dispatcher = new HTTPRequestDispatcher(this,
                                                                     clientStateManager,
                                                                     clientSock,
                                                                     getQueueSize(), timeout);
        IHTTPProxyTranscoderFactory factory = fProxy.getProxyTranscoderFactory();
        if(factory!=null){
        	dispatcher.setTranscoder(factory.newInstance(dispatcher.getDispatcherId()));
        }
        initInternal(clientSock, keepAlive, timeout, dispatcher);
    }

    public HTTPProxyConnection(HTTPProxy fProxy, int queueSize, int timeout) {
        super(fProxy, queueSize);
        this.fProxy = fProxy;
        this.timeout = timeout;
    }

}
