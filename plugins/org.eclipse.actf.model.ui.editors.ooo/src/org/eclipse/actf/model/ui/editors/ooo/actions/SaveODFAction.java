/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui.editors.ooo.actions;

import org.eclipse.actf.ui.util.PlatformUIUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;




public class SaveODFAction extends Action{
    
    private final String message = "Save";

    public SaveODFAction() {
        setText(message);
        setToolTipText(message);
        setImageDescriptor(PlatformUIUtil.getSharedImageDescriptor(ISharedImages.IMG_DEF_VIEW)); 
    }

    public void run() {
        MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OK);
        msgBox.setMessage("not implemented yet");
        msgBox.open();
    }
}
