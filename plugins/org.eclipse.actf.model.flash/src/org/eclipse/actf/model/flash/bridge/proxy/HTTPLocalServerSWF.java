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

package org.eclipse.actf.model.flash.bridge.proxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;
import org.eclipse.actf.util.httpproxy.core.IHTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.IHTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServer;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyConnection;
import org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoder;
import org.eclipse.actf.util.httpproxy.proxy.ISecretManager;
import org.eclipse.actf.util.httpproxy.util.HTTPUtil;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.ParseURI;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;

public class HTTPLocalServerSWF implements IHTTPLocalServer {
    private static final boolean LOCAL_FILE_ACCESS = false;

    private static final int HTTP_WELL_KNOWN_PORT = 80;
    private static final byte[] MIME_TYPE_APPLICATION_XML_A = "application/xml".getBytes();

    private static final Logger LOGGER = Logger.getLogger(HTTPLocalServerSWF.class);

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

	private static HashMap mimeTypeMap = new HashMap();

	public static void addMIMEType(String suffix, String type, String subtype) {
		mimeTypeMap.put(suffix, new MIMEType(suffix, type, subtype));
	}

	public static MIMEType getMIMEType(String suffix) {
		return (MIMEType) mimeTypeMap.get(suffix);
	}    
    
    private static byte[] bridgeInitSwf;

    public static void setBridgeInitSwf(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int b;
        try {
            while (true) {
                b = is.read();
                if (b < 0) break;
                os.write(b);
            }
        } catch (IOException e) {
            return;
        }
        bridgeInitSwf = os.toByteArray();
    }

    private IHTTPResponseMessage processBridgeInitSwf(IHTTPRequestMessage request) {
        IHTTPResponseMessage response = HTTPUtil.createHTTPResponseInMemoryMessage(request.getSerial(),
                                                                       IHTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(),
                                                                       bridgeInitSwf);
        response.setHeader(IHTTPHeader.CONTENT_TYPE_A,
                           SWFUtil.MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH_A);
        return response;
    }


    private IHTTPResponseMessage processLoadVarsForSwf(IHTTPRequestMessage request, ISecretManager secretManager) {
        IHTTPResponseMessage response = HTTPUtil.createHTTPResponseInMemoryMessage(request.getSerial(),
                                                                       IHTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(),
                                                                       secretManager.requestSecret());
        response.setHeader(IHTTPHeader.CONTENT_TYPE_A,
                           SWFUtil.MIME_TYPE_APPLICATION_X_WWW_FORM_URLENCODED_A);
        return response;
    }

    private IHTTPResponseMessage processSwfCrossDomainPolicyFile(IHTTPRequestMessage request) {
        //int port = SwfORBFactory.getInstance().getListenPort();
        LOGGER.info("Request crossdomain.xml policy file.");
        int port = getSwfORBPort();
        /*
        String contents = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\""
            + port + "\"/></cross-domain-policy>\n";
        */
        String contents = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<cross-domain-policy><allow-access-from domain=\"*\"/></cross-domain-policy>\n";
        IHTTPResponseMessage response = HTTPUtil.createHTTPResponseInMemoryMessage(request.getSerial(),
                                                                       IHTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(),
                                                                       contents.getBytes());
        response.setHeader(IHTTPHeader.CONTENT_TYPE_A, MIME_TYPE_APPLICATION_XML_A);
        return response;
    }

    private IHTTPResponseMessage processLocalFile(int id,
                                                 IHTTPRequestMessage request,
                                                 String absPath,
                                                 IHTTPProxyTranscoder transcoder) {
        try {
            absPath = URLDecoder.decode(absPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        LOGGER.info("Local Server Mode. Send:" + absPath);

        File f = new File(absPath);
        if (!f.canRead()) {
            return null;
        }

        int len = (int) f.length();
        byte[] contents = new byte[len];
        FileInputStream is = null;
        try {
            is = new FileInputStream(f);
            is.read(contents, 0, len);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
        }

        IHTTPResponseMessage response = HTTPUtil.createHTTPResponseInMemoryMessage(request.getSerial(),
                                                                       IHTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(), contents);
        int dotPos = absPath.lastIndexOf(".");
        if (dotPos > 0) {
            int sPos = absPath.lastIndexOf("/");
            if (dotPos > sPos) {
                String suffix = absPath.substring(dotPos + 1);
                MIMEType mt = getMIMEType(suffix);
                if (mt != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(mt.type).append('/').append(mt.subtype);
                    response.setHeader(IHTTPHeader.CONTENT_TYPE_A, sb.toString().getBytes());
                }
            }
        }

        if (transcoder != null) {
            response = transcoder.transcode(id, request, response);
        }
        return response;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.proxy.IHTTPLocalServer#processRequest(int, org.eclipse.actf.util.httpproxy.proxy.HTTPProxyConnection, org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage, org.eclipse.actf.util.httpproxy.proxy.IHTTPProxyTranscoder)
	 */
    public boolean processRequest(int id,
                                  IHTTPProxyConnection fClient,
                                  IHTTPRequestMessage request,
                                  IHTTPProxyTranscoder transcoder)
        throws InterruptedException, IOException {
        if (!IHTTPHeader.METHOD_GET.equals(request.getMethodAsString()))
            return false;
        String uriStr = request.getRequestURIString();
        String authority = ParseURI.getAuthority(uriStr);
        String absPath = ParseURI.getAbsolutePath(uriStr);

        String hostStr;
        String host;
        int port;
        if (authority != null) {
            hostStr = authority;
        } else {
            IHTTPHeader hostHeader = request.getHeader(IHTTPHeader.HOST_A);
            if (hostHeader == null)
                return false;
            hostStr = new String(hostHeader.getValue());
        }
        host = ParseURI.parseHost(hostStr);
        port = ParseURI.parsePort(hostStr, HTTP_WELL_KNOWN_PORT);

        IHTTPResponseMessage response = null;
        if (absPath.endsWith(SWFUtil.BRIDGE_INIT_SWF_FILENAME)) {
            response = processBridgeInitSwf(request);
        } else if (absPath.endsWith(SWFUtil.LOADVARS_PROPERTY_FILENAME)) {
            response = processLoadVarsForSwf(request, fClient.getSecretManager());
        } else {
            if (!("localhost".equals(host)))
                return false;
            if ((port == HTTP_WELL_KNOWN_PORT) && ("/crossdomain.xml".equals(absPath))) {
                response = processSwfCrossDomainPolicyFile(request);
            } else if (LOCAL_FILE_ACCESS) {
                if (port != fClient.getListenPort())
                    return false;
                response = processLocalFile(id, request, absPath, transcoder);
            }
        }
        if (response == null) {
            HTTPUtil.sendFailedToClient(fClient, request);
            return true;
        }

        try {
            fClient.sendResponse(0, response);
        } catch (TimeoutException e) {
            LOGGER.fatal("Timeout in local server mode", e);
        }
        return true;
    }

	// TODO!!!!
	private int swfORBPort = -1;

	public int getSwfORBPort() {
		if (swfORBPort < 0)
			throw new IllegalStateException("swfORBPort is not set");
		return swfORBPort;
	}

	public void setSwfORBPort(int swfORBPort) {
		this.swfORBPort = swfORBPort;
	}

    
    public HTTPLocalServerSWF() {        
		addMIMEType("html", "text", "html");
		addMIMEType("htm", "text", "html");
		addMIMEType("js", "application", "x-javascript");
		addMIMEType("swf", "application", "x-shockwave-flash");
		addMIMEType("gif", "image", "gif");
		addMIMEType("jpeg", "image", "jpeg");
		addMIMEType("jpg", "image", "jpeg");
		addMIMEType("png", "image", "png");

    }
}
