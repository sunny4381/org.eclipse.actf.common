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

import org.eclipse.actf.model.internal.ui.editors.ie.events.DocumentCompleteParameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;




public class DocumentCompleteParametersImpl extends AbstractEventParameters implements DocumentCompleteParameters {

    private static int
        INDEX_Window = 0,   // [in]
        INDEX_url = 1;      // [in]
        
    public DocumentCompleteParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            int     dispWindow = getWindowAddress();
            String  url =        getUrl();
            boolean isTop =      isTopWindow();
            System.out.println("DocumentComplete("+dispWindow+",\""+url+"\") top="+isTop); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.DocumentCompleteParameters#getWindowAddress()
     */
    public int getWindowAddress() {
        return getDispatchAddress(INDEX_Window);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.DocumentCompleteParameters#getUrl()
     */
    public String getUrl() {
        return getString(INDEX_url);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.DocumentCompleteParameters#isTopWindow()
     */
    public boolean isTopWindow() {
        return getWindowAddress() == getControlSiteAddress();
    }
    
}
