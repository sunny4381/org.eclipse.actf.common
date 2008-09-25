/*******************************************************************************
* Copyright (c) 2004, 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.eclipse.actf.core.runtime;

import org.eclipse.actf.core.config.ConfigurationException;
import org.eclipse.actf.core.config.IConfiguration;
import org.eclipse.actf.util.logging.IErrorLogger;
import org.eclipse.actf.util.resources.IResourceLocator;


public interface IRuntimeContext
{

	public IConfiguration getConfiguration () throws ConfigurationException;
	public IResourceLocator getResourceLocator ();
	public IErrorLogger getErrorLogger ();
	
}
