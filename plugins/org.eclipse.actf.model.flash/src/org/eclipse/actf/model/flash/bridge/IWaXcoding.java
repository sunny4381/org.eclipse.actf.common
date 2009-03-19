/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA -initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.bridge;

import java.io.InputStream;

import org.eclipse.actf.model.flash.transcoder.ISwfTranscoder;
import org.eclipse.actf.util.httpproxy.ExternalProxyConfig;
import org.eclipse.actf.util.httpproxy.ProxyConfig;
import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;

/**
 * Interface to manage Proxy for Flash bridge.
 */
public interface IWaXcoding {
	/**
	 * Start proxy
	 * 
	 * @param initLogger
	 *            if true, start logging together
	 * @return true ifsucceeded
	 */
	boolean start(boolean initLogger);

	/**
	 * Stop proxy
	 * 
	 * @return true if succeeded
	 */
	boolean stop();

	/**
	 * Get secret String that is used for confirmation of correct server-client
	 * combination
	 * 
	 * @param id
	 *            target key to obtain the secret
	 * @param remove
	 *            if true, the secret will be removed after invoking this method
	 * @return secret, or null if the corresponding value is not available for
	 *         the target key
	 * @see ISecretManager
	 */
	String getSecret(String id, boolean remove);

	/**
	 * Set proxy port
	 * 
	 * @param port
	 *            target port number
	 * @see ProxyConfig#setPort(int)
	 */
	void setPort(int port);

	/**
	 * Get proxy port
	 * 
	 * @return port number
	 */
	int getPort();

	/**
	 * Set boolean value to specify if HTTPProxy needs to use an external proxy
	 * 
	 * @param flag
	 *            true to use external proxy
	 * @see ExternalProxyConfig
	 */
	void setExternalProxyFlag(boolean flag);

	/**
	 * Set external proxy information
	 * 
	 * @param host
	 *            proxy host
	 * @param port
	 *            proxy port
	 * @see ExternalProxyConfig#setExternalProxy(String, int)
	 */
	void setExternalProxy(String host, int port);

	/**
	 * Set maximum connection number of the proxy
	 * 
	 * @param connections
	 *            maximum connection number
	 * @see ProxyConfig#setMaxConnection(int)
	 */
	void setMaxConnection(int connections);

	/**
	 * Set time out value of the proxy
	 * 
	 * @param timeout
	 *            time out value
	 * @see ProxyConfig#setTimeout(int)
	 */
	void setTimeout(int timeout);

	/**
	 * Set boolean value to specify if Proxy needs to use SWF boot loader
	 * 
	 * @param flag
	 *            true to use boot loader
	 */
	void setSWFBootloaderFlag(boolean flag);

	/**
	 * Set {@link InputStream} of SWF boot loader
	 * 
	 * @param is
	 *            target {@link InputStream}
	 */
	void setSWFBootloader(InputStream is);

	/**
	 * Set {@link InputStream} of SWF bridge
	 * 
	 * @param is
	 *            target {@link InputStream}
	 */
	void setSWFBridgeInit(InputStream is);

	/**
	 * Set boolean value to specify if Proxy needs to use {@link ISwfTranscoder}
	 * 
	 * @param flag
	 *            true to use boot loader
	 */
	void setSWFTranscodingFlag(boolean flag);

	/**
	 * Set minimum version of target SWF content
	 * 
	 * @param version
	 *            minimum version of target SWF content
	 */
	void setSWFTranscodingMinimumVersion(int version);

	/**
	 * Set {@link InputStream} for Object that will be imposed by
	 * {@link ISwfTranscoder}
	 * 
	 * @param is
	 *            target {@link InputStream}
	 * @see ISwfTranscoder#impose(Object, Object)
	 */
	void setSWFTranscodingImposedFile(InputStream is);

	void setSWFBootloaderV9(InputStream is);

	void setSWFBridgeInitV9(InputStream is);

}
