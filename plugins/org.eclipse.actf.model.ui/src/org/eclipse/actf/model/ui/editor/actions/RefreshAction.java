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
import org.eclipse.actf.model.ui.editor.browser.DefaultWebBrowserNavigationEventListener;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserNavigationEventListener;
import org.eclipse.actf.model.ui.editor.browser.WebBrowserNavigationEvent;
import org.eclipse.actf.model.ui.util.ModelServiceMessages;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.jface.action.Action;




public class RefreshAction extends Action {
    private String message = ModelServiceMessages.getString("WebBrowser.Refresh");

    private String message_tp = ModelServiceMessages.getString("WebBrowser.Refresh_tp");

    private IWebBrowserNavigationEventListener defaultListener = new DefaultWebBrowserNavigationEventListener();

    public RefreshAction() {
        this(true);
    }

    public RefreshAction(boolean flag) {
        setText(message);
        setToolTipText(message_tp);
        if (flag)
            setImageDescriptor(ModelUIPlugin.getImageDescriptor("icons/toolbar/reload.png"));
    }

    public void run() {
        IModelService modelService = ModelServiceUtils.getActiveModelService();
        if (modelService != null && modelService instanceof IWebBrowserACTF) {
            WebBrowserNavigationEvent event = new WebBrowserNavigationEvent(this, ((IWebBrowserACTF) modelService));
            if (IWebBrowserACTF.WebBrowserNavigationEventListnerHolder.LISTENER != null) {
                IWebBrowserACTF.WebBrowserNavigationEventListnerHolder.LISTENER.refresh(event);
            } else {
                defaultListener.refresh(event);
            }
        }
    }
}
