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

import org.eclipse.actf.model.internal.ui.editors.ie.events.NavigateErrorParameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;




public class NavigateErrorParametersImpl extends AbstractEventParameters implements NavigateErrorParameters {
    
    private static int 
        INDEX_Browser = 0,          // [in]
        INDEX_url = 1,              // [in]
        INDEX_targetFrameName = 2,  // [in]
        INDEX_statusCode = 3,       // [in]
        INDEX_cancel = 4;           // [in,out]

    public NavigateErrorParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            int     dispBrowser =     getBrowserAddress();
            String  url =             getUrl();
            String  targetFrameName = getTargetFrameName();
            int     statusCode =      getStatusCode();
            boolean cancel =          getCancel();
            System.out.println("NavigateError("+dispBrowser+",\""+url+"\","+targetFrameName+"\","+statusCode+"\","+cancel+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateErrorParameters#getBrowserAddress()
     */
    public int getBrowserAddress() {
        return getDispatchAddress(INDEX_Browser);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateErrorParameters#getUrl()
     */
    public String getUrl() {
        return getString(INDEX_url);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateErrorParameters#getTargetFrameName()
     */
    public String getTargetFrameName() {
        return getString(INDEX_targetFrameName);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateErrorParameters#getStatusCode()
     */
    public int getStatusCode() {
        return getInteger(INDEX_statusCode);
    }


    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateErrorParameters#getCancel()
     */
    public boolean getCancel() {
        return getBoolean(INDEX_cancel);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateErrorParameters#setCancel(boolean)
     */
    public void setCancel(boolean cancel) {
        setBooleanByRef(INDEX_cancel, cancel);
    }

}
