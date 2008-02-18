/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.ui.preferences;

import org.eclipse.actf.core.config.IConfiguration;
import org.eclipse.actf.util.logging.IReporter;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;


public abstract class AbstractUIPreferenceInitializer extends AbstractPreferenceInitializer
{

	public static final String V_NONE = "SEVERE";
	public static final String V_ERROR = "ERROR";
	public static final String V_WARNING = "WARNING";
	public static final String V_INFO = "INFO";
	public static final String V_ALL = "DETAIL";
	
	public static final String P_MODELS = IConfiguration.MODEL_ID;
	public static final String P_ALIASES = 	"aliases";

	public static final int NONE = IReporter.SEVERE;
	public static final int ERRORS = IReporter.ERROR;
	public static final int WARNINGS = IReporter.WARNING;
	public static final int PROCESS_INFO = IReporter.INFO;
	public static final int PROCESS_ALL = IReporter.CONFIG;
	
	@Override
	public void initializeDefaultPreferences() {
		// TODO Auto-generated method stub
	
	}
}