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

import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServer;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServerFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoder;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoderFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverrider;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverriderFactory;
import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;

/**
 * ProxyConfig is class to configure an IHTTPProxy.
 * 
 * @see IHTTPProxy
 * @see HTTPProxyFactory
 */
public class ProxyConfig {
	private int port = 0;

	private int maxConnection = 20;

	private int timeout = 30000;

	private int keepAliveInterval = 1000;

	private int maxQueueSize = 100;

	private int keepAliveTimeoutWithoutContentLength = 10;

	private IHTTPProxyTranscoderFactory proxyTranscoderFactory = null;

	private IHTTPSessionOverriderFactory sessionOverriderFactory = null;

	private IHTTPLocalServerFactory localServerFactory = null;

	private ISecretManager secretManager = null;

	/**
	 * Constructor of ProxyConfig
	 */
	public ProxyConfig() {
	}

	/**
	 * Get port number of proxy
	 * 
	 * @return port number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set port number for the proxy
	 * 
	 * @param port
	 *            port number
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get maximum connection number of the proxy
	 * 
	 * @return maximum connection number
	 */
	public int getMaxConnection() {
		return maxConnection;
	}

	/**
	 * Set maximum connection number of the proxy
	 * 
	 * @param maxConnection
	 *            maximum connection number
	 */
	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	/**
	 * Get time out value
	 * 
	 * @return time out value
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Set time out value of the proxy
	 * 
	 * @param timeout
	 *            time out value
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Get keep alive interval value
	 * 
	 * @return keep alive interval
	 */
	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	/**
	 * Get maximum queue size
	 * 
	 * @return maximum queue size
	 */
	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	/**
	 * Get keep alive interval value for a content without content length
	 * information
	 * 
	 * @return keep alive interval
	 */
	public int getKeepAliveTimeoutWithoutContentLength() {
		return keepAliveTimeoutWithoutContentLength;
	}

	/**
	 * Get Factory of {@link IHTTPProxyTranscoder} that is used to transcode
	 * HTTP Response message
	 * 
	 * @return factory of {@link IHTTPProxyTranscoder}
	 * @see IHTTPProxyTranscoder
	 */
	public IHTTPProxyTranscoderFactory getProxyTranscoderFactory() {
		return proxyTranscoderFactory;
	}

	/**
	 * Set Factory of {@link IHTTPProxyTranscoder} that is used to transcode
	 * HTTP Response message in the proxy
	 * 
	 * @param proxyTranscoderFactory
	 *            target {@link IHTTPProxyTranscoderFactory}
	 * @see IHTTPProxyTranscoder
	 */
	public void setProxyTranscoderFactory(
			IHTTPProxyTranscoderFactory proxyTranscoderFactory) {
		this.proxyTranscoderFactory = proxyTranscoderFactory;
	}

	/**
	 * Get Factory of {@link IHTTPSessionOverrider} that is used to override
	 * HTTP Session in the proxy
	 * 
	 * @return factory of {@link IHTTPSessionOverrider}
	 * @see IHTTPSessionOverrider
	 */
	public IHTTPSessionOverriderFactory getSessionOverriderFactory() {
		return sessionOverriderFactory;
	}

	/**
	 * Set Factory of {@link IHTTPSessionOverrider} that is used to override
	 * HTTP Session in the proxy
	 * 
	 * @param sessionOverriderFactory
	 *            target {@link IHTTPSessionOverriderFactory}
	 * @see IHTTPSessionOverrider
	 */
	public void setSessionOverriderFactory(
			IHTTPSessionOverriderFactory sessionOverriderFactory) {
		this.sessionOverriderFactory = sessionOverriderFactory;
	}

	/**
	 * Get Factory of {@link IHTTPLocalServer} that enables to return HTTP
	 * Response for specified request directly from the proxy.
	 * 
	 * @return factory of {@link IHTTPLocalServer}
	 * @see IHTTPLocalServer
	 */
	public IHTTPLocalServerFactory getLocalServerFactory() {
		return localServerFactory;
	}

	/**
	 * Set Factory of {@link IHTTPLocalServer} that enables to return HTTP
	 * Response for specified request directly from the proxy.
	 * 
	 * @param localServerFactory
	 *            target {@link IHTTPLocalServerFactory}
	 * @see IHTTPLocalServer
	 */
	public void setLocalServerFactory(IHTTPLocalServerFactory localServerFactory) {
		this.localServerFactory = localServerFactory;
	}

	/**
	 * Get {@link ISecretManager} that is used for confirmation of correct
	 * server-client combination.
	 * 
	 * @return secret manager
	 */
	public ISecretManager getSecretManager() {
		return secretManager;
	}

	/**
	 * Set {@link ISecretManager} that is used for confirmation of correct
	 * server-client combination.
	 * 
	 * @param secretManager
	 *            target secret manager
	 */
	public void setSecretManager(ISecretManager secretManager) {
		this.secretManager = secretManager;
	}

}
