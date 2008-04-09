/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

import org.eclipse.actf.util.httpproxy.core.impl.BufferRange;

public interface IHTTPRequestMessage extends IHTTPMessage{

	public abstract BufferRange getMethod();

	public abstract byte[] getMethodAsBytes();

	public abstract String getMethodAsString();

	public abstract BufferRange getRequestURI();

	public abstract void setRequestURIString(String newRequestURI);

	public abstract String getOriginalRequestURIString();

	public abstract String getRequestURIString();

	public abstract BufferRange getHTTPVersion();

	public abstract boolean isMethodEqualsTo(byte[] method);

	public abstract boolean isResponseBodyEmpty();

	public abstract boolean isConnectionShutdownRequired();

}