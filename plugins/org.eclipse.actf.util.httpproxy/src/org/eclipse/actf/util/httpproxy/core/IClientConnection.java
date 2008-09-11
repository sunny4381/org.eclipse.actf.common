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

public interface IClientConnection {

	public abstract void resetConnection();

	public abstract String close();

	public abstract Socket getClientSocket();

	public abstract int getCurrentServerGroupIndex();

	public abstract boolean isHandlingRequest();

	public abstract void sendResponse(long timeout,
			IHTTPResponseMessage response, boolean readyToHandleRequest)
			throws InterruptedException, IOException, TimeoutException;

	public abstract void sendResponse(long timeout, IHTTPResponseMessage response)
			throws InterruptedException, TimeoutException, IOException;

	public abstract void sendResponse(IHTTPResponseMessage response)
			throws InterruptedException, IOException;

	public abstract void allowTunnel(IHTTPRequestMessage req,
			ServerConnection sc, long timeout) throws InterruptedException,
			TimeoutException, IOException;

	public abstract void rejectTunnel(IHTTPRequestMessage req, long timeout)
			throws InterruptedException, TimeoutException, IOException;

	/**
	 * Continuously read requests from the client and put them into the send
	 * queue of the dispatcher.
	 */
	public abstract void run();

}