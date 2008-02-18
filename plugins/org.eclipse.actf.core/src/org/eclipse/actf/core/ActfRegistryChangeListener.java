/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Ann Ford - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.core;

import org.eclipse.actf.core.config.ConfigurationException;
import org.eclipse.actf.core.config.IConfiguration;
import org.eclipse.actf.core.runtime.IRuntimeContext;
import org.eclipse.actf.util.Utils;
import org.eclipse.actf.util.logging.IReporter;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;

public class ActfRegistryChangeListener implements IRegistryChangeListener {

	protected IConfiguration configuration;
	protected IRuntimeContext runtimeContext;
	public static final String namespace = ActfCorePlugin.ACTFCORE_PLUGIN_ID;
	public static final String MODEL_TYPES_EXTENSION_POINT = "modelTypes";
	public static final String NODE_FILTERS_EXTENSION_POINT = "nodeFilters";

	public ActfRegistryChangeListener() {
		super();
	}

	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(namespace);
		for (int i = 0; i < deltas.length; i++) {
			IExtensionPoint point = deltas[i].getExtensionPoint();
			if (point.getSimpleIdentifier().equals(MODEL_TYPES_EXTENSION_POINT)
					|| point.getSimpleIdentifier().equals(
							NODE_FILTERS_EXTENSION_POINT)) {

				IExtension[] extensions = point.getExtensions();
				for (int j = 0; j < extensions.length; j++) {
					try {
						configuration.addConfigurationData(extensions[j]);
					} catch (ConfigurationException e) {
						Utils.println(IReporter.SYSTEM_NONFATAL,"ConfigurationException" +e.getMessage());
					}
				}
			}
		}
	}

}
