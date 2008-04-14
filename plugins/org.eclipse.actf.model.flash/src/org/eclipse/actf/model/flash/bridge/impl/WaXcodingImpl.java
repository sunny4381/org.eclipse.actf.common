/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Kentarou FUKUDA -initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.bridge.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.bridge.proxy.HTTPLocalServerSWFFactory;
import org.eclipse.actf.model.flash.bridge.proxy.ProxyTranscoderSWFFactory;
import org.eclipse.actf.model.flash.bridge.proxy.SWFBootloaderFactory;
import org.eclipse.actf.model.flash.bridge.proxy.SWFSecretManager;
import org.eclipse.actf.util.httpproxy.ExternalProxyConfig;
import org.eclipse.actf.util.httpproxy.HTTPProxy;
import org.eclipse.actf.util.httpproxy.ProxyConfig;


public class WaXcodingImpl implements IWaXcoding {
    private HTTPProxy proxy;
    private ProxyConfig proxyConfig;
    private ExternalProxyConfig exProxyConfig;
    private WaXcodingConfig waxConfig;
    
    private static final String logConfig = 
        "handlers = java.util.logging.ConsoleHandler, java.util.logging.FileHandler\n"
        + "java.util.logging.ConsoleHandler.level=INFO\n"
        + "java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter\n"
        + "java.util.logging.FileHandler.level=FINEST\n"
        + "java.util.logging.FileHandler.pattern=c:/waxcoding.log\n"
        + "java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter\n";

    public boolean start(boolean initLogger) {
        if (proxy != null) return false;
        ByteArrayInputStream is = null;
        if (initLogger) {
            is = new ByteArrayInputStream(logConfig.getBytes());
        }
        
        proxy = HTTPProxy.newProxy(proxyConfig, exProxyConfig, "WaXcoding", is);
        if (proxy == null) return false;
        proxy.startThread();
        return true;
    }

    public boolean stop() {
        if (proxy != null) {
            proxy.stopThread();
            proxy = null;
        }
        return true;
    }

    public String getSecret(String id, boolean remove) {
        return proxy.getSecret(id, remove);
    }

    public void setPort(int port) {
        proxyConfig.setPort(port);
    }

    public int getPort() {
        if (proxy != null) {
            return proxy.getListenPort();
        } else {
            return proxyConfig.getPort();
        }
    }

    public void setExternalProxyFlag(boolean flag) {
    	exProxyConfig.setExternalProxyFlag(flag);
    }

    public void setExternalProxy(String host, int port) {
    	exProxyConfig.setExternalProxy(host, port);
    }

    public void setSWFTranscodingFlag(boolean flag) {
    	waxConfig.setSWFTranscodingFlag(flag);
    }

    public void setSWFTranscodingMinimumVersion(int version) {
    	waxConfig.setSWFTranscodingMinimumVersion(version);
    }

    public void setSWFTranscodingImposedFile(InputStream is) {
    	waxConfig.setSWFTranscodingImposedFile(is);
    }

    public void setMaxConnection(int connections) {
    	proxyConfig.setMaxConnection(connections);
    }

    public void setTimeout(int timeout) {
    	proxyConfig.setTimeout(timeout);
    }

    private WaXcodingImpl() {
    	proxyConfig = new ProxyConfig();
    	exProxyConfig = new ExternalProxyConfig();
    	waxConfig = WaXcodingConfig.getInstance();

    	proxyConfig.setProxyTranscoderFactory(new ProxyTranscoderSWFFactory());
    	proxyConfig.setSessionOverriderFactory(new SWFBootloaderFactory());
    	proxyConfig.setLocalServerFactory(new HTTPLocalServerSWFFactory());
    	proxyConfig.setSecretManager(new SWFSecretManager());
    }

    private static final IWaXcoding instance = new WaXcodingImpl();

    public static IWaXcoding getInstance() {
        return instance;
    }

    public void setSWFBootloaderFlag(boolean flag) {
    	waxConfig.setSWFBootloaderFlag(flag);
    }

    public void setSWFBootloader(InputStream is) {
    	waxConfig.setSWFBootLoader(is);
    }

    public void setSWFBridgeInit(InputStream is) {
    	waxConfig.setSWFBridgeInit(is);

    }
    
}
