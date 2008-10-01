/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

import java.io.IOException;
import java.io.PushbackInputStream;


/**
 * A HTTP response message which can provide {@link IPushbackMessageBody}.
 */
public interface IHTTPResponsePushbackMessage extends IHTTPResponseMessage{

	/**
	 * Reads the body of this message and returns it as an array of bytes.
	 * Note that the message body cannot be read later if false is specified for 'pushback' parameter or if
	 * an exception is thrown.
	 * @param timeout number of milliseconds until when the message body is read, or 0 if it does not need to timeout
	 * @param pushback true if the read bytes need to be pushed back to the input stream
	 * @return body of this message as an array of bytes
	 * @throws IOException
	 * @throws TimeoutException
	 */
	// Caution!!!! Without pushback option, body will be invalidated.
	public abstract byte[] readBody(long timeout, boolean pushback)
			throws IOException, TimeoutException;
	
	/**
	 * Returns a message body that can provide {@link PushbackInputStream} for reading the body.
	 * @return a message body that can provide {@link PushbackInputStream} for reading the body.
	 */
	public abstract IPushbackMessageBody getPushbackMessageBody();

}