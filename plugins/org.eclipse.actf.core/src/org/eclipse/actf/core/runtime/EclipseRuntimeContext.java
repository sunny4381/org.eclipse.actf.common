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


package org.eclipse.actf.core.runtime;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.actf.core.config.ConfigurationException;
import org.eclipse.actf.core.config.IConfiguration;
import org.eclipse.actf.core.config.XmlConfiguration;
import org.eclipse.actf.util.logging.EclipseErrorLogger;
import org.eclipse.actf.util.resources.EclipseResourceLocator;
import org.eclipse.actf.util.resources.IResourceLocator;


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
