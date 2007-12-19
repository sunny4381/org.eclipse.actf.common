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

package org.eclipse.actf.core;

import java.io.File;
import java.io.InputStream;

import org.eclipse.actf.core.config.IConfiguration;
import org.eclipse.actf.core.runtime.IRuntimeContext;
import org.eclipse.actf.core.runtime.RuntimeContextFactory;
import org.eclipse.actf.util.Utils;
import org.eclipse.actf.util.logging.AbstractReporter;
import org.eclipse.actf.util.logging.IReporter;
import org.eclipse.actf.util.resources.ClassLoaderCache;
import org.eclipse.actf.util.resources.EclipseResourceLocator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


public class ActfCorePlugin extends Plugin
	implements ITracingService, ILoggingService
{

	public static final String ACTF_CORE_JAR =  "actf-core.jar";
	
	public static final String ACTFCORE_PLUGIN_ID = "org.eclipse.actf.core";
	public static final String DEBUG_OPTION_ID = "debug";
	public static final String LOG_OPTION_ID = "log";
	public static final String TRACE_OPTION_ID = "trace";
	public static final String TRACESTREAM_OPTION_ID = "stream";
	public static final String TRACELEVEL_OPTION_ID = "level";

	protected IRuntimeContext runtimeContext;
	protected IConfiguration configuration;
	protected ClassLoaderCache clCache = ClassLoaderCache.getDefault();
	protected String traceStream;
	protected int traceLevel = IReporter.WARNING;

	protected String getPluginId () {
		return ACTFCORE_PLUGIN_ID;
	}

	protected String getTraceOptionId () {
		return getDebugOptionId() + "/" + TRACE_OPTION_ID;
	}

	protected String getDebugOptionId () {
		return getPluginId() + "/" + DEBUG_OPTION_ID;
	}

	protected String getLogOptionId () {
		return getDebugOptionId() + "/" + LOG_OPTION_ID ;
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
	
	protected String getDefaultTraceFilename () {
		return getPluginId() + "_" + TRACE_OPTION_ID + ".log";
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
		EclipseResourceLocator locator = (EclipseResourceLocator) runtimeContext.getResourceLocator();

		// provide a way for retrieving classes and resources from all bundles
		locator.registerBundleName(getPluginId());
		clCache.put(getPluginId(), getClass().getClassLoader());
		
		try {
			configuration = runtimeContext.getConfiguration();
			if (!getPluginId().equals(ACTFCORE_PLUGIN_ID)) {
				InputStream configStream = locator.getResourceAsStream(IConfiguration.ACTF_ID, null, "xml", getPluginId());
				if (configStream != null) {
				  configuration.addConfigurationData(configStream);
				  configStream.close();
				}		 
			}
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, getPluginId(), 0, "Error initializing configuration object", e));
		}
		
		/*String debug = Platform.getDebugOption(getDebugOptionId());
		setDebugging(debug != null && debug.equalsIgnoreCase("true"));
		if (isDebugging()) {
			prepareTraceFacility();
			if (configuration.getSymbolPoolContents(IConfiguration.ACTF_ID) == null){
				
			} 
			configuration.setSymbolPool(IConfiguration.ACTF_ID);
			configuration.setParameter(
				IConfiguration.TRACE_STREAM_KEY, traceStream);
			configuration.setParameter(
				IConfiguration.TRACE_LEVEL_KEY, traceLevel);
		}
		
		trace(getClass().getName() + " started");
		trace("configuration:" + configuration); */
	}

	protected IReporter getTracer () {
		return Utils.getTracer();
	}
	
	public IRuntimeContext getRuntimeContext () {
		return runtimeContext;
	}

	protected void prepareTraceFacility () {
		String trace = Platform.getDebugOption(getTraceOptionId());
		if (trace != null && trace.equalsIgnoreCase("true")) {
			traceStream = Platform.getDebugOption(getTraceOptionId() + "/" + TRACESTREAM_OPTION_ID);
			String levelStr = Platform.getDebugOption(getTraceOptionId() + "/" + TRACELEVEL_OPTION_ID);
			
			if (traceStream == null) {
				traceStream = setupDefaultTraceStream();
				if (traceStream == null) {
					traceStream = "stdout";
				}
			}
			
			if (levelStr == null) {
				levelStr = new Integer(IReporter.WARNING).toString();
			}
			try {
				traceLevel = Integer.parseInt(levelStr);
			}catch (NumberFormatException e) {
				traceLevel = IReporter.WARNING;
			}
			
			Utils.setTracer(AbstractReporter.getReporter(traceLevel, traceStream));
			Utils.getTracer().setSourceID(IReporter.TRACE);
		}
	}

	public void stop (BundleContext context) throws Exception {
		trace(getClass().getName() + " stopped");
		super.stop(context);
	}

	public void log (String option, int sev, int code,
					 String message, Throwable t) {
		if (message == null) {
			message = "<no message>";
		}
		if (isDebugging(option)) {
			Status status = new Status(sev, getPluginId(), code, message, t);
			getLog().log(status);
		}
	}

	public void logInfo (String message) {
		log(getLogOptionId(), IStatus.INFO, 0, message, (Throwable) null);
	}

	public void logError (String message) {
		log(getLogOptionId(), IStatus.ERROR, 0, message, (Throwable) null);
	}

	public void logException (String message, Throwable t) {
		String tname = t.getClass().getName();
		String msg = t.getMessage();
		msg = msg != null && msg.length() > 0 ? tname + " - " + msg : tname;
		log(getLogOptionId(), IStatus.ERROR, 1, message != null ? message : "<No message>", t);
	}

	public void logException (Throwable t) {
		String msg = t.getMessage();
		logException(msg != null && msg.length() > 0 ? msg : "<no message>", t);
	}

	public boolean isDebugging (String option) {
		String value = Platform.getDebugOption(option);
		return super.isDebugging() && value != null && "true".equalsIgnoreCase(value);
	}

	public void trace (String option, String message) {
		if (isDebugging(option)) {
			getTracer().report(IReporter.INFO, message);
		}
	}

	public void trace (String message) {
		trace(getTraceOptionId(), message);
	}

	public void trace (int level, String msg) {
		if (isDebugging(getTraceOptionId())) {
			getTracer().report(level, msg);
		}
	}

	public void trace (Object source, String message) {
		trace(getTraceOptionId(), source.getClass().getName() + ": " + message);
	}

	public void trace (String message, Throwable t) {
		if (isDebugging(getTraceOptionId())) {
			getTracer().report(IReporter.ERROR, message, t);
		}
	}

	public void trace (Throwable t) {
		String msg = t != null ? t.getMessage() : "<no message>";
		trace(msg != null && msg.length() > 0 ? msg : "<no message>", t);
	}

	protected String setupDefaultTraceStream () {
		boolean success = true;
		String traceFile = null;
		String traceDir = System.getProperty("java.io.tmpdir") + IConfiguration.ACTF_ID;
		File traceDirFile = new File(traceDir);
		
		if (!traceDirFile.exists()) {
			try {
				success = traceDirFile.mkdir();
			}catch (Exception e) {
				logException("Could not create default trace file directory" + traceDir, e);
				success = false;
			}
		}
		
		if (success) {
			traceFile = traceDir + File.separator + getDefaultTraceFilename();
		}else {
			traceFile = System.getProperty("java.io.tmpdir") + getDefaultTraceFilename();
		}
		
		return traceFile;
	}
	
}