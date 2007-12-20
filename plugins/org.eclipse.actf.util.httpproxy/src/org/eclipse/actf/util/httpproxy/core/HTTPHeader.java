/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

import java.io.IOException;
import java.io.OutputStream;

public abstract class HTTPHeader {
    public static byte toUpcase(byte b) {
        if ((b >= 'a') && (b <= 'z')) {
            return (byte) (b - ('a' - 'A'));
        }
        return b;
    }

    public static boolean compare(byte[] v1, byte[] v2) {
        int len = v1.length;
        if (len != v2.length) {
            return false;
        }
        for (int i = len - 1; i >= 0; i--) {
            if (v1[i] != v2[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareIgnoreCase(byte[] v1, byte[] v2) {
        int len = v1.length;
        if (len != v2.length) {
            return false;
        }
        for (int i = len - 1; i >= 0; i--) {
            if (HTTPHeader.toUpcase(v1[i]) != HTTPHeader.toUpcase(v2[i])) {
                return false;
            }
        }
        return true;
    }

    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_TRACE = "TRACE";
    public static final String METHOD_CONNECT = "CONNECT";
        
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String AGE = "Age";
    public static final String ALLOW = "Allow";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_MD5 = "Content-MD5";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String DATE = "Date";
    public static final String EXPECT = "Expect";
    public static final String EXPIRES = "Expires";
    public static final String FROM = "From";
    public static final String HOST = "Host";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String LOCATION = "Location";
    public static final String MAX_FORWARDS = "Max-Forwards";
    public static final String PRAGMA = "Pragma";
    public static final String PROXY_CONNECTION = "Proxy-Connection";
    public static final String RANGE = "Range";
    public static final String REFERER = "Referer";
    public static final String RETRY_AFTER = "Retry-After";
    public static final String SERVER = "Server";
    public static final byte[] SERVER_A = SERVER.getBytes();
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String UPGRADE = "Upgrade";
    public static final String USER_AGENT = "User-Agent";
    public static final String VARY = "Vary";
    public static final String VIA = "Via";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    public static final String HTTP_VERSION_1_0 = "HTTP/1.0";
    public static final byte[] HTTP_VERSION_1_0_A = HTTP_VERSION_1_0.getBytes();
    public static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    public static final byte[] HTTP_VERSION_1_1_A = HTTP_VERSION_1_1.getBytes();
    
        
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";

    public static final byte[] CONTENT_LENGTH_A = CONTENT_LENGTH.getBytes();
    public static final byte[] TRANSFER_ENCODING_A = TRANSFER_ENCODING.getBytes();
    public static final byte[] CHUNKED_A = "chunked".getBytes();
    public static final byte[] HOST_A = HOST.getBytes();
    public static final byte[] PROXY_CONNECTION_A = PROXY_CONNECTION.getBytes();
    public static final byte[] CONTENT_TYPE_A = CONTENT_TYPE.getBytes();
    public static final byte[] CONNECTION_A = CONNECTION.getBytes();
    public static final byte[] CACHE_CONTROL_A = CACHE_CONTROL.getBytes();
    public static final byte[] EXPIRES_A = EXPIRES.getBytes();
    public static final byte[] PRAGMA_A = PRAGMA.getBytes();
    public static final byte[] REFERER_A = REFERER.getBytes();
    public static final byte[] IF_MODIFIED_SINCE_A = IF_MODIFIED_SINCE.getBytes();
    public static final byte[] ACCEPT_ENCODING_A = ACCEPT_ENCODING.getBytes();

    private boolean isRemoved = false;
    
    protected HTTPHeader() {
    }
        
    public void setRemoved(boolean removed) {
    	isRemoved = removed;
    }
    
    public boolean isRemoved() {
    	return isRemoved;
    }
    
    public abstract boolean isFieldNameEqualsTo(byte[] name);

    public abstract boolean isFieldValueEqualsTo(byte[] value);

    public abstract byte[] getName();
        
    public abstract byte[] getValue();
        
    public abstract void setValue(byte[] value);
    
    protected abstract void write(OutputStream out) throws IOException;

    protected void writeHeaderSeparator(OutputStream out) throws IOException {
        out.write(':');
        out.write(HTTPReader.SP);
    }

    public boolean compareValue(byte[] value) {
        return compare(getValue(), value);
    }

    public boolean compareValueIgnoreCase(byte[] value) {
        return compareIgnoreCase(getValue(), value);
    }
    
    public void writeLine(OutputStream out) throws IOException {
    	if (!isRemoved) {
            write(out);
            out.write(HTTPReader.CR);
            out.write(HTTPReader.LF);
    	}
    }

    public String toString() {
        return "Header:" + new String(getName()) + ":" + new String(getValue());
    }
}
