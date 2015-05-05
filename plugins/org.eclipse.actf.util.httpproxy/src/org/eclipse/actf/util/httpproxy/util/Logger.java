/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Utility class for logging.
 */
public class Logger {
	private static final String COMMA_SPACE = ", "; //$NON-NLS-1$

	/**
	 * @param name
	 */
	public static void setConfigPropertyName(String name) {
		PROP_LOGGING_CONFIGURATION = name;
	}

	private static String PROP_LOGGING_CONFIGURATION = "httpproxy.conf.logging"; //$NON-NLS-1$
	public static final String DEFAULT_LOGGING_CONFIGURATION = "logging.conf"; //$NON-NLS-1$

	/**
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(Class clazz) {
		return new Logger(clazz);
	}

	/**
	 * @throws Exception
	 */
	public static void configure() throws Exception {
		String config = System.getProperty(PROP_LOGGING_CONFIGURATION,
				DEFAULT_LOGGING_CONFIGURATION);
		Properties props = new Properties();
		Exception exception = null;
		if (config != null) {
			try {
				FileInputStream in = new FileInputStream(config);
				java.util.logging.LogManager.getLogManager().readConfiguration(
						in);

				in = new FileInputStream(config);
				props.load(in);

				//exception = null;
			} catch (FileNotFoundException e) {
				// System.err.println("WARNING: File not found " + config);
				exception = e;
			} catch (IOException e) {
				// System.err.println("WARNING: Cannot read file " + config);
				exception = e;
			}
		}

		java.util.logging.Logger l = java.util.logging.Logger.getLogger(""); //$NON-NLS-1$
		l.setLevel(Level.ALL);

		// Set a FileHandler... disabled now. We can use Chainsaw or some other
		// log viewers via network.
		// setFileHandler(l, name, props);

		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * @param configIS
	 * @throws Exception
	 */
	public static void configure(InputStream configIS) throws Exception {
		Exception exception = null;
		if (configIS != null) {
			try {
				java.util.logging.LogManager.getLogManager().readConfiguration(
						configIS);
				//exception = null;
			} catch (FileNotFoundException e) {
				// System.err.println("WARNING: File not found " + config);
				exception = e;
			} catch (IOException e) {
				// System.err.println("WARNING: Cannot read file " + config);
				exception = e;
			}
		}

		java.util.logging.Logger l = java.util.logging.Logger.getLogger(""); //$NON-NLS-1$
		l.setLevel(Level.ALL);

		if (exception != null) {
			throw exception;
		}
	}

	/*
	 * private static void setFileHandler(java.util.logging.Logger l, String
	 * name, Properties props) throws IOException { String pattern =
	 * props.getProperty("java.util.logging.FileHandler.pattern",
	 * "logs/%s_%u.log"); pattern = pattern.replaceAll("%s", name); Handler
	 * fileHandler = new FileHandler(pattern); String formatterName =
	 * props.getProperty("java.util.logging.FileHandler.formatter"); if
	 * (formatterName != null) { try { Class formatterClass =
	 * Class.forName(formatterName); Object o = formatterClass.newInstance(); if
	 * (o instanceof Formatter) { fileHandler.setFormatter((Formatter) o); } }
	 * catch (Exception e) { // ignore } } //fileHandler.setLevel(Level.ALL);
	 * //l.addHandler(fileHandler); final MemoryHandler mh = new
	 * MemoryHandler(fileHandler, 1000, Level.ALL); l.addHandler(mh);
	 * 
	 * Timer timer = new Timer(true); timer.schedule(new TimerTask() { public
	 * void run() { mh.push(); mh.flush(); } }, 0, 1000L); }
	 */

	private final java.util.logging.Logger fDelegate;

	@SuppressWarnings("rawtypes")
	private Logger(Class clazz) {
		fDelegate = java.util.logging.Logger.getLogger(clazz.getName());
	}

	/**
	 * @return
	 */
	public final boolean isDebugEnabled() {
		return fDelegate.isLoggable(Level.FINE);
	}

	/**
	 * @return
	 */
	public final boolean isMethodTracingEnabled() {
		return fDelegate.isLoggable(Level.FINER);
	}

	private final String createMessage(String msg) {
		StringBuffer sb = new StringBuffer();
		// sb.append('[');
		// sb.append(fName);
		// sb.append("] ");
		sb.append(msg);
		return sb.toString();
	}

	/**
	 * Write a log message for serious situation.
	 * 
	 * @param msg
	 */
	public void fatal(String msg) {
		fDelegate.severe(createMessage(msg));
	}

	/**
	 * @param msg
	 * @param e
	 */
	public void fatal(String msg, Throwable e) {
		StringBuffer sb = new StringBuffer();
		sb.append(msg);
		sb.append(COMMA_SPACE);
		sb.append(dumpStackTrace(e));
		fDelegate.severe(sb.toString());
	}

	/**
	 * Write a log message notifying a potential problem.
	 * 
	 * @param msg
	 */
	public void warning(String msg) {
		fDelegate.warning(createMessage(msg));
	}

	/**
	 * @param msg
	 * @param e
	 */
	public void warning(String msg, Throwable e) {
		StringBuffer sb = new StringBuffer();
		sb.append(msg);
		sb.append(COMMA_SPACE);
		sb.append(dumpStackTrace(e));
		fDelegate.warning(sb.toString());
	}

	/**
	 * Write a log message for informational messages.
	 * 
	 * @param msg
	 */
	public void info(String msg) {
		fDelegate.info(createMessage(msg));
	}

	/**
	 * Write a log message for debug.
	 * 
	 * @param msg
	 */
	public void debug(String msg) {
		fDelegate.fine(createMessage(msg));
	}

	/**
	 * @param msg
	 * @param e
	 */
	public void debug(String msg, Throwable e) {
		StringBuffer sb = new StringBuffer();
		sb.append(msg);
		sb.append(COMMA_SPACE);
		sb.append(dumpStackTrace(e));
		fDelegate.fine(sb.toString());
	}

	private StringBuffer dumpStackTrace(Throwable e) {
		StringWriter w = new StringWriter();
		PrintWriter pw = new PrintWriter(w);
		e.printStackTrace(pw);
		pw.flush();
		return w.getBuffer();
	}
}
