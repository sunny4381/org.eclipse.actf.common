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

import org.eclipse.actf.model.internal.ui.editors.ie.events.StatusTextChangeParameters;
import org.eclipse.swt.ole.win32.OleEvent;




public class StatusTextChangeParametersImpl extends AbstractEventParameters implements StatusTextChangeParameters {

    private static int
        INDEX_text = 0; // [in]
    
    public StatusTextChangeParametersImpl(OleEvent event) {
        super(event);
//        if( Platform.inDebugMode() ) {
//            String text = getText();
//            System.out.println("StatusTextChange(\""+text+"\")"); //$NON-NLS-1$ //$NON-NLS-2$
//        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.StatusTextChangeParameters#getText()
     */
    public String getText() {
        return getString(INDEX_text);
    }
    
}
