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


package org.eclipse.actf.util.internal.httpproxy.proxy;

import java.util.HashMap;

import org.eclipse.actf.util.httpproxy.proxy.IClientStateManager;



public class ClientStateManager implements IClientStateManager {
    private Object key;
    private static HashMap clientStateManagers = new HashMap();

    private HashMap stateMap;

    // We should use read-write lock instead of mutex.
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.proxy.IClientStateManager#put(java.lang.Object, java.lang.Object)
	 */
    public synchronized void put(Object stateKey, Object stateValue) {
        stateMap.put(stateKey, stateValue);
    }
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.proxy.IClientStateManager#get(java.lang.Object)
	 */
    public synchronized Object get(Object stateKey) {
        return stateMap.get(stateKey);
    }

    private ClientStateManager(Object key) {
        this.key = key;
        this.stateMap = new HashMap();
    }
    
    public static IClientStateManager getClientStateManager(Object key) {
        IClientStateManager csm = (IClientStateManager) clientStateManagers.get(key);
        if (csm == null) {
            csm = new ClientStateManager(key);
            clientStateManagers.put(key, csm);
        }
        return csm;
    }
}

