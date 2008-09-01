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
package org.eclipse.actf.util.httpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import org.eclipse.actf.util.httpproxy.core.IClientConnection;
import org.eclipse.actf.util.httpproxy.core.impl.ClientConnectionListener;
import org.eclipse.actf.util.httpproxy.internal.IObjectPool;
import org.eclipse.actf.util.httpproxy.internal.IWorkpileController;
import org.eclipse.actf.util.httpproxy.internal.ObjectPoolImpl;
import org.eclipse.actf.util.httpproxy.internal.WorkpileControllerImpl;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServerFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoderFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverriderFactory;
import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;
import org.eclipse.actf.util.httpproxy.proxy.impl.ClientStateManager;
import org.eclipse.actf.util.httpproxy.proxy.impl.HTTPProxyConnection;
import org.eclipse.actf.util.httpproxy.util.Logger;

public class HTTPProxy implements ClientConnectionListener {
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

	public ISecretManager getSecretManager() {
		return secretManager;
	}

	public int getListenPort() {
		return fServerSock.getLocalPort();
	}
	
	public ExternalProxyConfig getExternalProxyConfig(){
		return externalProxyConfig;
	}
		
	public IHTTPSessionOverriderFactory getSessionOverriderFactory() {
		return sessionOverriderFactory;
	}

	public IHTTPProxyTranscoderFactory getProxyTranscoderFactory() {
		return proxyTranscoderFactory;
	}	

	public IHTTPLocalServerFactory getLocalServerFactory() {
		return localServerFactory;
	}

	private HTTPProxy(ProxyConfig config, ExternalProxyConfig externalProxyConfig) throws IOException {
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
			IHTTPProxyConnection obj = new HTTPProxyConnection(this, fQueueSize,
					config.getTimeout());
			connectionPool.add(obj);
		}
		secretManager = config.getSecretManager();
		sessionOverriderFactory = config.getSessionOverriderFactory();
		proxyTranscoderFactory = config.getProxyTranscoderFactory();
		localServerFactory = config.getLocalServerFactory();
	}

	public String getSecret(String id, boolean remove) {
		if(null==secretManager){
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
				if (sock != null) {
					connection.init(ClientStateManager
							.getClientStateManager(this), sock, fKeepAlive,
							getCurrentServerGroupIndex());
					wpc.input(connection);
				}
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
		LOGGER.info("...done");
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
			super("ProxyThread");
			threadExit = false;
		}
	}

	private ProxyThread proxyThread;

	public void startThread() {
		if (proxyThread != null)
			return;
		proxyThread = new ProxyThread();
		proxyThread.start();
	}

	public void stopThread() {
		proxyThread.exit();
		proxyThread = null;
	}

	private static final String USAGE_PARAMS = " <localport>";

	private static void PRINT_USAGE(String msg) {
		if (msg != null && msg.length() > 0) {
			System.err.println(msg);
		}
		System.err.println("Usage: java " + HTTPProxy.class.getName()
				+ USAGE_PARAMS);
	}

	public static HTTPProxy newProxy(ProxyConfig config, ExternalProxyConfig externalProxyConfig, String logName,
			InputStream configIS) {
		try {
			Logger.configure(logName, configIS);
		} catch (Exception e) {
		}
		try {
			HTTPProxy proxy = new HTTPProxy(config, externalProxyConfig);
			return proxy;
		} catch (IOException e) {
			return null;
		}
	}

	/**
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
//		WaXcodingConfig waxConfig = WaXcodingConfig.getInstance();
		
		try {
			Logger.configure("HTTPProxy");
		} catch (Exception e) {
			e.printStackTrace();
			// System.exit(1);
		}

//		try {
//			waxConfig.setSWFBootloaderFlag(false);
//			final File imposedSWFPath = new File("bridgeSWF/imposed.swf");
//			SWFTranscoder.setSWFTranscodingImposedFile(new FileInputStream(
//					imposedSWFPath));
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//			System.exit(1);
//		}

		try {
			HTTPProxy proxy = new HTTPProxy(config, externalProxyConfig);
			proxy.startProxy();
		} catch (IOException e) {
			System.err.println("Port is in use: " + localport);
			System.exit(1);
		}
	}
}
