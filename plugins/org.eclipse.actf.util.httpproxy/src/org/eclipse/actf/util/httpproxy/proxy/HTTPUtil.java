/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.httpproxy.proxy;

import java.io.IOException;

import org.eclipse.actf.util.httpproxy.core.HTTPHeader;
import org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponseInMemoryMessage;




public class HTTPUtil {
    public static void sendFailedToClient(HTTPProxyConnection fClient,
                                          HTTPRequestMessage request)
        throws InterruptedException, IOException {
        fClient.sendResponse(new HTTPResponseInMemoryMessage(request.getSerial(),
                                                             HTTPHeader.HTTP_VERSION_1_0_A,
                                                             "404".getBytes(),
                                                             "Not found".getBytes(),
                                                             HTTPResponseInMemoryMessage.EMPTY_BODY));
    }
}
