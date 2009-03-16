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

package org.eclipse.actf.util.httpproxy.util;


/**
 *
 */
public class ParseURI {
    /**
     * @param uri
     * @return
     */
    static public String getAuthority(String uri) {
        int pos = uri.indexOf(':');
        if (uri.length() <= (pos + 3)) {
            // Authority needs "://"
            return null;
        }
        if (uri.charAt(pos + 1) != '/') return null;
        if (uri.charAt(pos + 2) != '/') return null;
        pos += 3;
        int posEnd = uri.indexOf('/', pos);
        if (posEnd >= 0) {
            // scheme "://" authority abs-path
            return uri.substring(pos, posEnd);
        } else {
            // scheme "://" authority
            return uri.substring(pos);
        }
    }

    /**
     * @param uri
     * @return
     */
    static public String getAbsolutePath(String uri) {
        int pos = uri.indexOf(':');
        if (uri.length() <= (pos + 3)) {
            // Authority needs "://"
            return uri;
        }
        if (uri.charAt(pos + 1) != '/') return uri;
        if (uri.charAt(pos + 2) != '/') return uri;
        pos += 3;
        int posEnd = uri.indexOf('/', pos);
        if (posEnd >= 0) {
            // scheme "://" authority abs-path
            return uri.substring(posEnd);
        } else {
            // scheme "://" authority
            return "/"; //$NON-NLS-1$
        }
    }

    /**
     * @param hostStr
     * @return
     */
    static public String parseHost(String hostStr) {
        int sepIdx = hostStr.indexOf(':');
        if (sepIdx < 0) {
            return hostStr;
        } else {
            return hostStr.substring(0, sepIdx);
        }
    }

    /**
     * @param hostStr
     * @param defaultPort
     * @return
     */
    static public int parsePort(String hostStr, int defaultPort) {
        int sepIdx = hostStr.indexOf(':');
        if (sepIdx < 0) {
            return defaultPort;
        } else {
            String portStr = hostStr.substring(sepIdx + 1).trim();
            try {
                return Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                return defaultPort;
            }
        }
    }

    /**
     * @param uri
     * @return
     */
    static public String eliminateQuery(String uri) {
        int idx = uri.indexOf('?');
        if (idx < 0) return uri;
        return uri.substring(0, idx);
    }

}
