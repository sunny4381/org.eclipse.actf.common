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

package com.ibm.haac.raven.core.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.logging.IReporter;
import com.ibm.haac.raven.core.runtime.IRuntimeContext;
import com.ibm.haac.raven.core.runtime.RuntimeContextFactory;
import com.ibm.haac.util.Utils;

/**
 * retreaves the desired <code>IModel</code> based upon the
 * raven:model attribute value of the &lt;rulebase&gt; element.
 *
 * @see "resources/actf.xml"
 * @author Mike Squillace
 */
public class DefaultModelFactory implements IModelFactory
{

	private static DefaultModelFactory factoryInstance;

	private Map _modelCache = new HashMap();
	private IConfiguration _config;

	protected DefaultModelFactory () {
		try {
			IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
			_config = context.getConfiguration();
		}catch (ConfigurationException e) {
			throw new RuntimeException("Cannot initialize DefaultModelFactory - no Configuration object");
		}
	}

	/**
	 * retreave the instance of this default factory implementation.
	 * 
	 * @return factory instance
	 */
	public static DefaultModelFactory getInstance () {
		if (factoryInstance == null) {
			factoryInstance = new DefaultModelFactory();
		}
		return factoryInstance;
	}

	/**
	 * retrieve an <code>IModel</code> object for the given model type. If 
	 * a call to this method with the specified type has previously resulted in an <code>IModel</code> 
	 * object being created, that previously created instance will be returned.
	 * 
	 * <p>The IBM Rule-based Accessibility Validation Environment currently supports the following
	 * models:
	 *
	 * <p><ul>
	 * <li>Java AWT
	 * <li>Java Swing
	 * <li>Eclipse SWT
	 * <li>Mozilla/web
	 * </ul>
	 *
	 * @param model -- model type
	 * @see #resolveModel(String, boolean)
	 */
	public IModel resolveModel (String model) {
		return resolveModel(model, true);
	}

	/**
	 * retrieve an <code>IModel</code> object for the given model type. If 
	 * a call to this method with the specified type has previously resulted in an <code>IModel</code> 
	 * object being created and the useCache parameter is <code>true</code>, 
	 * that previously created instance will be returned.
	 * 
	 * <p>The IBM Rule-based Accessibility Validation Environment currently supports the following
	 * models:
	 *
	 * <p><ul>
	 * <li>Java AWT
	 * <li>Java Swing
	 * <li>Eclipse SWT
	 * <li>Mozilla/web
	 * </ul>
	 *
	 * @param modelType -- model type
	 * @param useCache -- if <code>true</code> any previously instantiated model of the specified 
	 * type will be used else a new <code>IModel</code> object will be created
	 */
	public IModel resolveModel (String modelType, boolean useCache) {
		IModel model = null;
		if (useCache && modelType != null) {
			model = (IModel) _modelCache.get(modelType);
		}
		if (model == null && modelType != null && _config != null) {
			List modelTypes = Arrays.asList(_config.getModelTypes());
			if (modelTypes.contains(modelType)) {
				try {
					_config.setSymbolPool(IConfiguration.MODEL_ID);
					Class modelCls = _config.getClassParameter(modelType);
					model = (IModel) modelCls.newInstance();
					_modelCache.put(modelType, model);
				}catch (Exception e) {
					Utils.println(IReporter.SYSTEM_FATAL, e);
					throw new IllegalArgumentException("Cannot find model for model named: "
							+ modelType);
				}
			}
		}
		return model;
	} // resolveModel

	/**
	 * find a model instance that supports the given type. The configuration of each model includes its 
	 * 'baseType' attribute, which specifies the superclass of all objects in the hierarchical structure embodied by 
	 * a model. This baseType is given by the <code>Configuration.MODEL_BASE_TYPE</code> key. This attribute 
	 * wil be examined for each model type that has been placed into the <code>Configuration.ACTF_ID</code> pool of the active 
	 * configuration instance.
	 * 
	 * <p>If more than one model supports the given type, which model 
	 * type is returned is indeterminant. A cached <code>IModel</code> instance is always used by this method when available.
	 * 
	 * @param type - type for which an model instance is desired
	 * @return model instance that supports the given type or <code>null</code> if no 
	 * model can be found that supports the given type
	 * @see com.ibm.haac.raven.core.config.IConfiguration#MODEL_BASE_TYPE
	 * @see com.ibm.haac.raven.core.config.IConfiguration#getModelTypes()
	 */
	public IModel resolveModel (Class type) {
		String[] modelTypes = _config.getModelTypes();
		IModel model = null;
		List classes = new LinkedList();
		
		classes.add(type);
		classes.addAll(Arrays.asList(type.getInterfaces()));
		OUTER: for (int a = 0; a < modelTypes.length; ++a) {
			_config.setModelSymbolPool(modelTypes[a], IConfiguration.MODEL_ID);
			String baseType = _config.getStringParameter(IConfiguration.MODEL_BASE_TYPE);
			// MAS: May want to support multiple base types
			if (baseType != null && baseType.length() > 0) {
				try {
					Class baseCls = Class.forName(baseType, false, type.getClassLoader());
					for (Iterator iter = classes.iterator(); iter.hasNext();) {
						if (baseCls.isAssignableFrom((Class) iter.next())) {
							model = resolveModel(modelTypes[a]);
							break OUTER;
						}
					}
				}catch (Exception e) {
					if (e instanceof ClassNotFoundException){
						Utils.println(
								IReporter.SYSTEM_FATAL, "ClassNotFound exception " + e.getMessage());
					} else {
						e.printStackTrace();
					}
				}
			}
		}
		return model;
	}
} // DefaultModelFactory
