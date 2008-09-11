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


package org.eclipse.actf.util.internal.httpproxy;

import java.util.ArrayList;





public class ObjectPoolImpl implements IObjectPool {
    private final String name;
    private ArrayList pool;
    private int waitInLine;

    synchronized public boolean add(Object o) {
        boolean flag = pool.add(o);
        if (flag &&  (waitInLine > 0)) notify();
        return flag;
    }

    synchronized public Object take() {
        if (pool.size() == 0) return null;
        return pool.remove(0);
    }

    synchronized public Object take(int timeout)
        throws InterruptedException {
        long startTime = System.currentTimeMillis();

        waitInLine++;
        while (pool.size() == 0) {
            wait(timeout);
            if (timeout > 0) {
                if ((System.currentTimeMillis() - startTime) > timeout) {
                    waitInLine--;
                    return null;
                }
            }
        }
        waitInLine--;
        return pool.remove(0);
    }

    public ObjectPoolImpl(String name) {
        this.name = name;
        this.pool = new ArrayList();
        this.waitInLine = 0;
    }

    public String getName() {
        return name;
    }
}
