/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash;

import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.Variant;


public class FlashAccInfo {

    private FlashNode parent;
    private Boolean hasOnRelease;
    private Boolean isSilent;
    private Integer accRole;
    
    public FlashAccInfo(FlashNode parent) {
        this.parent = parent;
    }
    
    public void dispose() {
        
    }
    
    public boolean hasOnRelease() {
        if( null == hasOnRelease ) {
            FlashNode onReleaseNode = parent.getNode("onRelease"); //$NON-NLS-1$
            if( null != onReleaseNode ) {
                hasOnRelease = Boolean.TRUE;
                onReleaseNode.dispose();
            }
            else {
                hasOnRelease = Boolean.FALSE;
            }
        }
        return hasOnRelease.booleanValue();
    }
    
    public int getAccRole() {
        if( null == accRole ) {
            Variant varRole = parent.getPlayer().callMethod(parent.getTarget()+"._accImpl","get_accRole",new Variant(0)); //$NON-NLS-1$ //$NON-NLS-2$
            if( null != varRole && OLE.VT_I4==varRole.getType() ) {
                accRole = new Integer(varRole.getInt());
            }
            else {
                accRole = new Integer(-1);
            }
        }
        return accRole.intValue();
    }
    
    public boolean isSilent() {
        if( null == isSilent ) {
            FlashNode accSilentNode = parent.getNode("_accProps.silent"); //$NON-NLS-1$
            if( null != accSilentNode ) {
                isSilent = new Boolean("true".equals(accSilentNode.getValue())); //$NON-NLS-1$
                accSilentNode.dispose();
            }
            else {
                isSilent = Boolean.FALSE;
            }
        }
        return isSilent.booleanValue();
    }
    
    public String getAccName() {
        Variant varName = parent.getPlayer().callMethod(parent.getTarget()+"._accImpl","get_accName", new Variant(0)); //$NON-NLS-1$ //$NON-NLS-2$
        if( null != varName && OLE.VT_BSTR==varName.getType() ) {
            return varName.getString();
        }
        FlashNode accNameNode = parent.getNode("_accProps.name"); //$NON-NLS-1$
        if( null != accNameNode ) {
            try {
                return accNameNode.getValue();
            }
            finally {
                accNameNode.dispose();
            }
        }
        return null;
    }
    
    public String getAccDescription() {
        FlashNode accDescriptionNode = parent.getNode("_accProps.description"); //$NON-NLS-1$
        if( null != accDescriptionNode ) {
            try {
                return accDescriptionNode.getValue();
            }
            finally {
                accDescriptionNode.dispose();
            }
        }
        return null;
    }

}
