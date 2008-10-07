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

import java.io.IOException;

import org.eclipse.actf.util.internal.httpproxy.HTTPProxy;

/**
 * Factory class for {@link IHTTPProxy}
 * 
 * @see IHTTPProxy
 * @see ProxyConfig
 * @see ExternalProxyConfig
 */
public class HTTPProxyFactory {

	/**
	 * Returns a new instance of IHTTPProxy.
	 * 
	 * @param config
	 *            Configuration for the instance
	 * @param externalProxyConfig
	 *            Configuration of an external Proxy that used by this instance.
	 * @return instance of HTTPProxy, or null if configurations are invalid
	 */
	public static IHTTPProxy newProxy(ProxyConfig config,
			ExternalProxyConfig externalProxyConfig) {
		try {
			IHTTPProxy proxy = new HTTPProxy(config, externalProxyConfig);
			return proxy;
		} catch (IOException e) {
			return null;
		}
	}

}
