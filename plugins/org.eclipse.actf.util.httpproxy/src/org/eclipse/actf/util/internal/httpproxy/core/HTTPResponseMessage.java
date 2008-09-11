/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.internal.httpproxy.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.IMessageBody;

public abstract class HTTPResponseMessage extends HTTPMessage implements IHTTPResponseMessage {
    // Request = Status-Line
    //                  *(( general-header
    //                    | request-header
    //                    | entity-header ) CRLF)
    //                  CRLF
    //                  [ message-body ]
    // Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
    private static final byte[] STATUS_NO_CONTENT_A = "204".getBytes();
    private static final byte[] STATUS_NOT_MODIFIED_A = "304".getBytes();

    private static final int WRITEBODY_DEFAULT_WAIT_DENOM = 10;

    protected HTTPResponseMessage(long serial) {
        super(serial);
    }

    protected boolean isBodyEmpty() {
        byte[] statCode = getStatusCodeAsBytes();
        return (Arrays.equals(statCode, STATUS_NO_CONTENT_A)
                || Arrays.equals(statCode, STATUS_NOT_MODIFIED_A));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage#getStatusCodeAsString()
	 */
    public abstract String getStatusCodeAsString();
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage#getStatusCodeAsBytes()
	 */
    public abstract byte[] getStatusCodeAsBytes();
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage#getReasonPhraseAsString()
	 */
    public abstract String getReasonPhraseAsString();
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage#getReasonPhraseAsBytes()
	 */
    public abstract byte[] getReasonPhraseAsBytes();

    protected void writeBodyWithoutContentLength(long timeout,
                                                 IMessageBody msgBody,
                                                 OutputStream out)
        throws IOException {
        if (isConnectionToBeClosed()) {
            InputStream body = msgBody.getMessageBodyInputStream();
            // Write until the connection is closed.
            for (;;) {
                int b = body.read();
                if (b < 0) break;
                out.write(b);
            }
        } else {
            InputStream body = msgBody.getMessageBodyTimeoutInputStream();
            // Try to read the incoming data as much as we can.
            long keepAliveTimeout = timeout / WRITEBODY_DEFAULT_WAIT_DENOM;
            if (LOGGER.isDebugEnabled()) {												// DEBUG
                StringBuffer sb = new StringBuffer();									// DEBUG
                sb.append("keepAliveTimeout=" + keepAliveTimeout + " ms"); 					// DEBUG
                LOGGER.debug(sb.toString());											// DEBUG
                System.out.println(sb.toString()); // DEBUG
            }
            final long start = System.currentTimeMillis();  
            long before = System.currentTimeMillis();
            while ((System.currentTimeMillis() - before) < keepAliveTimeout) {
            	int b;
                try {
                	b = body.read();
                    if (b < 0) break;
                    before = System.currentTimeMillis();
                } catch (SocketTimeoutException e) {
                	continue;
                }
                out.write(b);
            }
            if (LOGGER.isDebugEnabled()) {												// DEBUG
                StringBuffer sb = new StringBuffer();									// DEBUG
                sb.append("elapsed " + (System.currentTimeMillis() - start) + " ms"); 		// DEBUG
                LOGGER.debug(sb.toString());											// DEBUG
                System.out.println(sb.toString()); // DEBUG
            }																			// DEBUG
        }
    }
}
