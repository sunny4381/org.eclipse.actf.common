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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;



public class CacheMap extends TreeMap implements Map {
    private static final long serialVersionUID = 6681131647931821052L;

    private final int maxSize;
    private final int evictSize;
    
    private final LinkedList accessList = new LinkedList(); 

    public CacheMap(int maxSize, int evictSize) {
        this.maxSize = maxSize;
        this.evictSize = evictSize;
    }

    private void evict() {
        Iterator it = accessList.iterator();
        for (int i = 0; i < evictSize; i++) {
            if (!it.hasNext()) return;
            Object key = it.next();
            this.remove(key);
            it.remove();
        }
    }

    private int searchAccessList(Object key) {
        return accessList.indexOf(key);
    }

    private void accessEntry(Object key) {
        int idx = searchAccessList(key);
        if (idx >= 0) {
            accessList.remove(idx);
        }
        accessList.add(key);
    }

    public Object put(Object key, Object val) {
        if (size() >= maxSize) evict();
        accessEntry(key);
        return super.put(key, val);
    }

    public Object get(Object key) {
        accessEntry(key);
        return super.get(key);
    }

    public Object matchStartsWith(String prefix) {
        SortedMap smap = super.tailMap(prefix);
        Object okey;
        try {
            okey = smap.firstKey();
        } catch (NoSuchElementException e) {
            return null;
        }
        if (!(okey instanceof String)) return null;
        String key = (String) okey;
        // System.err.println("MSW:" + key + " / " + prefix);
        if (!key.startsWith(prefix)) return null;
        return super.get(key);
    }
}
