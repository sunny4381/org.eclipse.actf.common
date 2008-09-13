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

package org.eclipse.actf.model.internal.ui.editors.ie.events.impl;

import org.eclipse.actf.model.internal.ui.editors.ie.events.NewWindow2Parameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;




public class NewWindow2ParametersImpl extends AbstractEventParameters implements NewWindow2Parameters {
    
    private static int
        INDEX_Browser = 0,  // [in,out]
        INDEX_cancel = 1;   // [in,out]

    public NewWindow2ParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            int     dispBrowser = getBrowserAddress();
            boolean cancel =      getCancel();
            System.out.println("NewWindow2("+dispBrowser+","+cancel+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NewWindow2Parameters#getBrowserAddress()
     */
    public int getBrowserAddress() {
        return getIntegerByRef(INDEX_Browser);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NewWindow2Parameters#setBrowserAddress(int)
     */
    public void setBrowserAddress(int address) {
        setIntegerByRef(INDEX_Browser, address);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NewWindow2Parameters#getCancel()
     */
    public boolean getCancel() {
        return getBoolean(INDEX_cancel);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NewWindow2Parameters#setCancel(boolean)
     */
    public void setCancel(boolean cancel) {
        setBooleanByRef(INDEX_cancel, cancel);
    }
    
}
