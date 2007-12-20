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

package org.eclipse.actf.model.flash.proxy.internal;


public class WinInet {

    static {
        try {
            System.loadLibrary("AccessibilityProxyLibrary"); //$NON-NLS-1$
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    //
    //  access types for InternetOpen()
    //
    public static final int INTERNET_OPEN_TYPE_PRECONFIG                   = 0;   // use registry configuration
    public static final int INTERNET_OPEN_TYPE_DIRECT                      = 1;   // direct to net
    public static final int INTERNET_OPEN_TYPE_PROXY                       = 3;   // via named proxy
    public static final int INTERNET_OPEN_TYPE_PRECONFIG_WITH_NO_AUTOPROXY = 4;   // prevent using java/script/INS

    //
    //  perConnOptions manifests for Internet{Query|Set}Option
    //
    public static final int INTERNET_OPTION_CALLBACK                = 1;
    public static final int INTERNET_OPTION_CONNECT_TIMEOUT         = 2;
    public static final int INTERNET_OPTION_CONNECT_RETRIES         = 3;
    public static final int INTERNET_OPTION_CONNECT_BACKOFF         = 4;
    public static final int INTERNET_OPTION_SEND_TIMEOUT            = 5;
    public static final int INTERNET_OPTION_CONTROL_SEND_TIMEOUT    = INTERNET_OPTION_SEND_TIMEOUT;
    public static final int INTERNET_OPTION_RECEIVE_TIMEOUT         = 6;
    public static final int INTERNET_OPTION_CONTROL_RECEIVE_TIMEOUT = INTERNET_OPTION_RECEIVE_TIMEOUT;
    public static final int INTERNET_OPTION_DATA_SEND_TIMEOUT       = 7;
    public static final int INTERNET_OPTION_DATA_RECEIVE_TIMEOUT    = 8;
    public static final int INTERNET_OPTION_HANDLE_TYPE             = 9;
    public static final int INTERNET_OPTION_LISTEN_TIMEOUT          = 11;
    public static final int INTERNET_OPTION_READ_BUFFER_SIZE        = 12;
    public static final int INTERNET_OPTION_WRITE_BUFFER_SIZE       = 13;
    public static final int INTERNET_OPTION_ASYNC_ID                = 15;
    public static final int INTERNET_OPTION_ASYNC_PRIORITY          = 16;
    public static final int INTERNET_OPTION_PARENT_HANDLE           = 21;
    public static final int INTERNET_OPTION_KEEP_CONNECTION         = 22;
    public static final int INTERNET_OPTION_REQUEST_FLAGS           = 23;
    public static final int INTERNET_OPTION_EXTENDED_ERROR          = 24;
    public static final int INTERNET_OPTION_OFFLINE_MODE            = 26;
    public static final int INTERNET_OPTION_CACHE_STREAM_HANDLE     = 27;
    public static final int INTERNET_OPTION_USERNAME                = 28;
    public static final int INTERNET_OPTION_PASSWORD                = 29;
    public static final int INTERNET_OPTION_ASYNC                   = 30;
    public static final int INTERNET_OPTION_SECURITY_FLAGS          = 31;
    public static final int INTERNET_OPTION_SECURITY_CERTIFICATE_STRUCT = 32;
    public static final int INTERNET_OPTION_DATAFILE_NAME           = 33;
    public static final int INTERNET_OPTION_URL                     = 34;
    public static final int INTERNET_OPTION_SECURITY_CERTIFICATE    = 35;
    public static final int INTERNET_OPTION_SECURITY_KEY_BITNESS    = 36;
    public static final int INTERNET_OPTION_REFRESH                 = 37;
    public static final int INTERNET_OPTION_PROXY                   = 38;
    public static final int INTERNET_OPTION_SETTINGS_CHANGED        = 39;
    public static final int INTERNET_OPTION_VERSION                 = 40;
    public static final int INTERNET_OPTION_USER_AGENT              = 41;
    public static final int INTERNET_OPTION_END_BROWSER_SESSION     = 42;
    public static final int INTERNET_OPTION_PROXY_USERNAME          = 43;
    public static final int INTERNET_OPTION_PROXY_PASSWORD          = 44;
    public static final int INTERNET_OPTION_CONTEXT_VALUE           = 45;
    public static final int INTERNET_OPTION_CONNECT_LIMIT           = 46;
    public static final int INTERNET_OPTION_SECURITY_SELECT_CLIENT_CERT = 47;
    public static final int INTERNET_OPTION_POLICY                  = 48;
    public static final int INTERNET_OPTION_DISCONNECTED_TIMEOUT    = 49;
    public static final int INTERNET_OPTION_CONNECTED_STATE         = 50;
    public static final int INTERNET_OPTION_IDLE_STATE              = 51;
    public static final int INTERNET_OPTION_OFFLINE_SEMANTICS       = 52;
    public static final int INTERNET_OPTION_SECONDARY_CACHE_KEY     = 53;
    public static final int INTERNET_OPTION_CALLBACK_FILTER         = 54;
    public static final int INTERNET_OPTION_CONNECT_TIME            = 55;
    public static final int INTERNET_OPTION_SEND_THROUGHPUT         = 56;
    public static final int INTERNET_OPTION_RECEIVE_THROUGHPUT      = 57;
    public static final int INTERNET_OPTION_REQUEST_PRIORITY        = 58;
    public static final int INTERNET_OPTION_HTTP_VERSION            = 59;
    public static final int INTERNET_OPTION_RESET_URLCACHE_SESSION  = 60;
    public static final int INTERNET_OPTION_ERROR_MASK              = 62;
    public static final int INTERNET_OPTION_FROM_CACHE_TIMEOUT      = 63;
    public static final int INTERNET_OPTION_BYPASS_EDITED_ENTRY     = 64;
    public static final int INTERNET_OPTION_DIAGNOSTIC_SOCKET_INFO  = 67;
    public static final int INTERNET_OPTION_CODEPAGE                = 68;
    public static final int INTERNET_OPTION_CACHE_TIMESTAMPS        = 69;
    public static final int INTERNET_OPTION_DISABLE_AUTODIAL        = 70;
    public static final int INTERNET_OPTION_MAX_CONNS_PER_SERVER    = 73;
    public static final int INTERNET_OPTION_MAX_CONNS_PER_1_0_SERVER = 74;
    public static final int INTERNET_OPTION_PER_CONNECTION_OPTION   = 75;
    public static final int INTERNET_OPTION_DIGEST_AUTH_UNLOAD      = 76;
    public static final int INTERNET_OPTION_IGNORE_OFFLINE          = 77;
    public static final int INTERNET_OPTION_IDENTITY                = 78;
    public static final int INTERNET_OPTION_REMOVE_IDENTITY         = 79;
    public static final int INTERNET_OPTION_ALTER_IDENTITY          = 80;
    public static final int INTERNET_OPTION_SUPPRESS_BEHAVIOR       = 81;
    public static final int INTERNET_OPTION_AUTODIAL_MODE           = 82;
    public static final int INTERNET_OPTION_AUTODIAL_CONNECTION     = 83;
    public static final int INTERNET_OPTION_CLIENT_CERT_CONTEXT     = 84;
    public static final int INTERNET_OPTION_AUTH_FLAGS              = 85;
    public static final int INTERNET_OPTION_COOKIES_3RD_PARTY       = 86;
    public static final int INTERNET_OPTION_DISABLE_PASSPORT_AUTH   = 87;
    public static final int INTERNET_OPTION_SEND_UTF8_SERVERNAME_TO_PROXY = 88;
    public static final int INTERNET_OPTION_EXEMPT_CONNECTION_LIMIT = 89;
    public static final int INTERNET_OPTION_ENABLE_PASSPORT_AUTH    = 90;
    public static final int INTERNET_OPTION_HIBERNATE_INACTIVE_WORKER_THREADS = 91;
    public static final int INTERNET_OPTION_ACTIVATE_WORKER_THREADS = 92;
    public static final int INTERNET_OPTION_RESTORE_WORKER_THREAD_DEFAULTS = 93;
    public static final int INTERNET_OPTION_SOCKET_SEND_BUFFER_LENGTH = 94;
    public static final int INTERNET_OPTION_PROXY_SETTINGS_CHANGED  = 95;
    public static final int INTERNET_OPTION_DATAFILE_EXT            = 96;
    public static final int INTERNET_FIRST_OPTION                   = INTERNET_OPTION_CALLBACK;
    public static final int INTERNET_LAST_OPTION                    = INTERNET_OPTION_DATAFILE_EXT;

    /** Natives */
    public static final native int InternetOpenW(int lpszAgent, int dwAccessType, int lpszProxy, int lpszProxyBypass, int dwFlags);
    public static final native boolean InternetCloseHandle(int hInternet);
    public static final native boolean InternetSetOptionW(int hInternet, int dwOption, int lpBuffer, int dwBufferLength);
    public static final native boolean InternetQueryOptionW(int hInternet, int dwOption, int lpBuffer, int[] lpdwBufferLength);
    
    public static final native int FindFirstUrlCacheEntryW(int lpszUrlSearchPattern,int lpFirstCacheEntryInfo, int[] lpcbCacheEntryInfo);
    public static final native boolean FindNextUrlCacheEntryW(int hEnumHandle,int lpNextCacheEntryInfo,int[] lpcbCacheEntryInfo);
    public static final native boolean FindCloseUrlCache(int hEnumHandle);
    public static final native boolean DeleteUrlCacheEntryW(int lpszUrlName);

}
