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
import org.eclipse.actf.util.httpproxy.util.TimeoutException;

public interface IHTTPSessionOverrider {

	public abstract IHTTPRequestMessage getSessionRequest();

	public abstract IHTTPResponseMessage getSessionResponse();

	public abstract boolean replaceRequest(IClientStateManager csm,
			IHTTPRequestMessage request) throws IOException;

	public abstract boolean replaceResponse(IClientStateManager csm,
			IHTTPRequestMessage request, IHTTPResponseMessage response,
			int timeout) throws IOException, TimeoutException;

}