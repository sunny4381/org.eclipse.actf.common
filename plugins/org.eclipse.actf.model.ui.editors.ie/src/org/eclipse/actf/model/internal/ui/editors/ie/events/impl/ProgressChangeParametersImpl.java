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

import org.eclipse.actf.model.internal.ui.editors.ie.events.ProgressChangeParameters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.ole.win32.OleEvent;




public class ProgressChangeParametersImpl extends AbstractEventParameters implements ProgressChangeParameters {

    private static int
        INDEX_nProgress = 0,    // [in]
        INDEX_nProgressMax = 1; // [in]
    
    public ProgressChangeParametersImpl(OleEvent event) {
        super(event);
        if( Platform.inDebugMode() ) {
            int nProgress =    getProgress();
            int nProgressMax = getProgressMax();
            System.out.println("ProgressChange("+nProgress+","+nProgressMax+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.ProgressChangeParameters#getProgress()
     */
    public int getProgress() {
        return getInteger(INDEX_nProgress);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.model.ui.editors.ie.events.ProgressChangeParameters#getProgressMax()
     */
    public int getProgressMax() {
        return getInteger(INDEX_nProgressMax);
    }
    
}
