/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.proxy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.bridge.WaXcodingPlugin;
import org.eclipse.actf.model.flash.proxy.internal.INTERNET_PER_CONN_OPTION;
import org.eclipse.actf.model.flash.proxy.internal.INTERNET_PER_CONN_OPTION_LIST;
import org.eclipse.actf.model.flash.proxy.internal.WSTR;
import org.eclipse.actf.model.flash.proxy.internal.WinInet;
import org.eclipse.actf.model.flash.proxy.logs.ProxyLogHandler;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.internal.win32.OS;




public class ProxyManager {

	public static boolean initLogger = false;
	public static boolean forceProxy = false;
	private static String noProxy = Messages.getString("NO_PROXY"); //$NON-NLS-1$
    
	static {
		String[] temp = Platform.getApplicationArgs();
		for( int i=0; i<temp.length; i++ ) {
			if( "-initLogger".equals(temp[i]) ) { //$NON-NLS-1$
				initLogger = true;
			}
			else if( "-forceProxy".equals(temp[i]) ) { //$NON-NLS-1$
				forceProxy = true;
			}
			else if( "-noProxy".equals(temp[i]) ) { //$NON-NLS-1$
				if( i < temp.length-1 ) {
					String nextArg = temp[i+1];
					if( nextArg.startsWith("|") && nextArg.endsWith("|") ) { //$NON-NLS-1$ //$NON-NLS-2$
						noProxy = nextArg;
					}
				}
			}
		}
        if( Platform.inDebugMode() ) {
            System.out.println("initLogger = "+initLogger); //$NON-NLS-1$
            System.out.println("forceProxy = "+forceProxy); //$NON-NLS-1$
            System.out.println("noProxy = "+noProxy); //$NON-NLS-1$
        }
	}

    private INTERNET_PER_CONN_OPTION_LIST savedList;
    private int savedAddress;
    private int hSession;
    private boolean needRestore = false;
    private int currentPort = 0;
    private static String PROTCOL_HTTP =   "http"; //$NON-NLS-1$
    private static String PROTCOL_HTTPS =  "https"; //$NON-NLS-1$
    private static String PROTCOL_FTP =    "ftp"; //$NON-NLS-1$
    private static String PROTCOL_GOPHER = "gopher"; //$NON-NLS-1$
//    private static String[] PROTCOL_EXTRA = new String[]{"https","ftp","gopher"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    
    public ProxyManager() {
        // Save default settings
        savedList = new INTERNET_PER_CONN_OPTION_LIST(4);
        savedList.perConnOptions[0].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_FLAGS;
        savedList.perConnOptions[1].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_PROXY_SERVER;
        savedList.perConnOptions[2].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_PROXY_BYPASS;
        savedList.perConnOptions[3].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_AUTOCONFIG_URL;
        savedAddress = OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, INTERNET_PER_CONN_OPTION_LIST.sizeof);
        savedList.getData(savedAddress);
        int[] pInt = new int[]{INTERNET_PER_CONN_OPTION_LIST.sizeof};
        WinInet.InternetQueryOptionW(0,WinInet.INTERNET_OPTION_PER_CONNECTION_OPTION,savedAddress,pInt);
        savedList.setData(savedAddress);
        WSTR wsAgent = new WSTR("Agent"); //$NON-NLS-1$
        hSession = WinInet.InternetOpenW(wsAgent.getAddress(),WinInet.INTERNET_OPEN_TYPE_DIRECT,0,0,0);
        wsAgent.dispose();
    }
    
    public void startProxy(int port, int swfVersion, int timeout,
                           boolean swfBootloader, boolean swfTranscoder) {
        IWaXcoding waXcoding = getIWaXcoding();
        waXcoding.setPort(port);
        waXcoding.setSWFTranscodingMinimumVersion(swfVersion);
        waXcoding.setTimeout(timeout);
        waXcoding.setExternalProxyFlag(false);
        waXcoding.setSWFBootloaderFlag(swfBootloader);
        waXcoding.setSWFTranscodingFlag(swfTranscoder);
        String extProxy = getDefaultProxy(PROTCOL_HTTP);
        if( null != extProxy ) {
            int pos = extProxy.indexOf(':');
            if( -1 != pos ) {
                try {
                    String extHost = extProxy.substring(0,pos);
                    String portStr = extProxy.substring(pos+1);
                    int extPort = Integer.parseInt(portStr);
                    waXcoding.setExternalProxyFlag(true);
                    waXcoding.setExternalProxy(extHost,extPort);
                    if( Platform.inDebugMode() ) {
                        System.out.println("Using external proxy "+extHost+":"+extPort); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } catch( Exception e ) {
                }
            }
        }
        ProxyLogHandler.configure();      // Configure our log handler
        waXcoding.start(initLogger);           // Start WaXcoding without log handler
        if( !initLogger ) {
        	ProxyLogHandler.resetLogLevel();
        }
        currentPort = waXcoding.getPort();
    }
    
    public void stopProxy() {
        if( 0 != currentPort ) {
            getIWaXcoding().stop();
            currentPort = 0;
        }
    }
    
    public void setInternetOptions(boolean isGlobal) {
        if( 0 != savedAddress ) {
            if( 0 == currentPort ) {
                invokeSetOption(hSession,savedAddress);
                invokeSetOption(0,savedAddress);
                needRestore = false;
            }
            else {
//                INTERNET_PER_CONN_OPTION_LIST list = new INTERNET_PER_CONN_OPTION_LIST(3);
//                list.perConnOptions[0].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_FLAGS;
//                list.perConnOptions[0].dwValue=INTERNET_PER_CONN_OPTION.PROXY_TYPE_PROXY | INTERNET_PER_CONN_OPTION.PROXY_TYPE_DIRECT;
//                list.perConnOptions[1].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_PROXY_SERVER;
//                list.perConnOptions[1].strValue.setString(getProxyString());
//                list.perConnOptions[2].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_PROXY_BYPASS;
//                list.perConnOptions[2].strValue.setString(null);
                INTERNET_PER_CONN_OPTION_LIST list = new INTERNET_PER_CONN_OPTION_LIST(2);
                list.perConnOptions[0].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_FLAGS;
                list.perConnOptions[0].dwValue=INTERNET_PER_CONN_OPTION.PROXY_TYPE_AUTO_PROXY_URL | INTERNET_PER_CONN_OPTION.PROXY_TYPE_DIRECT;
                list.perConnOptions[1].dwOption=INTERNET_PER_CONN_OPTION.INTERNET_PER_CONN_AUTOCONFIG_URL;
                list.perConnOptions[1].strValue.setString(getAutoConfigURL(isGlobal||forceProxy));
                int address = OS.GlobalAlloc(OS.GMEM_FIXED | OS.GMEM_ZEROINIT, INTERNET_PER_CONN_OPTION_LIST.sizeof);
                list.getData(address);
                invokeSetOption(hSession, address);
                invokeSetOption(0, isGlobal ? address : savedAddress);
                needRestore = isGlobal;
                OS.GlobalFree(address);
                list.dispose();
            }
        }
    }
    
    public void dispose() {
        ProxyLogHandler.removeHandler();
        stopProxy();
        if( 0 != hSession ) {
            WinInet.InternetCloseHandle(hSession);
        }
        if( 0 != savedAddress ) {
            if( needRestore ) {
                WinInet.InternetSetOptionW(0,WinInet.INTERNET_OPTION_PER_CONNECTION_OPTION,savedAddress,INTERNET_PER_CONN_OPTION_LIST.sizeof);
            }
            OS.GlobalFree(savedAddress);
            savedList.dispose();
        }
    }

    private void invokeSetOption(int handle, int address) {
        WinInet.InternetSetOptionW(handle,WinInet.INTERNET_OPTION_PER_CONNECTION_OPTION,address,INTERNET_PER_CONN_OPTION_LIST.sizeof);
        WinInet.InternetSetOptionW(handle,WinInet.INTERNET_OPTION_SETTINGS_CHANGED,0,0);
        WinInet.InternetSetOptionW(handle,WinInet.INTERNET_OPTION_REFRESH,0,0);
    }
    
//    private String getProxyString() {
//        String result = PROTCOL_HTTP+"=localhost:"+currentPort; //$NON-NLS-1$
//        for( int i=0; i<PROTCOL_EXTRA.length; i++ ) {
//            String s = getDefaultProxy(PROTCOL_EXTRA[i]);
//            if( null!=s && s.length()>0 ) {
//                result += ";"+PROTCOL_EXTRA[i]+"="+s; //$NON-NLS-1$ //$NON-NLS-2$
//            }
//        }
//        return result;
//    }
    
    private String getDefaultProxy(String protocol) {
        if( 0 != (savedList.perConnOptions[0].dwValue & INTERNET_PER_CONN_OPTION.PROXY_TYPE_PROXY) ) {
            String defaultProxy = savedList.perConnOptions[1].strValue.getString();
            if( null != defaultProxy ) {
                if( -1 == defaultProxy.indexOf('=') ) {
                    return defaultProxy;
                }
                Matcher m = Pattern.compile(protocol+"=(.+)").matcher(defaultProxy); //$NON-NLS-1$
                if( m.find() ) {
                    String s = m.group(1);
                    if( null != s ) {
                        int sep = s.indexOf(';');
                        return -1==sep ? s : s.substring(0,sep);
                    }
                }
            }
        }
        return null;
    }
    
    private IWaXcoding getIWaXcoding() {
        return WaXcodingPlugin.getDefault().getIWaXcoding();
    }
    
    private String getAutoConfigURL(boolean disableAutoProxy) {
		String pac = readPluginFile("templates/proxy.pac"); //$NON-NLS-1$
        if( null != pac ) {
        	String httpProxy = getProxyString("localhost:"+currentPort); //$NON-NLS-1$
        	pac = pac.replaceAll("\\$NOPROXY_EXTS\\$",  disableAutoProxy ? "" : noProxy) //$NON-NLS-1$ //$NON-NLS-2$
		        	 .replaceAll("\\$HTTP_PROXY\\$",    httpProxy) //$NON-NLS-1$
		        	 .replaceAll("\\$HTTP_DIRECT\\$",   getProxyString(getDefaultProxy(PROTCOL_HTTP))) //$NON-NLS-1$
		        	 .replaceAll("\\$HTTPS_DIRECT\\$",  getProxyString(getDefaultProxy(PROTCOL_HTTPS))) //$NON-NLS-1$
		        	 .replaceAll("\\$FTP_DIRECT\\$",    getProxyString(getDefaultProxy(PROTCOL_FTP))) //$NON-NLS-1$
		        	 .replaceAll("\\$GOPHER_DIRECT\\$", getProxyString(getDefaultProxy(PROTCOL_GOPHER))); //$NON-NLS-1$
//            System.out.println(pac);
        	OutputStream os = null;
        	try {
                IPath pacPath = ProxyPlugin.getDefault().getStateLocation().append("proxy.pac"); //$NON-NLS-1$
                os = new FileOutputStream(pacPath.toFile());
    			OutputStreamWriter writer = new OutputStreamWriter(os, "utf-8"); //$NON-NLS-1$
    			writer.write(pac);
    			writer.close();
                return "file://"+pacPath.toString(); //$NON-NLS-1$
        	}
        	catch( Exception e ) {
        		e.printStackTrace();
        	}
        	finally {
        		if( null != os ) {
    				try {
    					os.close();
    				} 
    				catch (IOException e) {
    				}
        		}
        	}
        }
    	return null;
    }
    
    private static String getProxyString(String defaultProxy) {
    	return null != defaultProxy ? MessageFormat.format("PROXY {0}; DIRECT", new Object[]{defaultProxy}) : "DIRECT"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private static String readPluginFile(String path) {
    	InputStream is = null;
    	try {
        	URL url = FileLocator.resolve(ProxyPlugin.getDefault().getBundle().getEntry(path));
			is = url.openStream();
			Reader reader = new InputStreamReader(is);
			StringBuffer sb = new StringBuffer();
			char[] buff = new char[4096];
			int result;
			while( -1 != (result=reader.read(buff)) ) {
				sb.append(buff, 0, result);
			}
			return sb.toString();
    	}
    	catch( Exception e ) {
    		e.printStackTrace();
    	}
    	finally {
    		if( null != is ) {
				try {
					is.close();
				} 
				catch (IOException e) {
				}
    		}
    	}
    	return null;
    }
}
