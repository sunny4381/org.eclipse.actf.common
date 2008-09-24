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

import org.eclipse.actf.util.internal.httpproxy.core.BufferRange;

/**
 * A HTTP request message.
 */
public interface IHTTPRequestMessage extends IHTTPMessage {

	/**
	 * Gets the HTTP method field of this request message.
	 * @return buffer range that indicates the HTTP method field
	 */
	public abstract BufferRange getMethod();

	/**
	 * Gets the HTTP method field of this request message.
	 * @return value of HTTP method field as an array of bytes 
	 */
	public abstract byte[] getMethodAsBytes();

	/**
	 * Gets the HTTP method field of this request message.
	 * @return value of HTTP method field as a String
	 */
	public abstract String getMethodAsString();

	/**
	 * Gets the request URI field of this request message.
	 * @return buffer range that indicates the Request URI field
	 */
	public abstract BufferRange getRequestURI();

	/**
	 * Sets the request URI field of this request message.
	 * @param newRequestURI request URI
	 */
	public abstract void setRequestURIString(String newRequestURI);

	/**
	 * Gets the original request URI field of this request message.
	 * @return value of original request URI field
	 */
	public abstract String getOriginalRequestURIString();

	/**
	 * Gets the request URI field of this request message.
	 * @return value of request URI field as a String
	 */
	public abstract String getRequestURIString();

	/**
	 * Gets the HTTP version field of this request message.
	 * @return buffer range that indicates the HTTP version field
	 */
	public abstract BufferRange getHTTPVersion();

	/**
	 * Compares the method field of this request message with the specified value.
	 * @param method value to compare
	 * @return true if the method field is equals to the specified value 
	 */
	public abstract boolean isMethodEqualsTo(byte[] method);

	/**
	 * Returns whether the body of this request message is empty or not.
	 * @return true if the body of this request message is empty, or false if it is not empty
	 */
	public abstract boolean isResponseBodyEmpty();

	/**
	 * Returns whether the connection for exchanging this request message needs to be shutdown or not. 
	 * @return true if the connection needs to be shutdown
	 */
	public abstract boolean isConnectionShutdownRequired();

}