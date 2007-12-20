/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.httpproxy;

import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.actf.util.httpproxy.proxy.HTTPLocalServer;
import org.eclipse.actf.util.httpproxy.proxy.SWFBootloader;
import org.eclipse.actf.util.httpproxy.proxy.SWFTranscoder;




public class Config {
    private int port = 0;

    private boolean externalProxyFlag = false;
    private String externalProxyHost = "localhost";
    private int externalProxyPort = 8080;

    private int maxConnection = 20;

    private int timeout = 30000;

    private int keepAliveInterval = 1000;

    private int maxQueueSize = 100;

    private int keepAliveTimeoutWithoutContentLength = 10;

    private boolean swfTranscodingFlag = false;
    private int swfTranscodingMinimumVersion = 0;

    private boolean swfBootloaderFlag = true;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getExternalProxyFlag() {
        return externalProxyFlag;
    }

    public void setExternalProxyFlag(boolean flag) {
        this.externalProxyFlag = flag;
    }

    public String getExternalProxyHost() {
        return externalProxyHost;
    }

    public int getExternalProxyPort() {
        return externalProxyPort;
    }

    public void setExternalProxy(String host, int port) {
        this.externalProxyHost = host;
        this.externalProxyPort = port;
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

    public boolean getSWFTranscodingFlag() {
        return swfTranscodingFlag;
    }

    public void setSWFTranscodingFlag(boolean flag) {
        this.swfTranscodingFlag = flag;
    }

    public void setSWFBootloaderFlag(boolean swfBootloaderFlag) {
        this.swfBootloaderFlag = swfBootloaderFlag;
    }

    public boolean getSWFBootloaderFlag() {
        return swfBootloaderFlag;
    }

    public int getSWFTranscodingMinimumVersion() {
        return swfTranscodingMinimumVersion;
    }

    public void setSWFTranscodingMinimumVersion(int version) {
        this.swfTranscodingMinimumVersion = version;
    }

    public void setSWFTranscodingImposedFile(InputStream is) {
        SWFTranscoder.setSWFTranscodingImposedFile(is);
    }

    public void setSWFBootLoader(InputStream is) {
        SWFBootloader.setSWFBootLoaderFile(is);
    }

    public void setSWFBridgeInit(InputStream is) {
        HTTPLocalServer.setBridgeInitSwf(is);
    }

    static public class MIMEType {
        public final String suffix;
        public final String type;
        public final String subtype;
        MIMEType(String suffix, String type, String subtype) {
            this.suffix = suffix;
            this.type = type;
            this.subtype = subtype;
        }
    }
    private HashMap mimeTypeMap = new HashMap();

    public void addMIMEType(String suffix, String type, String subtype) {
        mimeTypeMap.put(suffix, new MIMEType(suffix, type, subtype));
    }

    public MIMEType getMIMEType(String suffix) {
        return (MIMEType) mimeTypeMap.get(suffix);
    }

    private Config() {
        addMIMEType("html", "text", "html");
        addMIMEType("htm", "text", "html");
        addMIMEType("js", "application", "x-javascript");
        addMIMEType("swf", "application", "x-shockwave-flash");
        addMIMEType("gif", "image", "gif");
        addMIMEType("jpeg", "image", "jpeg");
        addMIMEType("jpg", "image", "jpeg");
        addMIMEType("png", "image", "png");
    }
    
    // TODO!!!!
    private int swfORBPort = -1; 
    public int getSwfORBPort() {
        if (swfORBPort < 0) throw new IllegalStateException("swfORBPort is not set");
        return swfORBPort;
    }

    public void setSwfORBPort(int swfORBPort) {
        this.swfORBPort = swfORBPort; 
    }

    private static Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }
}
