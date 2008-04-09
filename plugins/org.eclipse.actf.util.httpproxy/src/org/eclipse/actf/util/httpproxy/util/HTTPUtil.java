/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.httpproxy.util;

import java.io.IOException;

import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponsePushbackMessage;
import org.eclipse.actf.util.httpproxy.core.impl.HTTPResponseInMemoryMessage;
import org.eclipse.actf.util.httpproxy.core.impl.HTTPResponsePushbackMessage;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection;

public class HTTPUtil {
	public static void sendFailedToClient(IHTTPProxyConnection fClient,
			IHTTPRequestMessage request) throws InterruptedException,IOException {
		fClient.sendResponse(new HTTPResponseInMemoryMessage(request
						.getSerial(), IHTTPHeader.HTTP_VERSION_1_0_A, "404"
						.getBytes(), "Not found".getBytes(),
						IHTTPResponseMessage.EMPTY_BODY));
	}

	public static IHTTPResponseMessage createHTTPResponseInMemoryMessage(
			IHTTPResponseMessage base, byte[] body) {
		return new HTTPResponseInMemoryMessage(base, body);
	}

	public static IHTTPResponseMessage createHTTPResponseInMemoryMessage(
			long serial, byte[] version, byte[] statusCode,
			byte[] reasonPhrase, byte[] body) {
		return new HTTPResponseInMemoryMessage(serial, version, statusCode, reasonPhrase, body);
	}
	
	public static IHTTPResponsePushbackMessage createHTTPResponsePushbackMessage(IHTTPResponseMessage base,
            int pushbackBufferSize){
		return new HTTPResponsePushbackMessage(base, pushbackBufferSize);
	}

}
