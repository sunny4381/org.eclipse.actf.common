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
package org.eclipse.actf.util.httpproxy;

import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;

/**
 * IHTTPProxy is the interface for the HTTP proxy.
 */
public interface IHTTPProxy {

	/**
	 * Get listen port of the proxy
	 * 
	 * @return listen port
	 */
	public abstract int getListenPort();

	/**
	 * Get secret String that is used for confirmation of correct server-client
	 * combination
	 * 
	 * @param id
	 *            target key to obtain the secret
	 * @param remove
	 *            if true, the secret will be removed after invoking this method
	 * @return secret, or null if the corresponding value is not available for
	 *         the target key
	 * @see ISecretManager
	 */
	public abstract String getSecret(String id, boolean remove);

	/**
	 * Start the proxy.
	 */
	public abstract void startThread();

	/**
	 * Stop the proxy.
	 */
	public abstract void stopThread();

}