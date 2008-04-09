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

public interface IMessageBody {

	/**
	 * Returns content length of this message body. If no content length is
	 * specified, e.g. when chunked encoding, -1 is returned.
	 * 
	 * return content length, or -1 if no content length is specified.
	 */	
	public abstract int getContentLength();

	public abstract boolean isChunkedEncoding();

	public abstract InputStream getMessageBodyTimeoutInputStream();

	public abstract InputStream getMessageBodyInputStream();

}