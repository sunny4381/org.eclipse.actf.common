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

import java.text.MessageFormat;

import org.eclipse.actf.model.flash.internal.Messages;
import org.eclipse.actf.util.win32.comclutch.ComService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;


public class FlashDetect {

	private static final String CLSID_FLASH = "{D27CDB6E-AE6D-11CF-96B8-444553540000}";

    private static String strVersion = null;

    static {
    	try {
    		org.eclipse.actf.util.win32.comclutch.IDispatch idisp = ComService.createDispatch(CLSID_FLASH);
    		String rawVersion = (String) idisp.invoke1("GetVariable", "$version");
			int sep = rawVersion.indexOf(' ');
			if( sep > 0 ) {
				strVersion = rawVersion.substring(sep+1);
			}
    	}
    	catch( Exception e ) {
    		e.printStackTrace();
    	}
    }
    
    public static void showDialog() {
        if( null == strVersion ) return;
        int sep = strVersion.indexOf(',');
        if( sep > 0 ) {
            try {
                if( Integer.parseInt(strVersion.substring(0,sep)) < 8 ) {
                    MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
                            Messages.getString("flash.warning"), //$NON-NLS-1$
                            MessageFormat.format(Messages.getString("flash.bad_flash_version"),new Object[]{strVersion})); //$NON-NLS-1$
                }
            }
            catch( Exception e ) {
            }
        }
        strVersion = null;
    }
    
}
