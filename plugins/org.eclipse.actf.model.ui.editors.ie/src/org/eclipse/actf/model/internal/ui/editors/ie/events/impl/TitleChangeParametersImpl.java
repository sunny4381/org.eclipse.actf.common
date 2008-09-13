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

import org.eclipse.actf.model.internal.ui.editors.ie.events.TitleChangeParameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;




public class TitleChangeParametersImpl extends AbstractEventParameters implements TitleChangeParameters {

    private static int
    INDEX_text = 0; // [in]

    public TitleChangeParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            String text = getText();
            System.out.println("TitleChange(\""+text+"\")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.TitleChangeParameters#getText()
     */
    public String getText() {
        return getString(INDEX_text);
    }
    
}
