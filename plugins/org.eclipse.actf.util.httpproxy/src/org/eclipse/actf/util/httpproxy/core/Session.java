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
package org.eclipse.actf.util.httpproxy.core;

import java.io.IOException;

import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public class Session {
    static final Logger LOGGER = Logger.getLogger(Session.class);
    
    private HTTPRequestMessage fRequestMsg = null;
    private HTTPResponseMessage fResponseMsg = null;

    synchronized public HTTPRequestMessage getRequestMessage() {
        return fRequestMsg;
    }
        
    synchronized public void start(HTTPRequestMessage req) throws InterruptedException {
        if (req == fRequestMsg) {
            // Restart
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("A session started: stat=" + fRequestMsg + ", req=" + req);
        }
        while (fRequestMsg != null) {
            this.wait();
        }
        // fRequestMsg == null
        fRequestMsg = req;
        this.notifyAll(); // --> receiveResponse()
    }
        
    synchronized public void start(HTTPRequestMessage req, long timeout) throws InterruptedException, TimeoutException {
        if (req == fRequestMsg) {
            // Restart
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("A session started: stat=" + fRequestMsg + ", req=" + req);
        }
        if (fRequestMsg != null) {
            long deadLine = System.currentTimeMillis() + timeout;
            long wait = timeout;
            while (wait > 0) {
                this.wait(wait);    // <--getAndSendResponse()
                if (fResponseMsg == null) {
                    break;
                }
                wait = deadLine - System.currentTimeMillis();
            }
            if (fRequestMsg == null) {
                throw new TimeoutException("Session.start");
            }
        }
        // fRequestMsg == null
        fRequestMsg = req;
        this.notifyAll(); // --> receiveResponse()
    }
        
    synchronized public boolean receiveResponse(HTTPResponseMessage response) throws InterruptedException {
        if (fRequestMsg != null && fRequestMsg.getSerial() != response.getSerial()) {
            return false;
        }
        while (fResponseMsg != null) {
            this.wait();
        }
        fResponseMsg = response;
        this.notifyAll(); // --> getAndSendResponse()
        return true;
    }

    synchronized protected HTTPResponseMessage waitResponse(long timeout)
        throws InterruptedException, TimeoutException {
        if (fResponseMsg == null) {
            long deadLine = System.currentTimeMillis() + timeout;
            long wait = timeout;
            while (wait > 0) {
                this.wait(wait);
                if (fResponseMsg != null) {
                    break;
                }
                wait = deadLine - System.currentTimeMillis();
            }
            if (fResponseMsg == null) {
                throw new TimeoutException("ResponseSync.get");
            }
        }
        // fResponseMsg != null
        HTTPResponseMessage response = fResponseMsg;
        fResponseMsg = null;
        return response;
    }

    synchronized protected void notifyCompletion() {
        fRequestMsg = null;
        this.notifyAll(); //-->start()
    }

    synchronized public HTTPResponseMessage getAndSendResponse(ClientConnection client, long timeout)
        throws InterruptedException, TimeoutException, IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getAndSendResponse: entrered");
        }
        long t0 = System.currentTimeMillis();
        HTTPResponseMessage response = waitResponse(timeout);
        if (response instanceof HTTPMalformedResponseMessage) {
            throw ((HTTPMalformedResponseMessage) response).getIOException();
        }
        long t1 = System.currentTimeMillis();
        timeout -= t1 - t0;
        if (timeout <= 0) {
            timeout = 1;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getAndSendResponse: got a response");
        }
        try {
            client.sendResponse(timeout, response);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getAndSendResponse: sent a response");
            }
        } catch (IOException e) {
            return response;
        } catch (TimeoutException e) {
            return response;
        }
        notifyCompletion();
        return null;
    }

    synchronized public HTTPResponseMessage getResponse(long timeout)
        throws InterruptedException, TimeoutException, IOException {
        HTTPResponseMessage response = waitResponse(timeout);
        notifyCompletion();
        if (response instanceof HTTPMalformedResponseMessage) {
            throw ((HTTPMalformedResponseMessage) response).getIOException();
        }
        return response;
    }
}
