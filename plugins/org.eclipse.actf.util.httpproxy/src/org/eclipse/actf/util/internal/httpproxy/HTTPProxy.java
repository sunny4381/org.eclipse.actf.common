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
package org.eclipse.actf.util.internal.httpproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.eclipse.actf.util.httpproxy.ExternalProxyConfig;
import org.eclipse.actf.util.httpproxy.IHTTPProxy;
import org.eclipse.actf.util.httpproxy.ProxyConfig;
import org.eclipse.actf.util.httpproxy.core.IClientConnection;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServerFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoderFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverriderFactory;
import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.internal.httpproxy.core.ClientConnectionListener;
import org.eclipse.actf.util.internal.httpproxy.proxy.ClientStateManager;
import org.eclipse.actf.util.internal.httpproxy.proxy.HTTPProxyConnection;

/**
 * HTTPProxy is the class for the server instance of HTTP proxy.
 */
public class HTTPProxy implements ClientConnectionListener, IHTTPProxy {
	private static final Logger LOGGER = Logger.getLogger(HTTPProxy.class);

	private final IWorkpileController wpc;

	private final ServerSocket fServerSock;

	private final long fKeepAlive;

	private final int fQueueSize;

	// private final AsyncWorkManager fDispatchWorkMan;

	private final IObjectPool connectionPool;

	private final ISecretManager secretManager;

	private final IHTTPSessionOverriderFactory sessionOverriderFactory;

	private final IHTTPProxyTranscoderFactory proxyTranscoderFactory;

	private final IHTTPLocalServerFactory localServerFactory;

	private ExternalProxyConfig externalProxyConfig;

	/**
	 * 
	 * @return
	 */
	public ISecretManager getSecretManager() {
		return secretManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.IHTTPProxy#getListenPort()
	 */
	public int getListenPort() {
		return fServerSock.getLocalPort();
	}

	/**
	 * @return
	 */
	public ExternalProxyConfig getExternalProxyConfig() {
		return externalProxyConfig;
	}

	/**
	 * @return
	 */
	public IHTTPSessionOverriderFactory getSessionOverriderFactory() {
		return sessionOverriderFactory;
	}

	/**
	 * @return
	 */
	public IHTTPProxyTranscoderFactory getProxyTranscoderFactory() {
		return proxyTranscoderFactory;
	}

	/**
	 * @return
	 */
	public IHTTPLocalServerFactory getLocalServerFactory() {
		return localServerFactory;
	}

	@SuppressWarnings("nls")
	public HTTPProxy(ProxyConfig config, ExternalProxyConfig externalProxyConfig)
			throws IOException {
		Logger.setConfigPropertyName("WaXcoding.conf.logging");
		this.externalProxyConfig = externalProxyConfig;
		wpc = new WorkpileControllerImpl("WaXcoding");
		fServerSock = new ServerSocket();
		SocketAddress sa = new InetSocketAddress("localhost", config.getPort());
		// SocketAddress sa = new InetSocketAddress(localPort);
		fServerSock.bind(sa);
		fKeepAlive = config.getKeepAliveInterval();
		fQueueSize = config.getMaxQueueSize();
		connectionPool = new ObjectPoolImpl("WaXcoding-clientconnections");
		for (int i = 0; i < config.getMaxConnection(); i++) {
			IHTTPProxyConnection obj = new HTTPProxyConnection(this,
					fQueueSize, config.getTimeout());
			connectionPool.add(obj);
		}
		secretManager = config.getSecretManager();
		sessionOverriderFactory = config.getSessionOverriderFactory();
		proxyTranscoderFactory = config.getProxyTranscoderFactory();
		localServerFactory = config.getLocalServerFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.actf.util.httpproxy.IHTTPProxy#getSecret(java.lang.String,
	 * boolean)
	 */
	public String getSecret(String id, boolean remove) {
		if (null == secretManager) {
			return null;
		}
		return secretManager.getSecret(id, remove);
	}

	public synchronized int getCurrentServerGroupIndex() {
		return 0;
	}

	public void connectionClosed(IClientConnection obj) {
		connectionPool.add(obj);
	}

	private boolean exit = false;

	@SuppressWarnings("nls")
	private void startProxy() {
		LOGGER.info("Started WaXcoding, Listening port "
				+ fServerSock.getLocalPort());
		while (true) {
			Socket sock = null;
			try {
				HTTPProxyConnection connection = (HTTPProxyConnection) connectionPool
						.take(0);
				sock = fServerSock.accept();
				sock.setSoTimeout(1);
				if (exit)
					break;
				// if (sock != null) {
				connection.init(ClientStateManager.getClientStateManager(this),
						sock, fKeepAlive, getCurrentServerGroupIndex());
				wpc.input(connection);
				// }
			} catch (InterruptedException e) {
				if (exit) {
					LOGGER.info("Stopping WaXcoding...");
					break;
				}
			} catch (SocketException e) {
				// this exception is thrown when the socket is closed.
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(wpc.toString());
			}
		}
	}

	private void cleanup() {
		LOGGER.info("...done"); //$NON-NLS-1$
	}

	private class ProxyThread extends Thread {
		private boolean threadExit;

		public void exit() {
			exit = true;
			synchronized (this) {
				while (!threadExit) {
					this.interrupt();
					try {
						fServerSock.close();
						wait(1000);
					} catch (IOException e) {
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void run() {
			startProxy();
			cleanup();
			synchronized (this) {
				threadExit = true;
				notifyAll();
			}
		}

		ProxyThread() {
			super("ProxyThread"); //$NON-NLS-1$
			threadExit = false;
		}
	}

	private ProxyThread proxyThread;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.IHTTPProxy#startThread()
	 */
	public void startThread() {
		if (proxyThread != null)
			return;
		proxyThread = new ProxyThread();
		proxyThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.util.httpproxy.IHTTPProxy#stopThread()
	 */
	public void stopThread() {
		proxyThread.exit();
		proxyThread = null;
	}

	private static final String USAGE_PARAMS = " <localport>"; //$NON-NLS-1$

	private static void PRINT_USAGE(String msg) {
		if (msg != null && msg.length() > 0) {
			System.err.println(msg);
		}
		System.err.println("Usage: java " + HTTPProxy.class.getName() //$NON-NLS-1$
				+ USAGE_PARAMS);
	}

	/**
	 * A sample bootstrap code for stand-alone HTTP proxy process.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			PRINT_USAGE(null);
			System.exit(1);
		}

		int argsOffset = 0;

		int localport = Integer.parseInt(args[argsOffset++]);

		ProxyConfig config = new ProxyConfig();
		ExternalProxyConfig externalProxyConfig = new ExternalProxyConfig();

		try {
			Logger.configure();
		} catch (Exception e) {
			e.printStackTrace();
			// System.exit(1);
		}

		try {
			HTTPProxy proxy = new HTTPProxy(config, externalProxyConfig);
			proxy.startProxy();
		} catch (IOException e) {
			System.err.println("Port is in use: " + localport); //$NON-NLS-1$
			System.exit(1);
		}
	}
}
