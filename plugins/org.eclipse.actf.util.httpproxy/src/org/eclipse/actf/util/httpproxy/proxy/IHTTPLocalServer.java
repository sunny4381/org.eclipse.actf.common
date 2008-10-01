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

import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;

/**
 * Interface to return HTTP Response for specified request directly from the
 * proxy.
 */
public interface IHTTPLocalServer {

	/**
	 * Check if the request is target of this implementation. If so, the
	 * implementation needs to create {@link IHTTPResponseMessage} and return it
	 * by using sendResponse method of {@link IHTTPProxyConnection}. Further
	 * process of the proxy will be canceled.
	 * 
	 * @param id
	 *            id of request
	 * @param connection
	 *            target {@link IHTTPProxyConnection}
	 * @param request
	 *            target {@link IHTTPRequestMessage}
	 * @param transcoder
	 *            transcoder set to the proxy. Local server may use this
	 *            transcoder to process the resulting message.
	 * @return true if the request is target of the {@link IHTTPLocalServer}
	 *         implementation
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public abstract boolean processRequest(int id,
			IHTTPProxyConnection connection, IHTTPRequestMessage request,
			IHTTPProxyTranscoder transcoder) throws InterruptedException,
			IOException;

}