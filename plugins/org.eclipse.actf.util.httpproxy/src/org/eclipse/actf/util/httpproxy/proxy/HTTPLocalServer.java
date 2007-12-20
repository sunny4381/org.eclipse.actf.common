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

package org.eclipse.actf.util.httpproxy.proxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.eclipse.actf.util.httpproxy.Config;
import org.eclipse.actf.util.httpproxy.core.HTTPHeader;
import org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponseInMemoryMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;




public class HTTPLocalServer {
    private static final boolean LOCAL_FILE_ACCESS = false;

    private static final int HTTP_WELL_KNOWN_PORT = 80;
    private static final byte[] MIME_TYPE_APPLICATION_XML_A = "application/xml".getBytes();

    private static final Logger LOGGER = Logger.getLogger(HTTPLocalServer.class);

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

    private HTTPResponseMessage processBridgeInitSwf(HTTPRequestMessage request) {
        HTTPResponseMessage response = new HTTPResponseInMemoryMessage(request.getSerial(),
                                                                       HTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(),
                                                                       bridgeInitSwf);
        response.setHeader(HTTPHeader.CONTENT_TYPE_A,
                           SWFUtil.MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH_A);
        return response;
    }

    private final SWFSecretManager secretManager;

    private HTTPResponseMessage processLoadVarsForSwf(HTTPRequestMessage request) {
        HTTPResponseMessage response = new HTTPResponseInMemoryMessage(request.getSerial(),
                                                                       HTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(),
                                                                       secretManager.requestSecret());
        response.setHeader(HTTPHeader.CONTENT_TYPE_A,
                           SWFUtil.MIME_TYPE_APPLICATION_X_WWW_FORM_URLENCODED_A);
        return response;
    }

    private HTTPResponseMessage processSwfCrossDomainPolicyFile(HTTPRequestMessage request) {
        //int port = SwfORBFactory.getInstance().getListenPort();
        LOGGER.info("Request crossdomain.xml policy file.");
        int port = Config.getInstance().getSwfORBPort();
        /*
        String contents = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\""
            + port + "\"/></cross-domain-policy>\n";
        */
        String contents = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<cross-domain-policy><allow-access-from domain=\"*\"/></cross-domain-policy>\n";
        HTTPResponseMessage response = new HTTPResponseInMemoryMessage(request.getSerial(),
                                                                       HTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(),
                                                                       contents.getBytes());
        response.setHeader(HTTPHeader.CONTENT_TYPE_A, MIME_TYPE_APPLICATION_XML_A);
        return response;
    }

    private HTTPResponseMessage processLocalFile(int id,
                                                 HTTPRequestMessage request,
                                                 String absPath,
                                                 HTTPProxyTranscoder transcoder) {
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

        HTTPResponseMessage response = new HTTPResponseInMemoryMessage(request.getSerial(),
                                                                       HTTPHeader.HTTP_VERSION_1_0_A,
                                                                       "200".getBytes(),
                                                                       "OK".getBytes(), contents);
        int dotPos = absPath.lastIndexOf(".");
        if (dotPos > 0) {
            int sPos = absPath.lastIndexOf("/");
            if (dotPos > sPos) {
                String suffix = absPath.substring(dotPos + 1);
                Config.MIMEType mt = Config.getInstance().getMIMEType(suffix);
                if (mt != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(mt.type).append('/').append(mt.subtype);
                    response.setHeader(HTTPHeader.CONTENT_TYPE_A, sb.toString().getBytes());
                }
            }
        }

        if (transcoder != null) {
            response = transcoder.transcode(id, request, response);
        }
        return response;
    }

    public boolean processRequest(int id,
                                  HTTPProxyConnection fClient,
                                  HTTPRequestMessage request,
                                  HTTPProxyTranscoder transcoder)
        throws InterruptedException, IOException {
        if (!HTTPHeader.METHOD_GET.equals(request.getMethodAsString()))
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
            HTTPHeader hostHeader = request.getHeader(HTTPHeader.HOST_A);
            if (hostHeader == null)
                return false;
            hostStr = new String(hostHeader.getValue());
        }
        host = ParseURI.parseHost(hostStr);
        port = ParseURI.parsePort(hostStr, HTTP_WELL_KNOWN_PORT);

        HTTPResponseMessage response = null;
        if (absPath.endsWith(SWFUtil.BRIDGE_INIT_SWF_FILENAME)) {
            response = processBridgeInitSwf(request);
        } else if (absPath.endsWith(SWFUtil.LOADVARS_PROPERTY_FILENAME)) {
            response = processLoadVarsForSwf(request);
        } else {
            if (!("localhost".equals(host)))
                return false;
            if ((port == HTTP_WELL_KNOWN_PORT) && ("/crossdomain.xml".equals(absPath))) {
                response = processSwfCrossDomainPolicyFile(request);
            } else if (LOCAL_FILE_ACCESS) {
                if (port != fClient.getProxy().getListenPort())
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

    HTTPLocalServer(SWFSecretManager secretManager) {
        this.secretManager = secretManager;
    }
}
