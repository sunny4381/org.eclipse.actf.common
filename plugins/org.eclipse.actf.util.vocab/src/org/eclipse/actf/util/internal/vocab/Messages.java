/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.internal.vocab;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
	public static String VocabPreferencePage_0;
	public static String VocabPreferencePage_1;
	public static String VocabPreferencePage_10;
	public static String VocabPreferencePage_11;
	public static String VocabPreferencePage_12;
	public static String VocabPreferencePage_5;
	public static String VocabPreferencePage_8;
	public static String VocabPreferencePage_9;
	public static String VocabPreferencePage_FlashDOM;
	public static String VocabPreferencePage_MSAA;
	public static String VocabPreferencePage_None;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
