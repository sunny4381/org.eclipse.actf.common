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

import org.eclipse.actf.util.win32.comclutch.IEnumUnknown;
import org.eclipse.actf.util.win32.comclutch.IOleContainer;
import org.eclipse.actf.util.win32.comclutch.IUnknown;



public class IOleContainerImpl extends IUnknownImpl implements IOleContainer {

    public IOleContainerImpl(IUnknownImpl base) {
        super(base);
    }
    
    public IEnumUnknown enumObjects(int flags) {
        IUnknown iunk = _enumObjects(getPtr(), flags);
        IEnumUnknown ieu = new IEnumUnknownImpl((IUnknownImpl) iunk);
        iunk.release();
        return ieu;
    }

    public boolean lockContainer(boolean lock) {
        return _lockContainer(getPtr(), lock);
    }
    
    private native IUnknown _enumObjects(long ptr, int flags);
    private native boolean _lockContainer(long ptr, boolean lock);

}
