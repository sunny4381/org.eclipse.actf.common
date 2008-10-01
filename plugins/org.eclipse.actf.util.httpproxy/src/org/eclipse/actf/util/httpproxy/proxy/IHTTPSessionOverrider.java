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
import org.eclipse.actf.util.httpproxy.core.TimeoutException;

/**
 * Interface to override HTTP Session in the proxy
 */
public interface IHTTPSessionOverrider {

	/**
	 * Return the processed {@link IHTTPRequestMessage}
	 * 
	 * @return processed {@link IHTTPRequestMessage}
	 */
	public abstract IHTTPRequestMessage getSessionRequest();

	/**
	 * Return the processed {@link IHTTPResponseMessage}
	 * @return processed {@link IHTTPResponseMessage}
	 */
	public abstract IHTTPResponseMessage getSessionResponse();

	/**
	 * Check if the request is target of this overrider.
	 * @param csm 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public abstract boolean replaceRequest(IClientStateManager csm,
			IHTTPRequestMessage request) throws IOException;

	/**
	 * @param csm
	 * @param request
	 * @param response
	 * @param timeout
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public abstract boolean replaceResponse(IClientStateManager csm,
			IHTTPRequestMessage request, IHTTPResponseMessage response,
			int timeout) throws IOException, TimeoutException;

}