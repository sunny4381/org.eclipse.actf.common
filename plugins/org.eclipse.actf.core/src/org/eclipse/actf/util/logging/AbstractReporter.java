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

package org.eclipse.actf.util.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.eclipse.actf.core.runtime.RuntimeContextFactory;



/**
 * base implementation of a Reporter. This class provides default
 * behaviors for the <code>org.eclipse.actf.core.logging.Reporter</code> interface, which
 * adds functionality for identifying the source of reported messages and for
 * rerouting errors and exceptions using the error-logging features added to ACTF.
 *
 *<p><b>Note</b>: Clients should subclass this class rather than implementing <code>Reporter</code> to avoid 
 *API incompatibilities and to insure appropriate behavior.
 *
 * @see org.eclipse.actf.util.logging.IReporter 
 * @author Mike Squillace
 */
public abstract class AbstractReporter implements IReporter
{

	/**
	 * formatter for forming category id strings. The default format is:
	 * 
	 * <pre>
	 * &lt;nodeName&gt;[&lt;nodeDescription&gt;][@id=&lt;nodeId&gt;]
	 * </pre>
	 *
	 * <p>where:
	 * <p><ul>
	 * <li><code>nodeName</code> is the node name of the node as returned by IModel.getNodeName
	 * <li><code>nodeDescription</code> is the node description of the node as returned by IModel.getNodeLocator.describe
	 * <li><code>nodeId</code> is the unique node id of the node as returned by IModel.getNodeId
	 * </ul></p>
	 * 
	 * <p>The category formatter can be changed using the <code>setCategoryFormatter(messageFormat)</code> method.
	 * 
	 * @see #setCategoryFormatter(MessageFormat)
	 * 
	 */
	public static final MessageFormat CATEGORY_FORMATTER = new MessageFormat("{0}[{1}][@id={2}]");
	
	protected static final Map SOURCE_ID_MAP = new HashMap();
	static {
		SOURCE_ID_MAP.put(VALIDATION, "Validator");
		SOURCE_ID_MAP.put(SYSTEM_FATAL, "System");
		SOURCE_ID_MAP.put(SYSTEM_NONFATAL, "Nonfatal");
		SOURCE_ID_MAP.put(USER, "User");
		SOURCE_ID_MAP.put(TRACE, "Tracer");
		SOURCE_ID_MAP.put(CONFIGURATION, "Configuration");
	}

	protected int defaultOutputLevel = UNKNOWN;
	protected Stack categories = new Stack();
	protected MessageFormat categoryFormatter = CATEGORY_FORMATTER;
	protected String sourceID;
	protected ResourceBundle bundle;
	protected List errorLoggers = new LinkedList();

	public AbstractReporter () {
		registerErrorLogger(RuntimeContextFactory.getInstance().getRuntimeContext().getErrorLogger());
	}

	/**
	 * add an error logger to the list of loggers to receive requests to report errors
	 * from this reporter.
	 * 
	 * @param logger - error logger for reporting errors
	 */
	public void registerErrorLogger (IErrorLogger logger) {
		errorLoggers.add(logger);
	}

	/**
	 * remove an error logger from the list of loggers to receive errors
	 * 
	 * @param logger - error logger to be removed
	 */
	public void unregisterErrorLogger (IErrorLogger logger) {
		errorLoggers.add(logger);
	}

	/**
	 * get the underlying resource bundle
	 *
	 * @return bundle
	 */
	public ResourceBundle getResourceBundle () {
		return bundle;
	}

	/**
	 * set the resource bundle
	 *
	 * @param bundle
	 */
	public void setResourceBundle (ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public void setCategoryFormatter (MessageFormat formatter) {
		categoryFormatter = formatter;
	}
	
	public MessageFormat getCategoryFormatter () {
		return categoryFormatter;
	}
	
	/** {@inheritDoc} */
	public void setSourceID (String id) {
		sourceID = id;
	}

	/** {@inheritDoc} */
	public String getSourceID () {
		return sourceID;
	}

	/**
	 * get the source id string associated with the given key. This method will return 
	 * <code>null</code> if one of the pre-defined symbolic constants in the <code>Reporter</code> interface 
	 * are not used.
	 * 
	 * @param id
	 * @return id associated with given key
	 */
	public String getSourceIDString (String id) {
		return (String) SOURCE_ID_MAP.get(sourceID);
	}

	/** {@inheritDoc} */
	public void setDefaultOutputLevel (int level) {
		if (level < MIN_LEVEL || level > MAX_LEVEL) {
			throw new IllegalArgumentException("invalid level: " + level);
		}
		defaultOutputLevel = level;
	}

	/** {@inheritDoc} */
	public int getDefaultOutputLevel () {
		return defaultOutputLevel;
	}

	/** {@inheritDoc} */
	public void report (String message) {
		report(DEFAULT_LEVEL, message, (Object[]) null);
	}

	/** {@inheritDoc} */
	public void report (String message, Object[] values) {
		report(DEFAULT_LEVEL, message, values);
	}

	/** {@inheritDoc} */
	public void report (int level, String message) {
		report(level, message, (Object[]) null);
	}

	/** {@inheritDoc} */
	public void report (Throwable t) {
		report(IReporter.ERROR, "", t);
	}

	/** {@inheritDoc} */
	public void report (int level, Throwable t) {
		report(level, "", t);
	}

	/** {@inheritDoc} */
	public void report (int level, String msg, Throwable t) {
		for (int l = 0; l < errorLoggers.size(); ++l) {
			IErrorLogger logger = (IErrorLogger) errorLoggers.get(l);
			logger.logError(msg, t);
		}
		StringBuffer sb = new StringBuffer(msg == null ? "" : msg);
		sb.append('\n');
		while (t != null) {
			String type = t.getClass().getName();
			String m = t.getMessage() == null ? type : type + ": "
					+ t.getMessage();
			StackTraceElement[] elements = t.getStackTrace();
			sb.append(m);
			sb.append('\n');
			for (int s = 0; s < elements.length; ++s) {
				sb.append("at ");
				sb.append(elements[s].toString());
				sb.append('\n');
			}
			t = t.getCause();
			if (t != null) {
				sb.append('\n');
				sb.append("Caused by: ");
			}
		}
		String s = sb.toString();
		if (s.trim().length() > 0) {
			report(level, s);
		}
	}

	/** {@inheritDoc} */
	public String getCategory () {
		return categories.isEmpty() ? null : (String) categories.peek();
	}

	/** {@inheritDoc} */
	public void startCategory (String id) {
		categories.push(id);
	}

	/** {@inheritDoc} */
	public void endCategory () {
		endCategory(null);
	}

	/** {@inheritDoc} */
	public void endCategory (String id) {
		if (categories.isEmpty()) { throw new IllegalStateException("no active categories"); }
		if (id != null && !id.equals(categories.peek())) { throw new IllegalStateException("category does not match: "
				+ id); }
		categories.pop();
	}

	/**
	 * utility method to construct reporters
	 * 
	 * @param traceLevel - level at which to report
	 * @param traceStream - stream to which report is sent (either a file, 'stderr', or 'stdout')
	 * @return PrintWriterReporter that reports to the given stream
	 */
	public static IReporter getReporter (int traceLevel, String traceStream) {
		int level = traceLevel > IReporter.MAX_LEVEL || traceLevel < IReporter.MIN_LEVEL
			? IReporter.ERROR : traceLevel;
		PrintWriter pw = null;
		IReporter tracer = null;
		
		if (traceStream == null || traceStream.equalsIgnoreCase("stderr")) {
			pw = new PrintWriter(System.err, true);
		}else if (traceStream.equalsIgnoreCase("stdout")) {
			pw = new PrintWriter(System.out, true);
		}else {
			try {
				pw = new PrintWriter(new FileWriter(traceStream), true);
			} catch (IOException e) {
				System.err.println("Could not create reporter using trace stream: " + traceStream + "; logging to System.err");
				pw = new PrintWriter(System.err, true);
			}
		}

		tracer = new PrintWriterReporter(pw);
		tracer.setDefaultOutputLevel(level);
		tracer.open();

		return tracer;
	} // getReporter

	/** {@inheritDoc} */
	public void reset () {
		categories.clear();
	}
	
} // AbstractReporter
