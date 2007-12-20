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

package org.eclipse.actf.model.flash.proxy.preferences;

import org.eclipse.actf.model.flash.proxy.ProxyPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


public class ProxyPreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        IPreferenceStore store = ProxyPlugin.getDefault().getPreferenceStore();
        store.setDefault(ProxyPreferenceConstants.P_PROXY_TYPE,ProxyPreferenceConstants.PROXY_SESSION);
        store.setDefault(ProxyPreferenceConstants.P_PROXY_SWF_METHOD,ProxyPreferenceConstants.PROXY_SWF_METHOD_BOOTLOADER);
        store.setDefault(ProxyPreferenceConstants.P_SWF_MINIMUM_VERSION,5);
        store.setDefault(ProxyPreferenceConstants.P_TIMEOUT,30);
        store.setDefault(ProxyPreferenceConstants.P_CACHE_CLEAR, ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP);
    }

}
