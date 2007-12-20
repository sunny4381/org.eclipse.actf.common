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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;



public abstract class ServerConnection implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ServerConnection.class);

    private static class SocketOpener implements Runnable {
        private ServerConnection fSocketReceiver;

        private String fHost;

        private int fPort;

        private int fSOTimeout;

        private boolean isValid;

        SocketOpener(String host, int port, int soTimeout, ServerConnection socketReceiver) {
            fHost = host;
            fPort = port;
            fSOTimeout = soTimeout;
            fSocketReceiver = socketReceiver;
            isValid = true;
        }

        synchronized void setValid(boolean v) {
            isValid = v;
        }

        synchronized boolean isValid() {
            return isValid;
        }

        public void run() {
            fSocketReceiver.DEBUG("SocketOpener started");
            try {
                Socket sock = null;
                OutputStream out = null;
                InputStream in = null;
                if (isValid()) {
                    if (LOGGER.isDebugEnabled()) {
                        fSocketReceiver.DEBUG("Trying to create a Socket: " + fHost + "@" + fPort);
                    }
                    sock = new Socket(fHost, fPort);
                    // sock = new Socket();
                    // sock.setSoTimeout((int) fSOTimeout);
                    // sock.setTcpNoDelay(true);
                    // SocketAddress dest = new
                    // InetSocketAddress(fInfo.getHost(), fInfo.getPort());
                    // sock.connect(dest, (int) fSOTimeout);
                    sock.setSoTimeout(1);
                    out = sock.getOutputStream();
                    in = sock.getInputStream();
                    if (LOGGER.isDebugEnabled()) {
                        fSocketReceiver.DEBUG("Created a Socket: " + fHost + "@" + fPort);
                    }
                }
                if (sock != null && out != null && in != null && isValid()) {
                    if (LOGGER.isDebugEnabled()) {
                        fSocketReceiver.DEBUG("Set a Socket to the ServerConnection: " + fHost + "@" + fPort);
                    }
                    fSocketReceiver.notifyConnected(sock, out, in);
                }
            } catch (IOException e) {
                // e.printStackTrace();
                if (LOGGER.isDebugEnabled()) {
                    fSocketReceiver.DEBUG("Failed to create a Socket (" + e.getClass().getName() + "): " + fHost + "@" + fPort);
                }
                if (isValid()) {
                    fSocketReceiver.notifyConnectFailed(e);
                }
            }
        }
    }

    private static class Status {
        private int fStat = STAT_INIT;

        private int fNumWaiters = 0;

        Status() {
            // nop
        }

        synchronized void set(int stat) {
            fStat = stat;
            this.notifyAll();
        }

        synchronized int get() {
            return fStat;
        }

        synchronized boolean equals(int stat) {
            return (fStat == stat);
        }

        synchronized boolean waitFor(int stat, long timeout)
            throws InterruptedException, TimeoutException {
            if (fStat == stat) return true;
            if (fStat == STAT_CLOSED) return false;

            fNumWaiters += 1;
            long deadline = System.currentTimeMillis() + timeout;
            long wait = timeout;
            while (wait > 0) {
                this.wait(wait);
                if (fStat == stat) return true;
                if (fStat == STAT_CLOSED) return false;
                wait = deadline - System.currentTimeMillis();
            }
            throw new TimeoutException();
        }

        public String toString() {
            switch (fStat) {
            case STAT_CLOSED:
                return "CLOSED";
            case STAT_INIT:
                return "INIT";
            case STAT_CONNECTING:
                return "CONNECTING";
            case STAT_CONNECTED:
                return "CONNECTED";
            default:
                return "Unkown";
            }
        }
    }

    public static final int STAT_CLOSED = -2;

    public static final int STAT_FINALIZING = -1;

    public static final int STAT_INIT = 0;

    public static final int STAT_CONNECTING = 1;

    public static final int STAT_CONNECTED = 2;

    private final ServerKey fKey;

    private final String name;

    private final String host;

    public String getHost() {
        return host;
    }

    private final int port;

    public int getPort() {
        return port;
    }

    private final RequestDispatcher fDispatcher;

    private final int fRetryTime;

    private final int timeout;

    private long fActivatedTime;

    private Thread fThread;

    private SocketOpener fSocketOpener = null;

    private Thread fSocketOpenerThread = null;

    private HTTPRequestMessage fRequest;

    private long fFirstTimeout = 0;

    private Socket fSocket = null;

    private IOException fSocketException = null;

    private InputStream fInputStream = null;

    InputStream getInputStream() {
        return fInputStream;
    }

    private BufferedOutputStream fOutputStream = null;

    OutputStream getOutputStream() {
        return fOutputStream;
    }

    private HTTPResponseReader fReader = null;

    private long fMessageSerial;

    private Status fStat = new Status();

    /**
     * 
     */
    protected ServerConnection(String name, String host, int port, int group, int index, RequestDispatcher dispatcher,
            int retryTime, int timeout) {
        this.name = name;
        this.host = host;
        this.port = port;
        fKey = new ServerKey(group, index);
        fRetryTime = retryTime;
        fDispatcher = dispatcher;
        this.timeout = timeout;
        fRequest = null;
        fMessageSerial = 0;
    }

    protected abstract HTTPResponseMessage createHTTPResponseMessage(long msgSerial);
    
    public synchronized void reset() {
        deactivate();
        setStat(STAT_INIT);
    }

    public ServerKey getKey() {
        return fKey;
    }

    public synchronized boolean isActive() {
        return (fThread != null);
    }

    // synchronized
    public synchronized void activate() {
        if (isActive()) {
            DEBUG("activate: already active");
            return;
        }
        DEBUG("activate");
        setStat(STAT_CONNECTING);
        fActivatedTime = System.currentTimeMillis();
        fMessageSerial = 0;
        setTimeout(false);
        fThread = new Thread(this, "ServerConnection-" + name);
        fThread.start();

        fSocket = null;
        fSocketException = null;
        fOutputStream = null;
        fReader = null;

        fSocketOpener = new SocketOpener(host, port, (int) timeout * 2, this);
        fSocketOpenerThread = new Thread(fSocketOpener, "SocketOpener-" + this.toString());
        fSocketOpenerThread.start();
    }

    public void activateAndConnect(long timeout) throws IOException, TimeoutException, InterruptedException {
        activate();
        waitUntilConnected(timeout);
    }

    public void waitUntilConnected(long timeout) throws IOException, TimeoutException, InterruptedException {
        DEBUG("waitUntilConnected");
        int stat;
        synchronized (fStat) {
            if (fStat.equals(STAT_CONNECTING)) {
                fStat.waitFor(STAT_CONNECTED, timeout);
            }
            stat = fStat.get();
        }
        if (stat == STAT_CONNECTED) {
            DEBUG("connected");
            return;
        } else if (stat == STAT_CONNECTING) {
            throw new TimeoutException("ServerConnection.waitUntilConnected");
        } else if (stat == STAT_INIT) {
            throw new IOException("This connection is not activated yet");
        } else if (stat == STAT_CLOSED) {
            throw new IOException("This connection is already closed");
        }
    }

    /**
     * Deactivate this connection.
     */
    public synchronized void deactivate() {
        DEBUG("deactivate");
        if (!isActive()) {
            DEBUG("deactivate: already deactive");
            return;
        }

        // Stop the thread
        try {
            if (fSocket != null && !fSocket.isOutputShutdown()) {
                fSocket.shutdownOutput();
            }
        } catch (IOException e) {
            // ignore
            WARNING("Failed to shutdown a socket (IOException): " + e.getMessage());
        }
        fThread.interrupt();
        fThread = null;

        fSocketOpener.setValid(false);
        fSocketOpenerThread.interrupt();

        fSocketOpener = null;
        fSocketOpenerThread = null;
        fSocket = null;
        fOutputStream = null;
        fReader = null;

        fActivatedTime = 0;
        fMessageSerial = 0;
        setTimeout(false);
        setStat(STAT_CLOSED);
    }

    synchronized void notifyConnected(Socket sock, OutputStream out, InputStream in) {
        if (sock == null) {
            throw new IllegalArgumentException("null");
        }
        DEBUG("setSocket");
        fSocket = sock;
        fSocketException = null;
        fOutputStream = new BufferedOutputStream(new SocketTimeoutRetryOutputStream(out));
        fInputStream = in;
        fReader = fDispatcher.createHTTPResponseReader(in);
        setStat(STAT_CONNECTED);
    }

    synchronized void notifyConnectFailed(IOException e) {
        WARNING("setSocketException: failed to create a socket (IOException): " + e.getMessage());
        fSocket = null;
        fSocketException = e;
        fOutputStream = null;
        fReader = null;
        deactivate();
    }

    public synchronized void setTimeout(boolean timeout) {
        if (timeout) {
            if (fFirstTimeout == 0) {
                fFirstTimeout = System.currentTimeMillis();
            }
        } else {
            fFirstTimeout = 0;
        }
    }

    private synchronized boolean isTimeout() {
        return (fFirstTimeout != 0);
    }

    public synchronized boolean isInvalid() {
        if (fFirstTimeout == 0) {
            return false;
        }
        return (System.currentTimeMillis() - fFirstTimeout < fRetryTime);
    }

    public int getStat() {
        return fStat.get();
    }

    private void setStat(int stat) {
        fStat.set(stat);
    }

    // synchronized
    private boolean startSession(long msgSerial, long timeout) throws TimeoutException, InterruptedException {
        if (fMessageSerial != 0) {
            if (msgSerial == fMessageSerial) {
                // Avoid sending the same message
                return false;
            }
            // There is a message whose response is not yet arrived.
            // Wait until a response arrives, or timeout.
            long deadline = System.currentTimeMillis() + timeout;
            long wait = timeout;
            while (wait > 0) {
                this.wait(wait); // <--
                if (fMessageSerial == 0) {
                    break;
                }
                wait = deadline - System.currentTimeMillis();
            }
            if (fMessageSerial != 0) {
                throw new TimeoutException("ServerConnection.startSession");
            }
        }
        // (fMessageSerial == 0) && (fMessageSerial != msgSerial)
        fMessageSerial = msgSerial;
        if (LOGGER.isDebugEnabled()) {
            DEBUG("Session started: msgSerial=" + fMessageSerial);
        }
        return true;
    }

    private synchronized void finishSession() {
        if (LOGGER.isDebugEnabled()) {
            DEBUG("Session finished: msgSerial=" + fMessageSerial);
        }
        assert (fMessageSerial != 0);
        fMessageSerial = 0; // -->startSession
        this.notifyAll();
    }

    /**
     * Send a message asynchronously.
     * 
     * @param req
     * @param timeout
     * @return
     */
    public synchronized void putRequest(HTTPRequestMessage req, long timeout)
        throws TimeoutException, InterruptedException
    {
        if (LOGGER.isDebugEnabled()) {
            DEBUG("putRequest: msgSerial=" + req.getSerial() + ", tid=" + req.getTid());
        }
        boolean sessionStarted = startSession(req.getSerial(), timeout);
        assert (req.getSerial() == fMessageSerial);
        if (sessionStarted) {
            // fMessageSerial != null
            fRequest = req; // -->nextRequest
            this.notifyAll();
        }
    }

    /*
      public synchronized HTTPResponseMessage getResponse(long timeout) 
      throws TimeoutException, InterruptedException 
      {
      while (fResponse == null) {
      this.wait();
      }
      HTTPResponseMessage response = fResponse;
      fResponse = null;
      fMessageSerial = 0;  //-->startSession (putRequest) 
      this.notifyAll();
      return response;
      }
    */
        
    private synchronized HTTPRequestMessage nextRequest() throws InterruptedException {
        while (fRequest == null || fMessageSerial == 0) {
            if (LOGGER.isDebugEnabled()) {
                DEBUG("nextRequest waiting: request=" + fRequest + ", msgSerial=" + fMessageSerial);
            }
            this.wait();
        }
        // (fRequest != null) && (fMessageSerial != 0)
        if (LOGGER.isDebugEnabled()) {
            DEBUG("nextRequest waiting done: request=" + fRequest + ", msgSerial=" + fMessageSerial);
        }
        HTTPRequestMessage req = fRequest;
        fRequest = null;
        return req;
    }

    // run()
    private HTTPResponseMessage receiveResponse(long timeout, boolean isBodyEmpty) throws InterruptedException,
            IOException, TimeoutException {
        HTTPResponseReader reader;
        long msgSerial;
        synchronized (this) {
            assert (fStat.equals(STAT_CONNECTED)) && (fReader != null) && (fMessageSerial != 0);
            if (fReader == null || fMessageSerial == 0) {
                throw new IOException("Deactivated");
            }
            reader = fReader;
            msgSerial = fMessageSerial;
        }

        // if (fMessageSerial == 0) {
        // long start = System.currentTimeMillis();
        // long deadline = start + timeout;
        // long wait = timeout;
        // while (wait > 0) {
        // this.wait(wait);
        // if (fMessageSerial != 0) {
        // break;
        // }
        // wait = deadline - System.currentTimeMillis();
        // }
        // timeout -= System.currentTimeMillis() - start;
        // }
        // if (fMessageSerial == 0 || timeout <= 0) {
        // throw new TimeoutException("ServerConnection.receiveResponse");
        // }
        // (fMessageSerial != 0) && (timeout > 0)

        HTTPResponseMessage response = createHTTPResponseMessage(msgSerial);
        DEBUG("Try to read response...");
        reader.readMessage(response, timeout, isBodyEmpty);
        
        //              if (LOGGER.isDebugEnabled()) {
        //                      DEBUG("Received a response: msgSerial=" + response.getSerial());
        //                      System.out.println("#######Response from server group " + fGroup.getIndex());
        //                      try { response.write(System.out); } catch (Exception e) {}
        //                      System.out.println("####################################################################");
        //              }
        //              fInfo.decrementProcessingRequest();
        //              if (response.getFFFResultCode() == FFFServletHeader.RC_SUCCESS) {
        //                      fInfo.updateLastActivityTime();
        //              }
        return response;
    }
        
    private synchronized void sendRequest(HTTPRequestMessage request,
                                          long timeout)
        throws IOException, InterruptedException, TimeoutException {
        if (LOGGER.isDebugEnabled()) {
            DEBUG("sendRequest....");
        }
        long t0 = System.currentTimeMillis();
        waitUntilConnected(timeout);
        assert (fOutputStream != null);
        long t1 = System.currentTimeMillis();
        timeout -= t1 - t0;
        if (timeout <= 0) {
            throw new TimeoutException(); 
        }

        if (LOGGER.isDebugEnabled()) {
            DEBUG("Sent a request: msgSerial=" + request.getSerial() + ", tid=" + request.getTid());
            StringBuffer sb = new StringBuffer();
            ByteArrayOutputStream ob = new ByteArrayOutputStream();
            BifurcatedOutputStream o = new BifurcatedOutputStream(fOutputStream, ob);
            request.write(timeout, o);
            ob.close();
            sb.append("\n===============>====>====>====>=============>\n");
            sb.append(ob.toString());
            sb.append("===============<====<====<====<=============<\n");
            DEBUG(sb.toString());
        } else {
            request.write(timeout, fOutputStream);
        }
        fOutputStream.flush();

        fMessageSerial = request.getSerial();

        // fInfo.incrementProcessingRequest();
    }

    /**
     * Reciever a response from the server. Keep trying to read a response and
     * pass it to the dispatcher.
     */
    public void run() {
        DEBUG("receiver thread started");
        // long lastActivityTime = System.currentTimeMillis();
        try {
            boolean serverError = false;
            int counter = 0;
            while (!Thread.interrupted() && !serverError) {
                counter += 1;
                HTTPRequestMessage request = nextRequest();

                // Send a request to the server
                try {
                    if (request.isConnectionShutdownRequired()) {
                        request.setConnectionHeader(false);
                        sendRequest(request, timeout);
                        fSocket.shutdownOutput();
                        setStat(STAT_FINALIZING);
                    } else {
                        request.setConnectionHeader(true);
                        sendRequest(request, timeout);
                    }
                    // fInfo.notifySuccessfulSend();
                    // lastActivityTime = System.currentTimeMillis();
                } catch (TimeoutException timeout) {
                    DEBUG("sendRequest() timeout");
                    setTimeout(true);
                    continue;
                }

                assert (fStat.equals(STAT_CONNECTED) || fStat.equals(STAT_FINALIZING));

                // Recieve a resposne from the server
                int loop = 0;
                long start = System.currentTimeMillis();
                while (!Thread.currentThread().isInterrupted() && !serverError) {
                    try {
                        DEBUG("ReceiveResponse...");
                        HTTPResponseMessage response = receiveResponse(timeout, request.isResponseBodyEmpty());

                        // Send the response to the client
                        DEBUG("ResponseArrived...");
                        if (response.isConnectionToBeClosed()) {
                            DEBUG("Since the response is not keep-alive, we do not reuse this connection.");
                            setStat(STAT_FINALIZING);
                        }
                        fDispatcher.responseArrived(this, response); // Interrupted
                        finishSession();

                        break;
                    } catch (TimeoutException timeout) {
                        // continue;
                        // setTimeout(true);
                        if (LOGGER.isDebugEnabled()) {
                            DEBUG("response receiving timeout (" + timeout.getMessage() + "): retry=" + loop
                                    + ", elapsed=" + (System.currentTimeMillis() - start));
                        }
                    }
                    loop += 1;
                    long delay = System.currentTimeMillis() - start;
                    if (delay > 60 * 1000) {
                        throw new IOException("Shutdown this connection");
                    }
                }
            }
        } catch (HTTPConnectionException e) {
        	DEBUG("The connection is closed by the peer.");
        } catch (IOException e) {
            DEBUG("IOException: " + e.getMessage());
            HTTPMalformedResponseMessage response = new HTTPMalformedResponseMessage(fMessageSerial, e);
            try {
                fDispatcher.responseArrived(this, response);
            } catch (InterruptedException interrupted) {
            }
            finishSession();
        } catch (InterruptedException e) {
            DEBUG("reader thread interrupted");
            // ignore
        } finally {
            DEBUG("receiver thread stopped");
            deactivate();
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(host).append(':').append(port).append('.').append(fKey.toString());
        return sb.toString();
    }

    private final void DEBUG(String msg) {
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append(name).append(" [");
            sb.append(this.toString()).append("] ").append(msg);
            LOGGER.debug(sb.toString());
        }
    }

    private final void WARNING(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(name).append(" [");
        sb.append(this.toString()).append("] ").append(msg);
        LOGGER.warning(sb.toString());
    }

    public synchronized String dump() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString()).append(": stat=").append(fStat);
        sb.append(", thread=");
        sb.append((fThread == null) ? "null" : Boolean.toString(fThread.isAlive()));
        sb.append(", socketOpener=");
        sb.append((fSocketOpener == null) ? "null" : "exists");
        sb.append(", socketOpenerThread=");
        sb.append((fSocketOpenerThread == null) ? "null" : Boolean.toString(fSocketOpenerThread.isAlive()));
        sb.append(", socket=");
        sb.append((fSocket == null) ? "null" : Boolean.toString(fSocket.isConnected()));
        sb.append(", invalid=").append(isInvalid());
        return sb.toString();
    }
}
