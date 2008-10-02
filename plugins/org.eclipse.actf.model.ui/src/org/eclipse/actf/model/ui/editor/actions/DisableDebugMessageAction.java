/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui.editor.actions;

import org.eclipse.actf.model.internal.ui.ModelUIPlugin;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.util.ModelServiceMessages;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;




public class DisableDebugMessageAction extends Action implements IPropertyChangeListener {
    private String message = ModelServiceMessages.getString("WebBrowser.Script");
    private String message_tp = ModelServiceMessages.getString("WebBrowser.Script_tp");


    public DisableDebugMessageAction() {
        this(true);
    }

    public DisableDebugMessageAction(boolean flag) {
        super("script", AS_CHECK_BOX);
        setText(message);
        setToolTipText(message_tp);
        //TODO refer current setting
        setChecked(true);
        if(flag)
            setImageDescriptor(ModelUIPlugin.getImageDescriptor("icons/toolbar/scriptDebug.png"));
    }

    public void run() {
        
        IModelService modelService = ModelServiceUtils.getActiveModelService();
        if (modelService != null && modelService instanceof IWebBrowserACTF) {
            ((IWebBrowserACTF) modelService).setDisableScriptDebugger(this.isChecked());
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        this.setChecked(((Action)event.getSource()).isChecked());
    }
}
