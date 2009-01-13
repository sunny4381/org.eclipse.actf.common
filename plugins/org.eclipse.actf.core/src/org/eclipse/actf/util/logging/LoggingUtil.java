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

package org.eclipse.actf.util.logging;


/**
 * A set of static methods utilized by the IBM Reflexive GUI Builder.
 * All parameters for the trace stream and level are also set
 * and maintaned by this class
 *
 * @author Mike Squillace
 */
public final class LoggingUtil
{

	
	/**
	 * Logger names for the main plugins
	 */
	public static final String ACCSERVICE_LOGGER_NAME = "org.eclipse.actf.accservice";
	public static final String ACTF_CORE_LOGGER_NAME = "org.eclipse.actf.core";
	public static final String ACTF_VALIDATION_LOGGER_NAME = "org.eclipse.actf.validation";
	public static final String ACTF_JAVACO_LOGGER_NAME = "org.eclipse.actf.javaco";
	public static final String ACTF_WEBELO_LOGGER_NAME = "org.eclipse.actf.webelo";
	public static final String ACTF_UI_LOGGER_NAME = "org.eclipse.actf.ui";
	
	/** only fatal errors and exception messages are printed */
	public static final int NONE = IReporter.SEVERE;

	/** only fatal errors and exception messages are printed */
	public static final int FATAL = IReporter.SEVERE;

	/** only errors are printed */
	public static final int ERRORS = IReporter.ERROR;

	/** warnings and errors only */
	public static final int WARNINGS = IReporter.WARNING;

	/** information regarding the GUI construction and validation process
	 * including nodes being built and object instantiations
	 */
	public static final int PROCESS_INFO = IReporter.INFO;

	/** information reported at previous levels + information about executable
	 *code and more details about the algorithm of construction
	 */
	public static final int PROCESS_ALL = IReporter.DETAIL;

	/**
	 * Highest trace level: all information regarding process of GUI construction and
	 * execution and validation; includes:
	 *
	 * <p><ul>
	 *   <li>warnings and errors from interpreter engine and validator (if present and active)
	 *   <li>information regarding GUI construction (e.g. steps of algorithm,
	 *   nodes being built)
	 *   <li>level 2 information + execution of code blocks and
	 *   notices about instantiations of objects
	 *   <li>all level 3 information + constructors and methods being
	 *   considered and arguments to chosen constructors and methods
	 * </ul>
	 */
	public static final int ALL = IReporter.CONFIG;

	private static IReporter _tracer = AbstractReporter.getReporter(
		IReporter.ERROR, "stdout");
	static {
		_tracer.setSourceID(IReporter.TRACE);
	}

	protected LoggingUtil () {
	}

	/**
	 * set the tracer using the given level and stream.
	 * The stream may be one of "stderr", "stdout", or a filename and
	 * the level parameter should be one of the public static field members of 
	 * <code>org.eclipse.actf.core.logging.Reporter</code>. The trace is always an instance of
	 * <code>org.eclipse.actf.core.logging.PrintWriterReporter</code>.
	 *
	 * @param traceLevel -- a level between <code>NONE</code> and <code>ALL</code>
	 * @param traceStream -- one of stdout, stderr, or filename
	 */
	public static void setTracer (int traceLevel, String traceStream) {
		_tracer = AbstractReporter.getReporter(traceLevel, traceStream);
		if (_tracer instanceof PrintWriterReporter) {
			((PrintWriterReporter) _tracer).setDefaultOutputLevel(traceLevel);
		}
		_tracer.setSourceID(IReporter.TRACE);
		_tracer.open();
	} // setTracer

	/**
	 * set the trcer with the given reporter
	 *
	 * @param reporter -- reporter to be used as tracer
	 */
	public static void setTracer (IReporter reporter) {
		_tracer = reporter;
		_tracer.setSourceID(IReporter.TRACE);
	}

	/**
	 * returns the active tracer
	 *
	 * @return active tracer
	 */
	public static IReporter getTracer () {
		return _tracer;
	}
	
	//Closing the _tracer which is opened by setTracer.
	public static void closeTracer() {
		if(_tracer.isOpen()) {
			_tracer.close();
		} 
	}

	/**
	 * get the current trace level
	 *
	 * @return current trace level
	 */
	public static int getTraceLevel () {
		return _tracer.getDefaultOutputLevel();
	}

	/**
	 * alert tracer that a new process is beginning.
	 * A new process will be indicated in the trace stream with the given id appearing
	 * in square brackets ([...]) before any message.
	 *
	 * @param id -- process id
	 */
	public static void startSubprocess (String id) {
		if (_tracer != null) {
			_tracer.startCategory(id);
		}
	}

	/**
	 * alert tracer that a process is ending.
	 * Processes are maintaned on a stack so that the last process initiated with
	 * the startSubprocess method will be terminated.
	 *
	 * @see #startSubprocess
	 */
	public static void endSubprocess () {
		if (_tracer != null) {
			_tracer.endCategory();
		}
	}

	/**
	 * print the given information according to the given level.
	 * All information associated with a level greater than or
	 *equal to the set trace level will be printed
	 *
	 * @param level -- level of given information
	 * @param str -- information to be printed
	 */
	public static void println (int level, String str) {
		_tracer.report(level, str);
	}

	/**
	 * output an error. The underlying reporter/tracer will
	 * use the appropriate error logger to display the error and, if desired, the stack trace.
	 * 
	 * @param errorKey - error key as defined by public fields in <code>org.eclipse.actf.core.logging.Reporter</code>
	 * @param msg - message
	 * @see org.eclipse.actf.util.logging.IReporter#setSourceID(String)
	 */
	public static void println (String errorKey, String msg) {
		println(errorKey, msg, null);
	}

	/**
	 * output an error. The underlying reporter/tracer will
	 * use the appropriate error logger to display the error and, if desired, the stack trace.
	 * 
	 * @param errorKey - error key as defined by public fields in <code>org.eclipse.actf.core.logging.Reporter</code>
	 * @param t - Throwable
	 * @see org.eclipse.actf.util.logging.IReporter#setSourceID(String)
	 */
	public static void println (String errorKey, Throwable t) {
		println(errorKey, null, t);
	}

	/**
	 * output an error. The underlying reporter/tracer will
	 * use the appropriate error logger to display the error and, if desired, the stack trace.
	 * 
	 * @param errorKey - error key as defined by public fields in <code>org.eclipse.actf.core.logging.Reporter</code>
	 * @param msg -message
	 * @param t - Throwable
	 * @see org.eclipse.actf.util.logging.IReporter#setSourceID(String)
	 */
	public static void println (String errorKey, String msg, Throwable t) {
		String oldSource = _tracer.getSourceID();
		_tracer.setSourceID(errorKey);
		if (t != null) {
			_tracer.report(IReporter.ERROR, msg, t);
		}else {
			_tracer.report(IReporter.ERROR, msg);
		}
		_tracer.setSourceID(oldSource);
	}

	/**
	 * print the members of the given array.
	 * This method is typically used in the trace by methods that wish to
	 * output the list of parameters given to a constructor
	 * or method.
	 *
	 * @param level -- level at which to print
	 * @param params -- parameters to be printed
	 */
	public static void printParams (int level, Object[] params) {
		if (_tracer != null) {
			_tracer.report(level, "With parameters:");
			if (_tracer.getDefaultOutputLevel() <= level) {
				for (int p = 0; params != null && p < params.length; ++p) {
					_tracer.report(level, (params[p] == null ? ""
							: params[p].getClass().getName())
							+ ":"
							+ params[p]
							+ (p < params.length - 1 ? ", " : ""));
				}
			}
		}
	} // printParams

	private static int findClosingSymbol (String argStr, int start,
											char openingSymbol) {
		int open = 1, index;
		char closingSymbol;
		if (openingSymbol == '(') {
			closingSymbol = ')';
		}else if (openingSymbol == '[') {
			closingSymbol = ']';
		}else if (openingSymbol == '{') {
			closingSymbol = '}';
		}else {
			closingSymbol = openingSymbol;
		}
		for (index = start; index < argStr.length() & open != 0; ++index) {
			if (argStr.charAt(index) == openingSymbol) {
				++open;
			}else if (argStr.charAt(index) == closingSymbol) {
				--open;
			}
		}
		if (open != 0) {
			println(ERRORS, "Mismatched symbol " + openingSymbol
					+ " in argument string: " + argStr);
			index = -1;
		}
		return index;
	} // findClosingSymbol

	/**
	 * converts the first character of the given string to uppper case
	 *
	 * @param str -- a string
	 * @return str with its first character converted to upper case
	 */
	public static String firstCharToUpper (String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String arrayAsString (String[] str) {
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < str.length; ++a) {
			sb.append(str[a]);
			if (a < str.length - 1) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	/**
	 * retreave the root exception of the given Throwable
	 * @param t - throwable
	 * @return root exception of t or t if t has no cause
	 */
	public static Throwable getRootException (Throwable t) {
		Throwable root = t;
		while ((t = t.getCause()) != null) {
			root = t;
		}
		return root;
	}

	/**
	 * Replace XML specific characters with entities.
	 * @param s text to fix
	 */
	public static String escape (String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\"') {
				sb.append("&quote;");
			}else if (c == '\'') {
				sb.append("&apos;");
			}else if (c == '&') {
				sb.append("&amp;");
			}else if (c == '<') {
				sb.append("&lt;");
			}else if (c == '>') {
				sb.append("&gt;");
			}else if (c < ' ' || c > 0x7E) {
				sb.append("&#");
				String xi = Integer.toHexString(c);
				if (xi.length() % 2 != 0) {
					sb.append("0");
				}
				sb.append(xi);
				sb.append(";");
			}else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
} // Utils
