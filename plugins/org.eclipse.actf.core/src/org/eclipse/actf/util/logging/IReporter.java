/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Barry Feigenbaum  - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.logging;


/**
 * interface to be implemented in order to generate reports for tracing or validation during the execution of
 * the ACTF application. Typically, reports are configured with a stream or socket but see the
 * <code>RecordReporter</code>, which can be used to store generated reports for later organization.
 *
 * @author Barry Feigenbaum 
 */
public interface IReporter
{

	int SEVERE = 5;
	int ERROR = 4;
	int WARNING = 3;
	int INFO = 2;
	int DETAIL = 1;
	int CONFIG = 0;

	int UNKNOWN = -1;
	int DEFAULT_LEVEL = ERROR;
	int MAX_LEVEL = SEVERE;
	int MIN_LEVEL = CONFIG;

	final String[] KEYS = {
			"level.CONFIG", "level.DETAIL", "level.INFO", "level.WARNING",
			"level.ERROR", "level.SEVERE"
		};

	public static final String VALIDATION = "validation";
	public static final String SYSTEM_FATAL = "system";
	public static final String SYSTEM_NONFATAL = "nonfatal";
	public static final String USER = "user";
	public static final String TRACE = "trace";
	public static final String CONFIGURATION = "configuration";

	/**
	 * set the default output level. This level determines the messages that are to be
	 * printed or logged. Messages reported at levels equal to or greater than this level
	 * are printed or logged whereas messages reported at levels lower than
	 * this default level are ignored by this reporter.
	 * 
	 *@param level - default output level
	 */
	public void setDefaultOutputLevel (int level);

	/**
	 * get the default output level.
	 * 
	 *@return default output level
	 *@see #setDefaultOutputLevel(int)
	 */
	public int getDefaultOutputLevel ();

	/**
	 * Open for processing
	 */
	public void open ();

	/**
	 * returns whether or not this reporter is open
	 *
	 * @return <code>true</code> if has been opened, <code>false</code> otherwise
	 * @see #open()
	 */
	public boolean isOpen ();

	/**
	 * close processing
	 */
	public void close ();

	/**
	 * Flush any pending output
	 */
	void flush ();

	/**
	 * report the message
	 *
	 * @param message -- message to be written
	 */
	public void report (String message);

	/**
	 * report an information message using the substitution values
	 *
	 * @param message -- message to be reported
	 * @param values -- substitution values
	 */
	public void report (String message, Object[] values);

	/**
	 * report the message at the given level.
	 * Messages are reported if the given level is greater than or equal to
	 * the set or default level.
	 *
	 * @param level -- level of message
	 * @param message -- message to be reported
	 */
	public void report (int level, String message);

	/**
	 * report the message at the given level with the given substitution values
	 *
	 * @param level
	 * @param message
	 * @param values
	 * @see #report(int, String)
	 */
	public void report (int level, String message, Object[] values);

	/**
	 * get the last reported message
	 *
	 * @return last reported message
	 */
	public String getLastReport ();

	/**
	 * get the category id string of current reporting
	 *
	 * @return current category id
	 */
	public String getCategory ();

	/**
	 * start a new category with the given id.
	 *
	 * @param id -- new category id
	 */
	public void startCategory (String id);

	/**
	 * end the current reporting category
	 */
	public void endCategory ();

	/**
	 * end the category with the given id
	 *
	 * @param id -- id of category to end
	 */
	public void endCategory (String id);

	/**
	 * add an error logger to the list of loggers to receive requests to report errors
	 * from this reporter.
	 * 
	 * @param logger - error logger for reporting errors
	 */
	public void registerErrorLogger (IErrorLogger logger);

	/**
	 * remove an error logger from the list of loggers to receive errors
	 * 
	 * @param logger - error logger to be removed
	 */
	public void unregisterErrorLogger (IErrorLogger logger);

	/**
	 * set the source id for future reports. 
	 * If no id is set, the word 'level' precedes
	 * the level of the violation.
	 *
	 * <p>The id can be one of the following:
	 * <p><ul>
	 * <li><code>VALIDATION</code> - validation violation
	 *<li><code>USER</code> - user error (i.e. can be remedied by user) 
	 * <li><code>SYSTEM_FATAL</code> - fatal system error (e.g. resource not found)
	 * <li><code>SYSTEM_NONFATAL</code> - nonfatal system error (i.e. process can continue but results are suspect)
	 * <li><code>TRACE</code> - message related to tracing or debugging 
	 * <li><code>CONFIGURATION</code> - message related to configuration 
	 * </ul></p>
	 * 
	 * @param name - name or id of this reporter
	 */
	public void setSourceID (String name);

	/**
	 * gets the id or name of this reporter
	 * 
	 * @return name or id of this reporter
	 * @see #setSourceID(String)
	 */
	public String getSourceID ();

	/**
	 * reports an exception or error. This method can be used to direct the stack trace
	 * of the given throwable to the appropriate output for this reporter (e.g. a GUI text area
	 * or stream).
	 * 
	 * <p>The default level of this report message is <code>Reporter.SEVERE</code>.
	 * 
	 * @param t - Throwable being reported
	 */
	public void report (Throwable t);

	/**
	 * reports an exception or error. This method can be used to direct the stack trace
	 * of the given throwable to the appropriate output for this reporter (e.g. a GUI text area
	 * or stream). The level of the throwable is determined by the
	 * specified level parameter.
	 *
	 * @param level - level of exception or error 
	 * @param t - Throwable being reported
	 */
	public void report (int level, Throwable t);

	/**
	 * reports an exception or error. This method can be used to direct the stack trace
	 * of the given throwable to the appropriate output for this reporter (e.g. a GUI text area
	 * or stream). The level of the throwable is determined by the
	 * specified level parameter.
	 *
	 * @param level - level of exception or error
	 * @param msg - optional message 
	 * @param t - Throwable being reported
	 */
	public void report (int level, String msg, Throwable t);

	/**
	 * start a new report. Every call of this method must be matched with a
	 * corresponding <code>endReport</code> call.
	 * 
	 * @param comp - root component for which report is being generated
	 */
	public void startReport (Object comp);

	/**
	 * ends a report started with <code>startReport</code>. Once this method is called, the <code>getLastReport</code>
	 * method can be called in order to supply the report generated betwen the <code>startReport</code> call and
	 * this call.
	 */
	public void endReport ();

	/**
	 * set the last report on this reporter. This method is only used in <code>ValidationLauncher.DISTINCT_JVM</code>
	 * after the launcher gets an indication that a report has completed.
	 * 
	 * @param reportText - last report generated
	 */
	public void setLastReport (String reportText);

	/**
	 * reset the reporter. This method should clear all categories and all remaining
	 * buffers and report records.
	 */
	public void reset ();
} // Reporter
