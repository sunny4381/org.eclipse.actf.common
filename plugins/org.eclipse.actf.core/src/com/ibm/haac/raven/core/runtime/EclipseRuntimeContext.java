/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  <a href="mailto:masquill@us.ibm.com>Mike Squillace</a> - initial API and implementation
*******************************************************************************/ 


package com.ibm.haac.raven.core.runtime;

import java.io.IOException;
import java.io.InputStream;

import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.config.XmlConfiguration;
import com.ibm.haac.raven.core.logging.EclipseErrorLogger;
import com.ibm.haac.raven.core.resources.EclipseResourceLocator;
import com.ibm.haac.raven.core.resources.IResourceLocator;

/**
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public class EclipseRuntimeContext extends AbstractRuntimeContext
{

	public EclipseRuntimeContext() {
		super();
		errorLogger = new EclipseErrorLogger();
		resourceLocator = new EclipseResourceLocator();
	}
	
	public IConfiguration getConfiguration () throws ConfigurationException {
		if (configuration == null) {
			InputStream configFileStream = resourceLocator.getResourceAsStream(
					IConfiguration.ACTF_ID, IResourceLocator.DEFAULT_ACTF_RESOURCES_DIR,
					"xml", null
			);
			
			configuration = new XmlConfiguration();
			if (configFileStream != null) {
				configuration.addConfigurationData(configFileStream);
				try {
					configFileStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return configuration;
	}

}
