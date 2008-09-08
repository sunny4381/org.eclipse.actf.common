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
package org.eclipse.actf.util.vocab.ui.preferences;

import org.eclipse.actf.util.vocab.VocabPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class VocabPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public VocabPreferencePage() {
		super(GRID);
		setPreferenceStore(VocabPlugin.getDefault().getPreferenceStore());
		setDescription("DOM Vocabulary Preferences");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
    public void createFieldEditors() {
        
        addField(new RadioGroupFieldEditor(VocabPreferenceConstants.NORMAL_FLASH, 
                "Extract normal Flash content information by", 1, 
                new String[][]{
                    {"MSAA", VocabPreferenceConstants.VALUE_MSAA_FLASH},
                    {"Flash DOM", VocabPreferenceConstants.VALUE_FLASH_DOM}, 
                    {"None", VocabPreferenceConstants.VALUE_NO_FLASH}}, 
                getFieldEditorParent()));
        
        addField(new RadioGroupFieldEditor(VocabPreferenceConstants.WNDLESS_FLASH, 
                "Extract windowless Flash content information by", 1, 
                new String[][]{
                    {"Flash DOM", VocabPreferenceConstants.VALUE_FLASH_DOM}, 
                    {"None", VocabPreferenceConstants.VALUE_NO_FLASH}}, 
                getFieldEditorParent()));

        addField(new BooleanFieldEditor(VocabPreferenceConstants.READ_NO_ALT,
                "Read image with no alt text", getFieldEditorParent()));
        addField(new BooleanFieldEditor(VocabPreferenceConstants.READ_NULL_ALT,
                "Read image with null alt text", getFieldEditorParent()));
        addField(new BooleanFieldEditor(VocabPreferenceConstants.READ_NO_ALT_LINK,
                "Read image link with no alt text", getFieldEditorParent()));
        addField(new BooleanFieldEditor(VocabPreferenceConstants.READ_NULL_ALT_LINK,
                "Read image link with null alt text", getFieldEditorParent()));
        addField(new BooleanFieldEditor(VocabPreferenceConstants.SKIP_ICON_LINK,
                "Skip image link following text link with the same link", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}
