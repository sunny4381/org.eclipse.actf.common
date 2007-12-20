/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.vocab.preferences;

import org.eclipse.actf.util.vocab.VocabPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class VocabPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
    public void initializeDefaultPreferences() {
		IPreferenceStore store = VocabPlugin.getDefault().getPreferenceStore();
        store.setDefault(VocabPreferenceConstants.NORMAL_FLASH, VocabPreferenceConstants.VALUE_MSAA_FLASH);
        store.setDefault(VocabPreferenceConstants.WNDLESS_FLASH, VocabPreferenceConstants.VALUE_FLASH_DOM);
        store.setDefault(VocabPreferenceConstants.READ_NO_ALT, true);
        store.setDefault(VocabPreferenceConstants.READ_NULL_ALT, false);
        store.setDefault(VocabPreferenceConstants.READ_NO_ALT_LINK, true);
        store.setDefault(VocabPreferenceConstants.READ_NULL_ALT_LINK, true);
        store.setDefault(VocabPreferenceConstants.SKIP_ICON_LINK, true);
	}

}
