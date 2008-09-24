/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

import java.io.InputStream;

/**
 * Body of a HTTP request/response message.
 */
public interface IMessageBody {

	/**
	 * Returns content length of this message body. If no content length is
	 * specified or it is chunked encoding, -1 is returned.
	 * 
	 * @return content length, or -1 if no content length is specified.
	 */	
	public abstract int getContentLength();

	/**
	 * Returns whether the encoding of this message body is chunked encoding.
	 * @return true if the encoding of this message body is chunked encoding
	 */
	public abstract boolean isChunkedEncoding();

	/**
	 * Returns an input stream for this message body whose read methods 
	 * keep retrying to read even when SocketTimeoutExceptions are thrown.  
	 * @return input stream for this message body
	 */
	public abstract InputStream getMessageBodyTimeoutInputStream();

	/**
	 * Returns an input stream for this message body.
	 * @return input stream for this message body
	 */
	public abstract InputStream getMessageBodyInputStream();

}