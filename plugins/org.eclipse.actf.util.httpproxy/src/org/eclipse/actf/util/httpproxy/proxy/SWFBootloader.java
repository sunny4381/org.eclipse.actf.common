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
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.actf.util.httpproxy.Config;
import org.eclipse.actf.util.httpproxy.core.HTTPHeader;
import org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponseInMemoryMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponsePushbackMessage;
import org.eclipse.actf.util.httpproxy.core.PushbackMessageBody;
import org.eclipse.actf.util.httpproxy.util.CacheMap;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;




public class SWFBootloader {
    private static final Logger LOGGER = Logger.getLogger(SWFBootloader.class);

    private static byte[] bootLoaderSWF;

    private boolean replacedFlag;
    private boolean bootloaderRequestingFlag;

    private HTTPRequestMessage sessionRequest;

    public HTTPRequestMessage getSessionRequest() {
        return sessionRequest;
    }

    private HTTPResponseMessage sessionResponse;

    public HTTPResponseMessage getSessionResponse() {
        return sessionResponse;
    }

    private final int id;

    public SWFBootloader(int id) {
        this.id = id;
    }

    public static void setSWFBootLoaderFile(InputStream is) {
        try {
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
            bootLoaderSWF = os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CachedSlot {
        public final String uri;
        public HTTPRequestMessage request;
        public HTTPResponseMessage response;
        CachedSlot(String uri) {
            this.uri = uri;
        }
    }

    private CachedSlot sessionCachedSlot;

    private static final int MAX_REFERER_LENGTH = 128;

    private Object getCachedObject(ClientStateManager csm, String uri) {
        CacheMap map = (CacheMap) csm.get(SWFBootloader.class);
        if (map == null) {
            map = new CacheMap(128, 20);
            csm.put(SWFBootloader.class, map);
        }
        if (uri.length() > MAX_REFERER_LENGTH) {
            synchronized (map) {
                return map.matchStartsWith(uri);
            }
        } else {
            synchronized (map) {
                return map.get(uri);
            }
        }
    }

    private void cacheObject(ClientStateManager csm, String uri, Object cobj) {
        Map map = (Map) csm.get(SWFBootloader.class);
        if (map == null) {
            map = new HashMap();
            csm.put(SWFBootloader.class, map);
        }
        synchronized (map) {
            map.put(uri, cobj);
        }
    }

    private static HTTPResponseMessage bootloaderResponseMessage(HTTPRequestMessage request) {
        HTTPResponseMessage msg = new HTTPResponseInMemoryMessage(request.getSerial(),
                                                                  HTTPHeader.HTTP_VERSION_1_0_A,
                                                                  "200".getBytes(),
                                                                  "OK".getBytes(),
                                                                  bootLoaderSWF);
        msg.setHeader(HTTPHeader.CACHE_CONTROL_A, "must-revalidate".getBytes());
        msg.setHeader(HTTPHeader.CONTENT_TYPE_A, SWFUtil.MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH_A);
        return msg;
    }

    public boolean replaceRequest(ClientStateManager csm,
                                  HTTPRequestMessage request)
    	throws IOException {
        replacedFlag = false;
        if (!Config.getInstance().getSWFBootloaderFlag()) return false;
        if (request.getHeader(SWFUtil.X_FLASH_VERSION_A) == null) return false;

        String uriStr = request.getRequestURIString();
        String method = request.getMethodAsString();
        Object cobj = getCachedObject(csm, uriStr);
        if (LOGGER.isDebugEnabled()) {
            StringBuffer buf = new StringBuffer("method:");
            buf.append(method);
            buf.append(' ');
            buf.append(uriStr);
            List l = request.getHeaders();
            Iterator it = l.iterator();
            while (it.hasNext()) {
                HTTPHeader h = (HTTPHeader) it.next();
                if (!h.isRemoved()) {
                    buf.append(h.toString());
                    buf.append('\n');
                }
            }
            DEBUG(buf.toString());
        }

        if (HTTPHeader.METHOD_POST.equals(method)) {
            if (cobj instanceof CachedSlot) {
                ByteArrayOutputStream ob = new ByteArrayOutputStream();
                try {
                    request.write(0, ob);
                } catch (TimeoutException e) {
                }
                if (LOGGER.isDebugEnabled()) {
                    DEBUG("Bootloader's POST command:" + new String(ob.toByteArray()));
                }
                INFO("bootloader requests the original SWF:" + uriStr);
                this.replacedFlag = true;
                this.bootloaderRequestingFlag = true;
                this.sessionCachedSlot = (CachedSlot) cobj;
                this.sessionRequest = sessionCachedSlot.request;
                this.sessionRequest.removeHeader("If-Modified-Since".getBytes());
                this.sessionResponse = sessionCachedSlot.response;
                return true;
            }
        }

        HTTPHeader referer = request.getHeader(HTTPHeader.REFERER_A);
        if (referer != null) {
            String refStrBase;
            String uriStrBase;

            if (false) {
                refStrBase = ParseURI.eliminateQuery(new String(referer.getValue()).trim());
                uriStrBase = ParseURI.eliminateQuery(uriStr);
                System.err.println("R: " + refStrBase);
                System.err.println("U: " + uriStrBase);
            } else {
                refStrBase = new String(referer.getValue()).trim();
                uriStrBase = uriStr;
            }

            Object o = getCachedObject(csm, refStrBase);
            if (o != null) {
                INFO(uriStrBase + " seems to be referred by " + refStrBase);
                if (cobj == null) {
                    cacheObject(csm, uriStrBase, Boolean.valueOf(true));
                }
                return false;
            }
        }

        // send bootloader swf

        CachedSlot cs;
        if (cobj instanceof CachedSlot){
            cs = (CachedSlot) cobj; 
        } else {
            cs = new CachedSlot(uriStr);
            if (false) {
                cacheObject(csm, ParseURI.eliminateQuery(uriStr), cs);
            } else {
                cacheObject(csm, uriStr, cs);
            }
        }
        cs.request = request;
        /*
        INFO("bootloader is used for " + uriStr);
        HTTPResponseMessage msg = bootloaderResponseMessage(request);
        this.sessionResponse = msg;
        */
        this.replacedFlag = true;
        this.bootloaderRequestingFlag = false;
        this.sessionCachedSlot = cs;
        this.sessionRequest = request;
        this.sessionRequest.removeHeader(HTTPHeader.IF_MODIFIED_SINCE_A);
        this.sessionRequest.removeHeader(HTTPHeader.ACCEPT_ENCODING_A);
        this.sessionResponse = null;
        return true;
    }

    public boolean replaceResponse(ClientStateManager csm,
                                   HTTPRequestMessage request,
                                   HTTPResponseMessage response,
                                   int timeout) throws IOException, TimeoutException {
        if (!replacedFlag) return false;

        if (bootloaderRequestingFlag) {
            if (response != sessionCachedSlot.response) {
                response.setHeader(HTTPHeader.CACHE_CONTROL_A, "no-cache".getBytes());
            }
            this.sessionResponse = response;
            return true;
        } else {
            if (!SWFUtil.isPossiblySWFContentType(response)) return false;
            HTTPResponsePushbackMessage newResponse = new HTTPResponsePushbackMessage(response,
                                                                                      SWFUtil.FLASH_MAGIC_NUMBER_SIZE);

            PushbackMessageBody body = (PushbackMessageBody) newResponse.getMessageBody();
            if (body == null) return false;
            PushbackInputStream bodyInputStream = body.getMessageBodyPushBackInputStream();
            int version = SWFUtil.isSWF(bodyInputStream);
            INFO("The incoming SWF is version " + version);
            String uriStr = request.getRequestURIString();
            if ((version >= Config.getInstance().getSWFTranscodingMinimumVersion())
                // Flash Version 9 or later is not supported yet.
                && (version < 9)) {
                INFO("bootloader is used for " + uriStr);
                HTTPResponseMessage msg = bootloaderResponseMessage(request);
                this.sessionResponse = msg;
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                newResponse.writeBody(timeout, body, os);
                HTTPResponseMessage cachedResponse = new HTTPResponseInMemoryMessage(response,
                                                                                     os.toByteArray());
                cachedResponse.setHeader(HTTPHeader.CACHE_CONTROL_A, "no-cache,must-revalidate".getBytes());
                cachedResponse.setHeader(HTTPHeader.PRAGMA_A, "no-cache".getBytes());
                cachedResponse.setHeader(HTTPHeader.EXPIRES_A, "-1".getBytes());

                INFO("The original response is cached for later use.");
                if (LOGGER.isDebugEnabled()) {
                    StringBuffer buf = new StringBuffer("CachedMessage: ");
                    List l = cachedResponse.getHeaders();
                    Iterator it = l.iterator();
                    while (it.hasNext()) {
                        HTTPHeader h = (HTTPHeader) it.next();
                        if (!h.isRemoved()) {
                            buf.append(h.toString());
                            buf.append('\n');
                        }
                    }
                    DEBUG(buf.toString());
                }
                sessionCachedSlot.response = cachedResponse;
                return true;
            } else {
                INFO("Due to the unsupported version, the bootloader is not applied to " + uriStr);
                this.sessionResponse = newResponse;
                return true;
            }
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[id:").append(id).append("] SWFBootloader");
        return sb.toString();
    }

    private final void INFO(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString()).append(": ").append(msg);
        LOGGER.info(sb.toString());
    }

    private final void DEBUG(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(this.toString()).append(": ").append(msg);
        LOGGER.debug(sb.toString());
    }

}
