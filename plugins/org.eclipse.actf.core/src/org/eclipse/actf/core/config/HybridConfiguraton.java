/*******************************************************************************
* Copyright (c) 2004, 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ package org.eclipse.actf.core.config;

import java.io.InputStream;

public class HybridConfiguraton extends AbstractConfiguration {

	/**
	 * a Configuration which can handle both XML files which contain ACTF's
	 * default values and Eclipse IConfigurationElement or IExtension data
	 * 
	 * @author Ann Ford
	 */
	
	private static final long serialVersionUID = -6926451284721623066L;

	private XmlConfiguration xmlConfig;
	private EclipseConfiguration eclipseConfiguration;

	public HybridConfiguraton() throws ConfigurationException {
		super();
		xmlConfig = new XmlConfiguration();
		eclipseConfiguration = new EclipseConfiguration();
		createSymbolPool(ACTF_ID);
	}

	@Override
	public void addConfigurationData(Object data) throws ConfigurationException {
		// determine if data is input stream or IConfigurationElement
		// call appropriate delegate to load the configuration
		// pass the _configMap along so that they are both using the same copy
		if (data instanceof InputStream) {
		_configMap = xmlConfig.addConfigurationData(data, _configMap);
		//	xmlConfig.addConfigurationData(data);
		} else {
		_configMap = eclipseConfiguration.addConfigurationData(data,
					_configMap);
			//eclipseConfiguration.addConfigurationData(data);
		}
	}
	
}
