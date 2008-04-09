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

import org.eclipse.actf.util.httpproxy.util.TimeoutException;

public interface IHTTPResponsePushbackMessage extends IHTTPResponseMessage{

	// Caution!!!! Without pushback option, body will be invalidated.
	public abstract byte[] readBody(long timeout, boolean pushback)
			throws IOException, TimeoutException;
	
	public abstract IPushbackMessageBody getPushbackMessageBody();

}