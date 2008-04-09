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

public interface IHTTPProxyConnection extends IClientConnection {

	public abstract ISecretManager getSecretManager();

	public abstract int getListenPort();

	public abstract void init(IClientStateManager clientStateManager,
			Socket clientSock, long keepAlive, int initServerGroupIdx)
			throws IOException;

}