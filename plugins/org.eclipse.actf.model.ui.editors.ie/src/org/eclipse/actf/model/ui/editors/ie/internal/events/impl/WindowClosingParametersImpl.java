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

import org.eclipse.actf.model.ui.editors.ie.internal.events.WindowClosingParameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;




public class WindowClosingParametersImpl extends AbstractEventParameters implements WindowClosingParameters {

    private static int
        INDEX_isChildWindow = 0,    // [in]
        INDEX_cancel = 1;           // [in,out]
    
    public WindowClosingParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            boolean isChildWindow = getIsChildWindow();
            boolean cancel =        getCancel();
            System.out.println("WindowClosing("+isChildWindow+","+cancel+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.WindowClosingParameters#getIsChildWindow()
     */
    public boolean getIsChildWindow() {
        return getBoolean(INDEX_isChildWindow);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.WindowClosingParameters#getCancel()
     */
    public boolean getCancel() {
        return getBoolean(INDEX_cancel);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.WindowClosingParameters#setCancel(boolean)
     */
    public void setCancel(boolean cancel) {
        setBooleanByRef(INDEX_cancel, cancel);
    }
    
}
