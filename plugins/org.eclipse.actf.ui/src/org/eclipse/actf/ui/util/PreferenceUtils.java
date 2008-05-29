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

package org.eclipse.actf.ui.util;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;



public class PreferenceUtils {

    private static PreferenceDialog _preferenceDialog = null;

    public static void openPreferenceDialog() {
        openPreferenceDialog(null);
    }

    public static void openPreferenceDialog(String selectedPreferencePageId) {
        _preferenceDialog = PreferencesUtil.createPreferenceDialogOn(null, selectedPreferencePageId, null, null);
        _preferenceDialog.open();
    }

    public static void close() {
        if (null != _preferenceDialog) {
            _preferenceDialog.close();
            _preferenceDialog = null;
        }
    }
}
