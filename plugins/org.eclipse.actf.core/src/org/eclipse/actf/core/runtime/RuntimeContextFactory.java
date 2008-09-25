/*******************************************************************************
* Copyright (c) 2004, 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.eclipse.actf.core.runtime;

import java.lang.reflect.Method;

public class RuntimeContextFactory
{

	public static final String RUNTIME_CONTEXT_CLASSNAME_KEY = "org.eclipse.actf.core.runtimeContext";
	
	private static RuntimeContextFactory factoryInstance;
	
	private IRuntimeContext contextInstance;
	
	protected RuntimeContextFactory () {
	}
	
	public static RuntimeContextFactory getInstance () {
		if (factoryInstance == null) {
			factoryInstance = new RuntimeContextFactory();
		}
		return factoryInstance;
	}
	
	public IRuntimeContext getRuntimeContext() {
		if (contextInstance == null) {
			contextInstance = createRuntimeContext();
		}
		return contextInstance;
	}
	
	private IRuntimeContext createRuntimeContext () {
		String clsName = "org.eclipse.actf.core.runtime.StandaloneRuntimeContext";
		String contextProp = System.getProperty(RUNTIME_CONTEXT_CLASSNAME_KEY);
		IRuntimeContext context = null;
		
		if (contextProp != null && contextProp.length() > 0) {
			clsName = contextProp;
		} else {
			try {
				Class platformCls = Class.forName("org.eclipse.core.runtime.Platform");
				Method isRunning = platformCls.getMethod("isRunning", (Class[]) null);
				if (isRunning != null) {
					boolean inEclipse = ((Boolean) isRunning.invoke(null, (Object[]) null)).booleanValue();
					if (inEclipse) {
						clsName = "org.eclipse.actf.core.runtime.EclipseRuntimeContext";
					}
				}
			} catch (Exception e) {
				// not in Eclipse so assume stand-alone
			}
		}
		
		try {
			context = (IRuntimeContext) Class.forName(clsName).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return context;
	}
}
