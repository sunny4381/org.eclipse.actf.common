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

import java.security.SecureRandom;
import java.util.Map;

import org.eclipse.actf.util.httpproxy.util.CacheMap;




public class SWFSecretManager {
    private Map secrets = new CacheMap(256, 20);
    private SecureRandom random;
    private static final int SECRET_LENGTH = 20;

    public synchronized String getSecret(String id, boolean remove) {
        String secret = (String) secrets.get(id);
        if (remove && (secret != null)) {
            secrets.remove(id);
        }
        return secret;
    }

    byte[] requestSecret() {
        byte[] secretBytes = new byte[SECRET_LENGTH * 2];
        random.nextBytes(secretBytes);
        StringBuffer buf = new StringBuffer("contentid=");
        StringBuffer buf2 = new StringBuffer();
        int i;
        for (i = 0; i < SECRET_LENGTH; i++) {
            byte b = secretBytes[i];
            buf2.append(b % 100);
        }
        String id = buf2.toString();
        buf.append(buf2);
        buf2.setLength(0);
        buf.append("&secret=");
        for (; i < SECRET_LENGTH * 2; i++) {
            byte b = secretBytes[i];
            buf2.append(b % 100);
        }
        buf.append(buf2);
        buf.append("&end=term");
        buf.append('\r');
        buf.append('\n');
        String secret = buf2.toString();
        synchronized (this) {
            secrets.put(id, secret);
        }
        return buf.toString().getBytes();
    }

    public SWFSecretManager() {
        this.random = new SecureRandom();
    }
}
