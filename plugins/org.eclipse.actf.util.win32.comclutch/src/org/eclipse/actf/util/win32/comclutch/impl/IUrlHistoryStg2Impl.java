/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.win32.comclutch.impl;

import org.eclipse.actf.util.win32.comclutch.BrowserHistory;



public class IUrlHistoryStg2Impl implements BrowserHistory {
    private long ptr;
    
    public IUrlHistoryStg2Impl() {
        ptr = _initialize();
    }

    public boolean isVisited(String url) {
        return _isVisited(ptr, url);
    }

    public void release() {
        _release(ptr);
    }

    private native long _initialize();
    private native boolean _isVisited(long ptr, String url);
    private native void _release(long ptr);
}
