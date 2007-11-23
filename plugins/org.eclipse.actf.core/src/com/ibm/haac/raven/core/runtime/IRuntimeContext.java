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


package com.ibm.haac.raven.core.runtime;

import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.logging.IErrorLogger;
import com.ibm.haac.raven.core.resources.IResourceLocator;

public interface IRuntimeContext
{

	public IConfiguration getConfiguration () throws ConfigurationException;
	public IResourceLocator getResourceLocator ();
	public IErrorLogger getErrorLogger ();
	
}
