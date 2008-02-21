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
package org.eclipse.actf.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

public class EclipseConfiguration extends AbstractConfiguration {

	/**
	 * 
	 */
	static final long serialVersionUID = 7981371198710093969L;
	public static final String MODEL_ID_ATTRIBUTE = "id";
	public static final String ALIAS_ID = "alias";

	transient private HashMap modelMap;
	transient private HashMap filterMap;
	transient private HashMap pluginMap;
	transient private HashMap attributeMap;
	private Stack elementStack = new Stack();
	private String pluginId;

	/**
	 * create a Configuration from extension point information
	 * 
	 * 
	 * @throws ConfigurationException
	 */
	public EclipseConfiguration() throws ConfigurationException {
		super();
	}

	/**
	 * treats data object as an
	 * <code>IConfigurationElement (or IExtension??)</code>.
	 * 
	 * @param data
	 *            configuration data in the form of a
	 *            <code>IConfigurationElement (or IExtension??)</code>
	 * @throws ConfigurationException
	 */
	public void addConfigurationData(Object data) throws ConfigurationException {
		if (data instanceof IConfigurationElement) {
			IConfigurationElement element = (IConfigurationElement) data;
			pluginId = element.getNamespaceIdentifier();
			addElement(element);
		} else if (data instanceof IExtension) {
			IExtension extension = (IExtension) data;
			pluginId = extension.getNamespaceIdentifier();
			IConfigurationElement[] configElements = extension
					.getConfigurationElements();
			for (int i = 0; i < configElements.length; i++) {
				addElement(configElements[i]);
			}
		}

	}

	private void addElement(IConfigurationElement element)
			throws ConfigurationException {
		/*
		 * our extension points are currently either model types, or nodeFilters
		 * This version is based  on just to get it working for now, then make it more generic to
		 * accommodate later additions and be more efficient
		 */
		String elementName = element.getName();
		if (elementName.equals(MODEL_ID)) {
			// create the model symbol pool if it doesn't exist and then select
			// it
			if (_configMap.containsKey(elementName)) {
				setSymbolPool(elementName);
				modelMap = (HashMap) _configMap.get(elementName);
			} else {
				createSymbolPool(elementName);
				modelMap = new HashMap();
				_configMap.put(elementName, modelMap);
			}
			// get model attributes and add the model to the model pool
			String[] attrNames = element.getAttributeNames();
			attributeMap = new HashMap();
			for (int i = 0; i < attrNames.length; i++) {
				String attrValue = element.getAttribute(attrNames[i]);
				attributeMap.put(attrNames[i], attrValue);
			}
			String modelId = (String) attributeMap.get(MODEL_ID_ATTRIBUTE);
			modelMap.put(modelId, attributeMap);

			// then get all of its children and process them too
			IConfigurationElement[] children = element.getChildren();
			if (children.length > 0) {
				elementStack.push(modelId);
				for (int i = 0; i < children.length; i++) {
					addElement(children[i]);
				}
				elementStack.pop();
			}
		} else if (elementName.equals(IConfiguration.FILTER_ID)) {
			// create the filter symbol pool if it doesn't exist and then select
			// it
			if (_configMap.containsKey(elementName)) {
				setSymbolPool(elementName);
				filterMap = (HashMap) _configMap.get(elementName);
			} else {
				createSymbolPool(elementName);
				filterMap = new HashMap();
				_configMap.put(elementName, filterMap);
			}
			// get filter attributes and add the filter to the filter pool
			String[] attrNames = element.getAttributeNames();
			attributeMap = new HashMap();
			for (int i = 0; i < attrNames.length; i++) {
				String attrValue = element.getAttribute(attrNames[i]);
				attributeMap.put(attrNames[i], attrValue);
			}
			String filterModel = (String) attributeMap
					.get(IConfiguration.FILTER_MODEL_ATTRIBUTE);
			filterMap.put(filterModel, attributeMap);

		} else if (elementName.equals(IConfiguration.ALIASES_ID)) {
			// aliases are children of the model element
			String model = (String) elementStack.peek();
			// create pool with id <model-name>_aliases and add the aliases
			String poolName = model + MODEL_POOL_ID_DELIMITER
					+ IConfiguration.ALIASES_ID;
			if (_configMap.containsKey(poolName)) {
				setSymbolPool(poolName);
			} else {
				createSymbolPool(poolName);
			}
			// then get all of its children and process them too
			IConfigurationElement[] children = element.getChildren();
			if (children.length > 0) {
				for (int i = 0; i < children.length; i++) {
					addElement(children[i]);
				}
			}
		} else if (elementName.equals(ALIAS_ID)) {
			// get the attributes and add this element to
			// the current model alias pool - <model_name>_aliases)
			String key = element.getAttribute("name");
			String value = element.getAttribute("value");
			setParameter(key, value);
		} else {
			throw new ConfigurationException("addElement - unknown element "
					+ elementName);
		}
	}

	/*
	 * Adaptor method to handle the fact that we have two different
	 * Configurations adding data to the configMap. This ensures that they both
	 * add it to the same map
	 */
	 //TODO AKF - remove this if u can get class var solution working
	protected Map addConfigurationData(Object data, Map configMap)
			throws ConfigurationException {
		_configMap = configMap;
		addConfigurationData(data);
		return _configMap;
	}

}
