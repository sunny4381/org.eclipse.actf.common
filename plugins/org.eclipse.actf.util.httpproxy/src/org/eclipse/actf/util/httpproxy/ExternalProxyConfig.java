/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
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

/**
 * ExternalProxyConfig is class to configure an external Proxy that used by
 * IHTTPProxy.
 * 
 * @see IHTTPProxy
 * @see HTTPProxyFactory
 */
public class ExternalProxyConfig {
	private boolean externalProxyFlag = false;
	private String externalProxyHost = "localhost";
	private int externalProxyPort = 8080;

	/**
	 * Constructor of the class
	 */
	public ExternalProxyConfig() {

	}

	/**
	 * Check if an external proxy is used for HTTPProxy
	 * 
	 * @return true if need to use external proxy. Default value is false
	 */
	public boolean getExternalProxyFlag() {
		return externalProxyFlag;
	}

	/**
	 * Set boolean value to specify if HTTPProxy needs to use an external proxy
	 * 
	 * @param flag
	 *            true if need to use external proxy
	 */
	public void setExternalProxyFlag(boolean flag) {
		this.externalProxyFlag = flag;
	}

	/**
	 * Get external proxy host URL in String
	 * 
	 * @return external proxy host URL. Default value is "localhost"
	 */
	public String getExternalProxyHost() {
		return externalProxyHost;
	}

	/**
	 * Get external proxy port number
	 * 
	 * @return external proxy port number. Default value is "8080"
	 */
	public int getExternalProxyPort() {
		return externalProxyPort;
	}

	/**
	 * Set external proxy host and port
	 * 
	 * @param host
	 *            URL of target external proxy host
	 * @param port
	 *            port number of target external proxy
	 */
	public void setExternalProxy(String host, int port) {
		this.externalProxyHost = host;
		this.externalProxyPort = port;
	}

}
