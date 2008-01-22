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

import org.eclipse.actf.model.ui.editors.ie.internal.events.BeforeNavigate2Parameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;

public class BeforeNavigate2ParametersImpl extends AbstractEventParameters implements BeforeNavigate2Parameters {
    
    private static int 
        INDEX_Browser = 0,          // [in]
        INDEX_url = 1,              // [in]
        INDEX_flags = 2,            // [in]
        INDEX_targetFrameName = 3,  // [in]
        INDEX_postData = 4,         // [in]
        INDEX_headers = 5,          // [in]
        INDEX_cancel = 6;           // [in,out]

    public BeforeNavigate2ParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            int     dispBrowser =     getBrowserAddress();
            String  url =             getUrl();
            int     flags =           getFlags();
            String  targetFrameName = getTargetFrameName();
            Object  postData =        getPostData();
            String  headers =         getHeaders();
            boolean cancel =          getCancel();
            System.out.println("BeforeNavigate2("+dispBrowser+",\""+url+"\","+flags+",\""+targetFrameName+"\","+postData+",\""+headers+"\","+cancel+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#getBrowserAddress()
     */
    public int getBrowserAddress() {
        return getDispatchAddress(INDEX_Browser);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#getUrl()
     */
    public String getUrl() {
        return getString(INDEX_url);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#getFlags()
     */
    public int getFlags() {
        return getInteger(INDEX_flags);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#getTargetFrameName()
     */
    public String getTargetFrameName() {
        return getString(INDEX_targetFrameName);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#getPostData()
     */
    public Object getPostData() {
        return getVariant(INDEX_postData);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#getHeaders()
     */
    public String getHeaders() {
        return getString(INDEX_headers);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#getCancel()
     */
    public boolean getCancel() {
        return getBoolean(INDEX_cancel);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.BeforeNavigate2Parameters#setCancel(boolean)
     */
    public void setCancel(boolean cancel) {
        setBooleanByRef(INDEX_cancel, cancel);
    }

}
