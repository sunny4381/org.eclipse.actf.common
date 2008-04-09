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

import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServerFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoderFactory;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPSessionOverriderFactory;
import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;


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

	public ProxyConfig() {
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	public int getKeepAliveTimeoutWithoutContentLength() {
		return keepAliveTimeoutWithoutContentLength;
	}
	
	public IHTTPProxyTranscoderFactory getProxyTranscoderFactory() {
		return proxyTranscoderFactory;
	}

	public void setProxyTranscoderFactory(
			IHTTPProxyTranscoderFactory proxyTranscoderFactory) {
		this.proxyTranscoderFactory = proxyTranscoderFactory;
	}

	public IHTTPSessionOverriderFactory getSessionOverriderFactory() {
		return sessionOverriderFactory;
	}

	public void setSessionOverriderFactory(IHTTPSessionOverriderFactory sessionOverriderFactory) {
		this.sessionOverriderFactory = sessionOverriderFactory;
	}

	public IHTTPLocalServerFactory getLocalServerFactory() {
		return localServerFactory;
	}

	public void setLocalServerFactory(IHTTPLocalServerFactory localServerFactory) {
		this.localServerFactory = localServerFactory;
	}

	public ISecretManager getSecretManager() {
		return secretManager;
	}

	public void setSecretManager(ISecretManager secretManager) {
		this.secretManager = secretManager;
	}
	
	
}
