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

import java.io.InputStream;
import java.net.Socket;

import org.eclipse.actf.util.httpproxy.core.IClientConnection;
import org.eclipse.actf.util.httpproxy.core.IHTTPMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;


public abstract class RequestDispatcher extends Thread {
    private static final Logger LOGGER = Logger.getLogger(RequestDispatcher.class);

    private final int fId;

    /** Message which just arrived from a client */
    private FixedSizeQueue fArrivedMsgs;

    private IHTTPRequestMessage fNextMsg = null;

    protected final Session fSession;

    protected RequestDispatcher(String name, Session session, IClientConnection client, Socket clientSock, int queueSize) {
        super(name);
        this.fSession = session;
        fId = clientSock.getPort();
        fArrivedMsgs = new FixedSizeQueue(queueSize);
    }
        
    public int getDispatcherId() {
        return fId;
    }
        
    public void putRequest(IHTTPRequestMessage req, long timeout) throws InterruptedException, TimeoutException {
        fArrivedMsgs.put(req, timeout);
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Request received from a client: ");
            sb.append("tid=").append(req.getTid());
            sb.append(", msgSerial=").append(req.getSerial());
            /*
            sb.append("\n[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[\n");
            try {
                ByteArrayOutputStream o  = new ByteArrayOutputStream();
                req.write(o);
                o.close();
                sb.append(o.toString());
            } catch (Exception e) {
                //nop
            }
            sb.append("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]\n");
            */
            DEBUG(sb.toString());
        }
    }
    
    // Called by ServerConnections
    synchronized boolean responseArrived(ServerConnection conn, IHTTPResponseMessage response) throws InterruptedException {
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Response arrived from a server (").append(conn);
            sb.append("): tid=").append(response.getTid());
            sb.append(", msgSerial=").append(response.getSerial());
            /*
            sb.append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
            try {
                ByteArrayOutputStream o  = new ByteArrayOutputStream();
                response.write(o);
                o.close();
                sb.append(o.toString());
            } catch (Exception e) {
                //nop
            }
            sb.append("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
            */
            DEBUG(sb.toString());
        }
        boolean received = fSession.receiveResponse(response);
        if (received) {
            return true;
        } else {
            return false;
        }
    }
    
    private String msgToString(IHTTPMessage msg) {
        StringBuffer sb = new StringBuffer(); 
        if (msg == null) {
            sb.append("null");
        } else {
            sb.append("(ser=").append(msg.getSerial());
            sb.append(",tid=").append(msg.getTid()).append(')');
        }
        return sb.toString();
    }
        
    private String dumpMessages() {
        StringBuffer sb = new StringBuffer();
        sb.append("Sent=").append(msgToString(fSession.getRequestMessage()));
        sb.append(", Next=").append(msgToString(fNextMsg));
        sb.append(", Arrived=").append(fArrivedMsgs.getSize());
        return sb.toString();
    }

    public HTTPRequestReader createHTTPRequestReader(InputStream is) {
        return new HTTPRequestReader(is);
    }
    
    public HTTPResponseReader createHTTPResponseReader(InputStream is) {
        return new HTTPResponseReader(getDispatcherId(), is);
    }
        
    // Called only by run()
    protected IHTTPRequestMessage getNextRequest() throws InterruptedException {
        if (fNextMsg != null) {
            DEBUG("Reuse the same request");
            return fNextMsg;
        } else {
            fNextMsg = (IHTTPRequestMessage) fArrivedMsgs.remove();
            if (LOGGER.isDebugEnabled()) {
                DEBUG("Filled a message: msgSerial=" + fNextMsg.getSerial() + ", tid=" + fNextMsg.getTid());
                DEBUG(dumpMessages());
            }
            return fNextMsg;
        }
    }

    protected void clearNextRequest() {
        fNextMsg = null;
    }
    
    protected void startSessionAndSendRequest(IHTTPRequestMessage request, ServerConnection conn, long timeout)
        throws InterruptedException, TimeoutException {
        fSession.start(request);
        conn.putRequest(request, timeout);
    }           
    
    public abstract void run();
    
    public void close() {
        DEBUG("Closing a RequestDispatcher");
        this.interrupt();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("RequestDispatcher-").append(fId);
        return sb.toString();
    }
    
    protected void DEBUG(String msg) {
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append(this.toString()).append(": ").append(msg);
            LOGGER.debug(sb.toString());
        }
    }

    protected void WARNING(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString()).append(": ").append(msg);
        LOGGER.warning(sb.toString());
    }

    protected void INFO(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString()).append(": ").append(msg);
        LOGGER.info(sb.toString());
    }
}
