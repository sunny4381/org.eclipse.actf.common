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

package org.eclipse.actf.core;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.actf.core.config.IConfiguration;
import org.eclipse.actf.core.runtime.IRuntimeContext;
import org.eclipse.actf.core.runtime.RuntimeContextFactory;
import org.eclipse.actf.util.logging.LoggingUtil;
import org.eclipse.actf.util.resources.ClassLoaderCache;
import org.eclipse.actf.util.resources.EclipseResourceLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class ActfCorePlugin extends Plugin
{

	public static final String ACTFCORE_PLUGIN_ID = "org.eclipse.actf.core";

	protected IRuntimeContext runtimeContext;
	protected IConfiguration configuration;
	protected ClassLoaderCache clCache = ClassLoaderCache.getDefault();
	
	private Logger logger = Logger.getLogger(LoggingUtil.ACTF_CORE_LOGGER_NAME);
 
	protected String getPluginId () {
		return ACTFCORE_PLUGIN_ID;
	}

	// The shared instance.
	private static ActfCorePlugin plugin;

	/**
	 * Returns the shared instance.
	 * 
	 * @return plugin
	 */
	public static ActfCorePlugin getDefault () {
		if (plugin == null) {
			plugin = new ActfCorePlugin();
		}
		return plugin;
	}
	

	/**
	 * The constructor.
	 */
	public ActfCorePlugin () {
		super();
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 *            bundle context
	 * @throws Exception
	 */
	public void start (BundleContext context) throws Exception {
		super.start(context);
		
		runtimeContext = RuntimeContextFactory.getInstance().getRuntimeContext();
		configuration = runtimeContext.getConfiguration();
		EclipseResourceLocator locator = (EclipseResourceLocator) runtimeContext.getResourceLocator();
			
		// provide a way for retrieving classes and resources from all bundles
		locator.registerBundleName(getPluginId());
		clCache.put(getPluginId(), getClass().getClassLoader());
		
		String logging = System.getProperty("java.util.logging.config.file");
		if (logging == null){
			LogManager logManager = LogManager.getLogManager();
		    EclipseResourceLocator resourceLocator = (EclipseResourceLocator) runtimeContext.getResourceLocator();
		    resourceLocator.registerBundleName(ACTFCORE_PLUGIN_ID);
		    InputStream ins = resourceLocator.getResourceAsStream("logging", "\\", "properties", ACTFCORE_PLUGIN_ID);
		    if (ins != null){
		    	logManager.readConfiguration(ins);
		    }
		}
		
		
		logger.log(Level.INFO, getClass().getName() + " started");
		logger.log(Level.INFO, "configuration:" + configuration);
	}
	
	public IRuntimeContext getRuntimeContext () {
		return runtimeContext;
	}

	public void logException (String message, Throwable t) {
		String tname = t.getClass().getName();
		String msg = t.getMessage();
		msg = msg != null && msg.length() > 0 ? tname + " - " + msg : tname;		
		logger.log(Level.SEVERE, message != null ? message : "<No message>", t);
	}

	public void logException (Throwable t) {
		String msg = t.getMessage();
		logger.log(Level.SEVERE, msg != null && msg.length() > 0 ? msg : "<no message>", t);
	}
	
	public void stop (BundleContext context) throws Exception {
		logger.log(Level.INFO, getClass().getName() + " stopped");
		super.stop(context);
	}

	public boolean isDebugging (String option) {
		String value = Platform.getDebugOption(option);
		return super.isDebugging() && value != null && "true".equalsIgnoreCase(value);
	}
	
}