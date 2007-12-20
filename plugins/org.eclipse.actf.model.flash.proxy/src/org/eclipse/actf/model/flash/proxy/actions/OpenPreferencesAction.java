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

package org.eclipse.actf.model.flash.proxy.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.dialogs.PreferencesUtil;



public class OpenPreferencesAction extends ActionDelegate implements IViewActionDelegate {

    IViewPart view;

    public void run(IAction action) {
        PreferencesUtil
        .createPreferenceDialogOn(view.getSite().getShell(), "org.eclipse.actf.model.flash.proxy.preferences.ProxyPreferencePage", null, null)
        .open();
    }

    public void init(IViewPart view) {
        this.view = view;
    }

}
