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
package org.eclipse.actf.util.httpproxy.proxy.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.eclipse.actf.util.httpproxy.ExternalProxyConfig;
import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.impl.HTTPResponseInMemoryMessage;
import org.eclipse.actf.util.httpproxy.core.impl.HeaderToAdd;
import org.eclipse.actf.util.httpproxy.core.impl.RequestDispatcher;
import org.eclipse.actf.util.httpproxy.core.impl.ServerConnection;
import org.eclipse.actf.util.httpproxy.core.impl.Session;
import org.eclipse.actf.util.httpproxy.proxy.IClientStateManager;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServer;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServerFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoder;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverrider;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverriderFactory;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.ParseURI;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;

public class HTTPRequestDispatcher extends RequestDispatcher {
	private static final Logger LOGGER = Logger
			.getLogger(HTTPRequestDispatcher.class);

	private final IClientStateManager clientStateManager;

	private final HTTPProxyConnection fClient;
	
	private final ExternalProxyConfig fExProxyConf;

	private final int timeout;

	static private final int HTTP_WELL_KNOWN_PORT = 80;
	static private final int HTTPS_WELL_KNOWN_PORT = 443;

	private static class CacheConfig {
		public final String urlPattern;
		public final byte[] control;

		public CacheConfig(String urlPattern, String control) {
			this.urlPattern = urlPattern;
			this.control = control.getBytes();
		}
	}

	private static CacheConfig[] cacheConfigs = { // new
													// CacheConfig("http://disney\\.go\\.com/.*",
													// "max-age=" + 24 * 60 *
													// 60),
	new CacheConfig("http://.*\\abcnews\\.go\\.com/Video/.*", "no-cache") };

	private IHTTPResponseMessage addCacheControl(String uriStr,
			IHTTPResponseMessage response) {
		// System.err.println("U:" + uriStr);
		for (int i = 0; i < cacheConfigs.length; i++) {
			if (uriStr.matches(cacheConfigs[i].urlPattern)) {
				System.err.println("match-to:" + cacheConfigs[i].urlPattern);
				response.setHeader(IHTTPHeader.CACHE_CONTROL_A,
						cacheConfigs[i].control);
				return response;
			}
		}
		return response;
	}

	private IHTTPSessionOverrider overrider = null;
	private IHTTPLocalServer localServer = null;

	public HTTPRequestDispatcher(HTTPProxyConnection client,
			IClientStateManager clientStateManager, Socket clientSock,
			int queueSize, int timeout) {
		super("HTTPRequestDispatcher-" + clientSock.getLocalPort(),
				new Session(), client, clientSock, queueSize);
		this.fClient = client;
		this.fExProxyConf = client.getExternalProxyConfig();
		this.clientStateManager = clientStateManager;
		this.timeout = timeout;
		
		//this.localServer = new HTTPLocalServerSWF(client.getSecretManager());
		//this.overrider = new SWFBootloader(getDispatcherId());

		IHTTPLocalServerFactory localServerFactory = client.getProxy().getLocalServerFactory();
		IHTTPSessionOverriderFactory overriderFactory = client.getProxy().getSessionOverriderFactory();
		
		if(null!=localServerFactory){
			this.localServer = localServerFactory.newInstance();
		}
		if(null!=overriderFactory){
			this.overrider = overriderFactory.newInstance(getDispatcherId());
		}		
	}

	private boolean processLocalServerRequest(IHTTPRequestMessage request)
			throws InterruptedException, IOException {
		if(null==localServer){
			return false;
		}
		return localServer.processRequest(getDispatcherId(), fClient, request,
				transcoder);
	}

	private ArrayList serverConnectionCache = new ArrayList();

	private HTTPServerConnection getConnection(String host, int port) {
		HTTPServerConnection sc;
		int len = serverConnectionCache.size();
		int i = 0;
		while (i < len) {
			sc = (HTTPServerConnection) serverConnectionCache.get(i);
			if (host.equals(sc.getHost()) && (port == sc.getPort())) {
				if (sc.getStat() < 0) {
					sc.deactivate();
					sc = HTTPServerConnection.newConnection(this, host, port,
							timeout);
					serverConnectionCache.set(i, sc);
				}
				return sc;
			} else {
				if (sc.getStat() < 0) {
					sc.deactivate();
					serverConnectionCache.remove(i);
					len--;
				} else {
					i++;
				}
			}
		}
		sc = HTTPServerConnection.newConnection(this, host, port, timeout);
		serverConnectionCache.add(sc);
		return sc;
	}

	private HTTPServerConnection newConnectionForServer(
			IHTTPRequestMessage request, boolean modeConnect) {
		String host;
		int port;
		String uriStr = request.getRequestURIString();
		String authority = ParseURI.getAuthority(uriStr);
		IHTTPHeader hostHeader = request.getHeader(IHTTPHeader.HOST_A);
		if (modeConnect) {
			host = ParseURI.parseHost(uriStr);
			port = ParseURI.parsePort(uriStr, HTTPS_WELL_KNOWN_PORT);
		} else if (hostHeader != null) {
			String hostStr = new String(hostHeader.getValue());
			host = ParseURI.parseHost(hostStr);
			port = ParseURI.parsePort(hostStr, HTTP_WELL_KNOWN_PORT);
		} else if (authority != null) {
			/*
			 * URI uri = null; try { uri = new URI(uriStr); } catch
			 * (URISyntaxException e) { WARNING("URI is invalid:" + uriStr);
			 * return null; } host = uri.getHost(); port = uri.getPort();
			 */
			host = ParseURI.parseHost(authority);
			port = ParseURI.parsePort(authority, HTTP_WELL_KNOWN_PORT);

			HeaderToAdd header = new HeaderToAdd();
			// header.init(HTTPHeader.HOST, host);
			// request.addHeader(header);
			request.setHeader(IHTTPHeader.HOST_A, host.getBytes());
		} else {
			return null;
		}
		if (authority != null) {
			String absPath = ParseURI.getAbsolutePath(uriStr);
			request.setRequestURIString(absPath);
		}

		IHTTPHeader pcHeader = request.getHeader(IHTTPHeader.PROXY_CONNECTION_A);
		if (pcHeader != null) {
			pcHeader.setRemoved(true);
			request.setHeader(IHTTPHeader.CONNECTION_A, pcHeader.getValue());
		}

		IHTTPHeader pragmaHeader = request.getHeader(IHTTPHeader.PRAGMA_A);
		if (pragmaHeader != null)
			pragmaHeader.setRemoved(true);

		return getConnection(host, port);
	}

	private HTTPServerConnection newConnectionForProxy() {
		return getConnection(fExProxyConf.getExternalProxyHost(), fExProxyConf
				.getExternalProxyPort());
	}

	private IHTTPProxyTranscoder transcoder = null;

	public void setTranscoder(IHTTPProxyTranscoder transcoder) {
		this.transcoder = transcoder;
	}

	private IHTTPResponseMessage transcode(IHTTPRequestMessage request,
			IHTTPResponseMessage response) {
		if (transcoder != null) {
			return transcoder.transcode(getDispatcherId(), request, response);
		}
		return response;
	}

	private void sendGatewayTimeout(IHTTPRequestMessage request)
			throws InterruptedException, IOException {
		fClient.sendResponse(new HTTPResponseInMemoryMessage(request
				.getSerial(), IHTTPHeader.HTTP_VERSION_1_0_A, "504".getBytes(),
				"Gateway Timeout".getBytes(),
				IHTTPResponseMessage.EMPTY_BODY));
	}

	private void sendBadGateway(IHTTPRequestMessage request)
			throws InterruptedException, IOException {
		fClient.sendResponse(new HTTPResponseInMemoryMessage(request
				.getSerial(), IHTTPHeader.HTTP_VERSION_1_0_A, "502".getBytes(),
				"Bad Gateway".getBytes(),
				IHTTPResponseMessage.EMPTY_BODY));
	}

	private void sendNotFound(IHTTPRequestMessage request)
			throws InterruptedException, IOException {
		fClient
				.sendResponse(new HTTPResponseInMemoryMessage(request
						.getSerial(), IHTTPHeader.HTTP_VERSION_1_0_A, "404"
						.getBytes(), "Not Found".getBytes(),
						IHTTPResponseMessage.EMPTY_BODY));
	}

	public void run() {
		// Sender thread.
		// Pickup a message (QueuedMsg | RetryMsg) and
		// The sent request is put into the waiting queue.
		try {
			IHTTPRequestMessage request = null;
			IHTTPResponseMessage response = null;
			ServerConnection conn = null;
			while (true) {
				// Get next request
				if (request == null) {
					DEBUG("Trying to get next request");
					// Wait while (fNextMsg == null && fRetryMsg == null &&
					// fArrivedMsgs.isEmpty())
					request = getNextRequest();
				}
				assert (request != null);
				if (LOGGER.isDebugEnabled()) {
					StringBuffer sb = new StringBuffer();
					sb.append("nextRequest: msgSerial=").append(
							request.getSerial());
					sb.append(", tid=").append(request.getTid());
					DEBUG(sb.toString());
				}
				String uriStr = request.getRequestURIString();

				boolean modeConnect;
				if (IHTTPHeader.METHOD_CONNECT.equals(request
						.getMethodAsString())) {
					modeConnect = true;
				} else {
					modeConnect = false;
				}

				if (null!=overrider && overrider.replaceRequest(clientStateManager, request)) {
					request = overrider.getSessionRequest();
					response = overrider.getSessionResponse();
					if (response != null) {
						try {
							fClient.sendResponse(timeout, response);
							fClient.notifySuccessfulServerConnection(conn);
							DEBUG("SUCCESS");
						} catch (TimeoutException e) {
							sendGatewayTimeout(request);
							DEBUG("Timeout. Failed to send a SWF bootloader to the client.");
						}
						request = null;
						clearNextRequest();
						continue;
					}
				}
				if (processLocalServerRequest(request)) {
					request = null;
					clearNextRequest();
					continue;
				}

				// Send a request to a server
				if (fExProxyConf.getExternalProxyFlag()) {
					conn = newConnectionForProxy();
				} else {
					conn = newConnectionForServer(request, modeConnect);
				}
				if (conn == null) {
					request = null;
					continue;
				}

				try {
					if (LOGGER.isDebugEnabled()) {
						DEBUG("Trying to connect to " + conn);
					}
					conn.activateAndConnect(timeout);
					if (LOGGER.isDebugEnabled()) {
						DEBUG("Trying to send a request to " + conn);
					}
					if (conn.getStat() == ServerConnection.STAT_CONNECTED) {
						if (modeConnect
								&& !fExProxyConf.getExternalProxyFlag()) {
							fClient.allowTunnel(request, conn, timeout);
							clearNextRequest();
							request = null;
							conn.deactivate();
							continue;
						}
						startSessionAndSendRequest(request, conn, timeout);
					} else {
						fClient.sendResponse(new HTTPResponseInMemoryMessage(
								request.getSerial(),
								IHTTPHeader.HTTP_VERSION_1_0_A,
								"503".getBytes(), "Service Unavailable"
										.getBytes(),
								IHTTPResponseMessage.EMPTY_BODY));
						continue;
					}
					DEBUG("Sent a request to " + conn);
				} catch (TimeoutException timeout) {
					DEBUG("sendRequest() timeout, try again");
					sendGatewayTimeout(request);
					conn.deactivate();
					continue;
				} catch (IOException ioe) {
					DEBUG("connection closed");
					sendNotFound(request);
					break;
				}

				// Get a response from a server, and send it back to the client.
				if (LOGGER.isDebugEnabled()) {
					DEBUG("Trying to receive a response from " + conn);
				}

				try {
					response = fSession.getResponse(timeout);
					response = addCacheControl(uriStr, response);
					if (response != null) {
						if (modeConnect) {
							if ("200".equals(response.getStatusCodeAsString())) {
								fClient.allowTunnel(request, conn, timeout);
							} else {
								fClient.sendResponse(0, response);
							}
						} else {
							IHTTPResponseMessage newResponse = transcode(
									request, response);
							if (null!=overrider && overrider.replaceResponse(
									clientStateManager, request, response,
									timeout)) {
								newResponse = overrider
										.getSessionResponse();
							}
							fClient.sendResponse(timeout, newResponse);
						}

						request = null;
						clearNextRequest();
						fClient.notifySuccessfulServerConnection(conn);

						if (LOGGER.isDebugEnabled()) {
							StringBuffer sb = new StringBuffer("SUCCESS\n");
							sb.append("Received a response from ").append(conn);
							DEBUG(sb.toString());
						}
					}
				} catch (TimeoutException e) {
					sendGatewayTimeout(request);
					if (LOGGER.isDebugEnabled()) {
						StringBuffer sb = new StringBuffer();
						sb
								.append(
										"Timeout. Failed to send/receive a message to a server (")
								.append(conn);
						sb.append(") Try another server.");
						DEBUG(sb.toString());
					}
					// TODO
				} catch (IOException e) {
					if (fClient.isHandlingRequest()) {
						sendBadGateway(request);
					}
					throw e;
				}
				// conn.deactivate();
			}
		} catch (InterruptedException interrupted) {
			// nop
			DEBUG("Interrupted");
		} catch (IOException e) {
			if (LOGGER.isDebugEnabled()) {
				DEBUG("RequestDispatcher is stopped by IOException.");
				e.printStackTrace();
			}
			// fClient.resetConnection();
		} finally {
			int len = serverConnectionCache.size();
			for (int i = 0; i < len; i++) {
				HTTPServerConnection c = (HTTPServerConnection) serverConnectionCache
						.get(i);
				c.deactivate();
			}
			DEBUG("RequestDispatcher stopped");
		}
	}

	public void close() {
		super.close();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		// sb.append("HTTPRequestDispatcher-").append(getDispatcherId());
		sb.append("[id:").append(getDispatcherId()).append(
				"] HTTPRequestDispatcher");
		return sb.toString();
	}

	protected final void DEBUG(String msg) {
		if (LOGGER.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append(this.toString()).append(": ").append(msg);
			LOGGER.debug(sb.toString());
		}
	}

	protected final void WARNING(String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.toString()).append(": ").append(msg);
		LOGGER.warning(sb.toString());
	}

	protected final void INFO(String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.toString()).append(": ").append(msg);
		LOGGER.info(sb.toString());
	}
}
