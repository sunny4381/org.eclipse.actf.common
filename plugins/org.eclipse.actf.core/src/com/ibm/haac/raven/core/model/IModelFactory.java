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

/**
 * implementations retreave the desired IModel based upon the
 * raven:model attribute value of the &lt;rulebase&gt; element.
 *
 * @author Mike Squillace
 */
public interface IModelFactory
{

	/**
	 * retreave the desired IModel for the given model type.
	 *
	 * @param model -- model type
	 * @return model appropriate for the given type
	 */
	public IModel resolveModel (String model);
	
} // IModelFactory
