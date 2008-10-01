/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.proxy;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.actf.util.httpproxy.core.IClientConnection;

/**
 * Interface for HTTP connection with client.
 */
public interface IHTTPProxyConnection extends IClientConnection {

	/**
	 * Gets the secret manager for this proxy connection.
	 * 
	 * @return secret manager for this proxy connection
	 */
	public abstract ISecretManager getSecretManager();

	/**
	 * Gets the TCP port number on which this proxy connection is listening.
	 * 
	 * @return listening TCP port number
	 */
	public abstract int getListenPort();

	/**
	 * Initializes this proxy connection.
	 * 
	 * @param clientStateManager
	 *            client state manager
	 * @param clientSock
	 *            TCP socket for client connection
	 * @param keepAlive
	 *            number of milliseconds until the timeout of each network I/O
	 *            operation
	 * @param initServerGroupIdx
	 *            index of initial server group
	 * @throws IOException
	 */
	public abstract void init(IClientStateManager clientStateManager,
			Socket clientSock, long keepAlive, int initServerGroupIdx)
			throws IOException;

}