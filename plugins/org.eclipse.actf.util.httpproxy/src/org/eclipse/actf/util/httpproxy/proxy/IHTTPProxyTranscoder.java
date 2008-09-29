/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
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

import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;

/**
 * Transcoder of HTTP response message.
 */
public interface IHTTPProxyTranscoder {
	/**
	 * Transforms an HTTP response message.
	 * 
	 * @param id
	 * @param request an HTTP request message
	 * @param response an HTTP response message
	 * @return transcoded HTTP response message
	 */
	public IHTTPResponseMessage transcode(int id, IHTTPRequestMessage request,
			IHTTPResponseMessage response);
}
