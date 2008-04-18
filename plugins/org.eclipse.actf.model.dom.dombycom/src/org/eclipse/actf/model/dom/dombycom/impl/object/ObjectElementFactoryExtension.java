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
package org.eclipse.actf.model.dom.dombycom.impl.object;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.dom.dombycom.IObjectElementFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

public class ObjectElementFactoryExtension {
	private static final String EXTENSION_NAME = "elementFactory";

	private static final String OBJECT_ELEMENT_FACTORY = "objectElementFactory";

	private static final String ATTR_IMPL = "implementations";

	private static ObjectElementFactoryExtension[] extensions;

	private static IObjectElementFactory[] factories = null;

	public static IObjectElementFactory[] getObjectElementFactories() {
		if (factories != null) {
			return factories;
		}

		ObjectElementFactoryExtension[] tmpExtensions = getExtensions();
		ArrayList<IObjectElementFactory> tmpList = new ArrayList<IObjectElementFactory>();
		if (tmpExtensions != null) {
			for (int i = 0; i < tmpExtensions.length; i++) {
				IObjectElementFactory tmpFactory = tmpExtensions[i].getFactory();
				if (tmpFactory != null) {
					tmpList.add(tmpFactory);
				}
			}
		}
		factories = new IObjectElementFactory[tmpList.size()];
		tmpList.toArray(factories);
		return factories;
	}

	private static ObjectElementFactoryExtension[] getExtensions() {
		if (extensions != null)
			return extensions;

		IExtension[] tmpExtensions = Platform.getExtensionRegistry()
				.getExtensionPoint("org.eclipse.actf.model.dom.dombycom", EXTENSION_NAME)
				.getExtensions();

		List<ObjectElementFactoryExtension> l = new ArrayList<ObjectElementFactoryExtension>();
		for (int i = 0; i < tmpExtensions.length; i++) {
			IConfigurationElement[] configElements = tmpExtensions[i]
					.getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) {
				ObjectElementFactoryExtension ex = parseExtension(configElements[j]);
				if (ex != null) {
					l.add(ex);
				}
			}
		}
		extensions = l.toArray(new ObjectElementFactoryExtension[l.size()]);
		return extensions;
	}

	private static ObjectElementFactoryExtension parseExtension(
			IConfigurationElement configElement) {
		if (!configElement.getName().equals(OBJECT_ELEMENT_FACTORY)) {
			return null;
		}
		try {
			return new ObjectElementFactoryExtension(configElement);
		} catch (Exception e) {
		}
		return null;
	}

	private IObjectElementFactory factory = null;

	private ObjectElementFactoryExtension(IConfigurationElement configElement) {
		try {
			this.factory = (IObjectElementFactory) configElement
					.createExecutableExtension(ATTR_IMPL);
		} catch (Exception e) {
		}

	}

	private IObjectElementFactory getFactory() {
		return this.factory;
	}
	

}
