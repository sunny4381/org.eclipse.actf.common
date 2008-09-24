/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
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
import java.net.Socket;

import org.eclipse.actf.util.httpproxy.util.TimeoutException;
import org.eclipse.actf.util.internal.httpproxy.core.ServerConnection;

/**
 * A connection with client. 
 */
public interface IClientConnection {

	/**
	 * Reset the connection.
	 */
	public abstract void resetConnection();

	/**
	 * Close the connection.
	 * @return name of this connection which can be used for debug messages
	 */
	public abstract String close();

	/**
	 * Get a socket object for connecting with the client.
	 * @return a Socket object
	 */
	public abstract Socket getClientSocket();

	/**
	 * TODO: Fill javadoc comment
	 * @return
	 */
	public abstract int getCurrentServerGroupIndex();

	/**
	 * Returns whether this connection is handling a request but a response is not sent yet.
	 * @return true if this connection is handling a request
	 */
	public abstract boolean isHandlingRequest();

	/**
	 * Send a HTTP response message to the client.
	 * @param timeout Number of milliseconds to wait until the response is sent to the client, or 0 if it does not need to timeout 
	 * @param response a HTTP response message
	 * @param readyToHandleRequest true if "handlingRequest" status needs to be set to false after the response is sent successfully
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws TimeoutException if it failed to send the response within the specified time
	 * @see #isHandlingRequest()
	 */
	public abstract void sendResponse(long timeout,
			IHTTPResponseMessage response, boolean readyToHandleRequest)
			throws InterruptedException, IOException, TimeoutException;

	/**
	 * Send a HTTP response message to the client.
	 * The "isHandlingRequest" status will be set to false after the response is sent successfully.
	 * @param timeout Number of milliseconds to wait until the response is sent to the client, or 0 if it does not need to timeout 
	 * @param response a HTTP response message
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws TimeoutException if it failed to send the response within the specified time
	 * @see #isHandlingRequest()
	 */
	public abstract void sendResponse(long timeout, IHTTPResponseMessage response)
			throws InterruptedException, IOException, TimeoutException; 

	/**
	 * Send a HTTP response message to the client.
	 * This method will be blocked until it sends the response (no timeout).
	 * The "isHandleRequest" status will be set to false after the response is sent successfully.
	 * @param response a HTTP response message
	 * @throws InterruptedException
	 * @throws IOException
	 * @see #isHandlingRequest()
	 */
	public abstract void sendResponse(IHTTPResponseMessage response)
			throws InterruptedException, IOException;

	/**
	 * TODO: Fill javadoc comment
	 * @param req
	 * @param sc
	 * @param timeout
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public abstract void allowTunnel(IHTTPRequestMessage req,
			ServerConnection sc, long timeout) throws InterruptedException,
			TimeoutException, IOException;

	/**
	 * TODO: Fill javadoc comment
	 * @param req
	 * @param timeout
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public abstract void rejectTunnel(IHTTPRequestMessage req, long timeout)
			throws InterruptedException, TimeoutException, IOException;

	/**
	 * Continuously read requests from the client and put them into the send
	 * queue of the dispatcher.
	 */
	public abstract void run();

}