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

package org.eclipse.actf.model.ui.editors.ie.internal.events.impl;

import org.eclipse.actf.model.ui.editors.ie.internal.events.NavigateComplete2Parameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;




public class NavigateComplete2ParametersImpl extends AbstractEventParameters implements NavigateComplete2Parameters {
    
    private static int 
        INDEX_Browser = 0,          // [in]
        INDEX_url = 1;              // [in]

    public NavigateComplete2ParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            int     dispBrowser =     getBrowserAddress();
            String  url =             getUrl();
            System.out.println("NavigateComplete2("+dispBrowser+",\""+url+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateComplete2Parameters#getBrowserAddress()
     */
    public int getBrowserAddress() {
        return getDispatchAddress(INDEX_Browser);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.NavigateComplete2Parameters#getUrl()
     */
    public String getUrl() {
        return getString(INDEX_url);
    }

}
