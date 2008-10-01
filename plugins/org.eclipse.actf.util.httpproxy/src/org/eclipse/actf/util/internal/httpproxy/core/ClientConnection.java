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

package org.eclipse.actf.util.internal.httpproxy.core;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.eclipse.actf.util.httpproxy.core.IClientConnection;
import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.TimeoutException;
import org.eclipse.actf.util.httpproxy.util.Logger;




public abstract class ClientConnection implements Runnable, IClientConnection {
    private static final Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private final ClientConnectionListener clientConnectionListener;

    private final int fQueueSize;

    // private final AsyncWorkManager fDispatchWorkMan;

    private long fKeepAlive;

    private Socket fClientSocket;

    private InputStream fClientIn;

    private BufferedOutputStream fClientOut;

    private HTTPRequestReader fReader;

    private RequestDispatcher fDispatcher;

    private long fLastReadTime = 0;

    private long fMessageSerial = 0;

    private boolean isHandlingRequest;

    private String connectionName;

    protected void setConnectionName(String name) {
        this.connectionName = name;
    }

    protected int getQueueSize() {
        return fQueueSize;
    }

    protected ClientConnection(ClientConnectionListener clientConnectionListener, int queueSize) {
        this.clientConnectionListener = clientConnectionListener;
        fQueueSize = queueSize;
        // fDispatchWorkMan = dispatchWorkMan;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#resetConnection()
	 */
    public void resetConnection() {
        try {
            if (fClientSocket != null) {
                if (fClientIn != null) {
                    fClientIn.close();
                }
                if (fClientOut != null) {
                    fClientOut.close();
                }
                if (!fClientSocket.isClosed()) {
                    fClientSocket.setSoLinger(true, 0);
                    fClientSocket.close();
                }
                fClientSocket = null;
            }
        } catch (IOException ex) {
            WARNING("Failed to shut down a client output connection (IOException): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#close()
	 */
    public String close() {
        if (LOGGER.isDebugEnabled()) {
            DEBUG("Shutdown a client socket: lastReadTime=" + fLastReadTime);
        }
        if (fDispatcher != null) {
            fDispatcher.close();
        }
        if (fClientSocket != null) {
            try {
                fClientSocket.shutdownInput();
                if (fClientIn != null) {
                    fClientIn.close();
                }
            } catch (IOException ex) {
                WARNING("Failed to close a client connection (IOException): " + ex.getMessage());
            }
        }

        String cNameTmp = connectionName;
        connectionName = null;
        fClientSocket = null;
        fClientOut = null;
        fReader = null;
        fDispatcher = null;
        // System.out.println("CLOSE");
        clientConnectionListener.connectionClosed(this);
        return cNameTmp;
    }

    protected void initInternal(Socket clientSock, long keepAlive, int timeout, RequestDispatcher dispatcher)
            throws IOException {
        fClientSocket = clientSock;
        fClientSocket.setSoTimeout(timeout);
        fClientIn = clientSock.getInputStream();
        fClientOut = new BufferedOutputStream(new SocketTimeoutRetryOutputStream(clientSock.getOutputStream()));
        fKeepAlive = keepAlive;
        fReader = dispatcher.createHTTPRequestReader(fClientIn);
        isHandlingRequest = false;
        fDispatcher = dispatcher;
        fDispatcher.start();
        DEBUG("Initialized");
    }

    protected HTTPRequestMessage createHTTPRequestMessage(long msgSerial) {
        return new HTTPRequestMessage(msgSerial);
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#getClientSocket()
	 */
    public Socket getClientSocket() {
        return fClientSocket;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#getCurrentServerGroupIndex()
	 */
    public int getCurrentServerGroupIndex() {
        return clientConnectionListener.getCurrentServerGroupIndex();
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#isHandlingRequest()
	 */
    public synchronized boolean isHandlingRequest() {
        return isHandlingRequest;
    }

    private synchronized void setHandlingRequest(boolean b) {
        isHandlingRequest = b;
        if (!isHandlingRequest) {
            this.notify();
        }
    }

    private synchronized void waitHandlingRequestFinish() throws InterruptedException {
        while (isHandlingRequest) {
            this.wait();
        }
    }

    protected void requestReceived(IHTTPRequestMessage request) {
        // nop
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#sendResponse(long, org.eclipse.actf.util.httpproxy.core.HTTPResponseMessage, boolean)
	 */
    public void sendResponse(long timeout, IHTTPResponseMessage response, boolean readyToHandleRequest)
        throws InterruptedException, IOException, TimeoutException 
    {
        if (fClientOut == null) return;

        if (!isHandlingRequest()) {
            System.err.println("*****INVALID STATE: response=" + response);
            System.exit(-1);
            return;
        }
        try {
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("Send a response to the client: serial=").append(response.getSerial());
                sb.append(", tid=").append(response.getTid());
                DEBUG(sb.toString());
            }

            // System.out.println(new String(response.getMessageBody()));
            // response.writeHeaders(System.out);
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("Response arrived: tid=").append(response.getTid());
                sb.append(", msgSerial=").append(response.getSerial());
                sb.append("\n_______________________________________________________\n");
                try {
                    ByteArrayOutputStream ob = new ByteArrayOutputStream();
                    BifurcatedOutputStream o = new BifurcatedOutputStream(fClientOut, ob);
                    response.write(timeout, o);
                    ob.close();
                    sb.append(ob.toString());
                } catch (Exception e) {
                    // nop
                }
                sb.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                DEBUG(sb.toString());
            } else {
                response.write(timeout, fClientOut);
            }
            fClientOut.flush();
            if (response.isConnectionToBeClosed()) {
                fClientSocket.shutdownOutput();
                throw new InterruptedException("This connection is no longer available.");
            }
/*
        } catch (IOException e) {
            StringBuffer sb = new StringBuffer();
            sb.append("Failed to send a response to the client (IOException) : tid=");
            sb.append(response.getTid());
            sb.append(", message=").append(e.getMessage());
            LOGGER.warning(sb.toString());
*/
        } finally {
            assert isHandlingRequest();
            if (readyToHandleRequest) {
                setHandlingRequest(false);
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#sendResponse(long, org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage)
	 */
    public void sendResponse(long timeout, IHTTPResponseMessage response) 
        throws InterruptedException, TimeoutException, IOException 
    {
        sendResponse(timeout, response, true);
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#sendResponse(org.eclipse.actf.util.httpproxy.core.HTTPResponseInMemoryMessage)
	 */
    public void sendResponse(IHTTPResponseMessage response)
        throws InterruptedException, IOException
    {
        try {
            sendResponse(0, response);
        } catch (TimeoutException e) {
            throw new IOException("Timeout: " + e.getMessage());
        }
    }

    private class TunnelThread extends Thread {
        private byte[] buffer;

        private static final int DEFUALT_TUNNEL_BUFFER_SIZE = 1024;

        private InputStream is;

        private OutputStream os;

        private boolean exited;

        synchronized private void exit() {
            exited = true;
            notifyAll();
        }

        synchronized public void waitExit() {
            while (true) {
                try {
                    wait();
                    if (exited) {
                        return;
                    }
                } catch (InterruptedException e) {
                }
            }
        }

        public void run() {
            try {
                while (true) {
                    try {
                        int size = is.available();
                        if (size < 0) {
                            break;
                        }
                        if (size > buffer.length) {
                            buffer = new byte[size];
                        }
                        size = is.read(buffer);
                        if (size < 0) {
                            break;
                        }
                        if (LOGGER.isDebugEnabled()) {
                            DEBUG("----TUNNEL--->\n"
                                  + ((size > 0) ? new String(buffer, 0, size) : "")
                                  +"\n----TUNNEL--->\n");
                        }
                        os.write(buffer, 0, size);
                        os.flush();
                    } catch (IOException e) {
                        break;
                    }
                }
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                }
                exit();
            }
        }

        TunnelThread(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            this.buffer = new byte[DEFUALT_TUNNEL_BUFFER_SIZE];
            this.exited = false;
        }

    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#allowTunnel(org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage, org.eclipse.actf.util.httpproxy.core.ServerConnection, long)
	 */
    public void allowTunnel(IHTTPRequestMessage req, ServerConnection sc, long timeout) throws InterruptedException, TimeoutException, IOException {
        if (LOGGER.isDebugEnabled()) {
            DEBUG("CONNECT to " + sc.toString());
        }
        try {
            sendResponse(timeout,
                         new HTTPResponseInMemoryMessage(req.getSerial(),
                                                         IHTTPHeader.HTTP_VERSION_1_0_A,
                                                         "200".getBytes(),
                                                         "OK".getBytes(),
                                                         IHTTPResponseMessage.EMPTY_BODY),
                         false);
            TunnelThread c2s = new TunnelThread(fClientIn, sc.getOutputStream());
            TunnelThread s2c = new TunnelThread(sc.getInputStream(), fClientOut);
            DEBUG("Tunnel is started.");
            c2s.start();
            s2c.start();
            c2s.waitExit();
            s2c.waitExit();
            DEBUG("Tunnel is finished.");
        } finally {
            setHandlingRequest(false);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#rejectTunnel(org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage, long)
	 */
    public void rejectTunnel(IHTTPRequestMessage req, long timeout) throws InterruptedException, TimeoutException, IOException {
        sendResponse(timeout,
                new HTTPResponseInMemoryMessage(req.getSerial(),
                        IHTTPHeader.HTTP_VERSION_1_0_A,
                        "405".getBytes(),
                        "Method Not Allowed".getBytes(),
                        IHTTPResponseMessage.EMPTY_BODY));
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IClientConnection#run()
	 */
    public void run() {
        DEBUG("Reader thread started");
        String cNameTmp;
        try {
            fLastReadTime = System.currentTimeMillis();

            setHandlingRequest(false);
            HTTPRequestMessage receivedRequest = null;
            long timeout = fKeepAlive;
            do {
                waitHandlingRequestFinish();

                // Read next request
                if (receivedRequest == null) {
                    try {
                        setHandlingRequest(true);
                        long msgSerial = fMessageSerial + 1;
                        if (msgSerial == 0) {
                            fMessageSerial = 1;
                        }
                        receivedRequest = createHTTPRequestMessage(msgSerial);
                        if (timeout > 0) {
                            DEBUG("read w/ timeout: " + timeout);
                            fReader.readMessage(receivedRequest, timeout, false);
                        } else {
                            DEBUG("read w/o timeout");
                            fReader.readMessage(receivedRequest, false);
                        }
                        fLastReadTime = System.currentTimeMillis();
                        timeout = fKeepAlive;
                        requestReceived(receivedRequest);
                        // receivedRequest.setTid(fTidGen.getID());
                        fMessageSerial = msgSerial;
                        if (LOGGER.isDebugEnabled()) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("Read a request: ").append(connectionName);
                            sb.append(", serial=").append(receivedRequest.getSerial());
                            sb.append(", tid=").append(receivedRequest.getTid());
                            DEBUG(sb.toString());
                        }
                        // receivedRequest.writeHeaders(System.out);
                    } catch (TimeoutException e) {
                        setHandlingRequest(false);
                        receivedRequest = null;
                        if (LOGGER.isDebugEnabled()) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("Request reading timeout: lastReadTime=").append(fLastReadTime);
                            sb.append(", elapsed=").append(System.currentTimeMillis() - fLastReadTime);
                            DEBUG(sb.toString());
                        }
                    }
                }

                // Send a request to a server
                if (receivedRequest != null) {
                    try {
                        fDispatcher.putRequest(receivedRequest, 10);
                        if (LOGGER.isDebugEnabled()) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("Sent a request: serial=").append(receivedRequest.getSerial());
                            sb.append(", tid=").append(receivedRequest.getTid());
                            DEBUG(sb.toString());
                        }
                        receivedRequest = null;
                    } catch (InterruptedException e) {
                        // ignore, try again in the next loop
                        if (LOGGER.isDebugEnabled()) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("Thread interrupted (Dispatcher.putRequest): serial=").append(
                                    receivedRequest.getSerial());
                            DEBUG(sb.toString());
                        }
                    } catch (TimeoutException e) {
                        // ignore, try again in the next loop
                        if (LOGGER.isDebugEnabled()) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("Queue overflow (Dispatcher.putRequest): serial=").append(
                                    receivedRequest.getSerial());
                            DEBUG(sb.toString());
                        }
                    }
                }
            } while (isHandlingRequest() || System.currentTimeMillis() - fLastReadTime < fKeepAlive);
            // WARNING("ZZZZ FINISHING: isHandlingRequest=" +
            // isHandlingRequest);
        } catch (InterruptedException interrupted) {
            // nop
            // WARNING("Interrupted");
        } catch (HTTPConnectionException e) {
        	DEBUG("The connection is closed by the peer.");
        } catch (IOException ioe) {
            // Exception from the InputStream
            if (LOGGER.isDebugEnabled()) {
                DEBUG("Client connection is lost: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        } finally {
            cNameTmp = close();
        }
        DEBUG(cNameTmp, "Reader thread stopped");
    }

    private void DEBUG(String name, String msg) {
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append(name).append(": ");
            sb.append(msg);
            LOGGER.debug(sb.toString());
        }
    }

    private final void DEBUG(String msg) {
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append(connectionName).append(": ");
            sb.append(msg);
            LOGGER.debug(sb.toString());
        }
    }

    private final void WARNING(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(connectionName).append(": ");
        sb.append(msg);
        LOGGER.warning(sb.toString());
    }
}
