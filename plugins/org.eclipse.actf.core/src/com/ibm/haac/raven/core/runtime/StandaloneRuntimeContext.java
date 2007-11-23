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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.config.XmlConfiguration;
import com.ibm.haac.raven.core.logging.StreamErrorLogger;
import com.ibm.haac.raven.core.resources.DefaultResourceLocator;

public class StandaloneRuntimeContext extends AbstractRuntimeContext
{

	public StandaloneRuntimeContext() {
		super();
		errorLogger = new StreamErrorLogger(System.err);
		resourceLocator = new DefaultResourceLocator();
	}

	public IConfiguration getConfiguration () throws ConfigurationException {
		if (configuration == null) {
			URL[] urls = resourceLocator.getResources(IConfiguration.ACTF_ID + ".xml");
			configuration = new XmlConfiguration();
			for (int p = 0; p < urls.length; ++p) {
				try {
					InputStream configFileStream = urls[p].openStream(); 
					//System.err.println("url="+urls[p]+"; "+urls[p].toExternalForm()+"; "+configFileStream);
					if (configFileStream != null) {
						configuration.addConfigurationData(configFileStream);
					}
					configFileStream.close();
				} catch (IOException e) {
				}
			}
		}
		
	return configuration;
	}

}
