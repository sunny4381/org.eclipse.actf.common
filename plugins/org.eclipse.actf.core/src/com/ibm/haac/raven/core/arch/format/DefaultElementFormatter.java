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

package com.ibm.haac.raven.core.arch.format;

import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.runtime.IRuntimeContext;
import com.ibm.haac.raven.core.runtime.RuntimeContextFactory;

/**
 * @author Mike Squillace
 */
public class DefaultElementFormatter implements ElementFormatter
{

	protected IConfiguration configuration;

	public DefaultElementFormatter () {
		try {
			IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
			configuration = context.getConfiguration();
		}catch (ConfigurationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	public String[] getDisplayedProperties () {
		return new String[0];
	}
	
	/** {@inheritDoc} */
	public void formatElement (Object element, int nest, StringBuffer out) {
		for (int i = 0; i < nest; i++) {
			out.append("    ");
		}
		out.append(nest);
		out.append(": ");
		out.append(element.getClass().getName());
		out.append(" [");
		String el = element.toString();
		if (el.length() > 30) {
			el = el.substring(0, 30) + "...";
		}
		out.append(el);
		out.append(']');
		out.append('\n');
	}
}
