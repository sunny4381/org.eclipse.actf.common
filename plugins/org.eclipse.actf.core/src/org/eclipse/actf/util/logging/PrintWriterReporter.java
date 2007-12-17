/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Barry Feigenbaum - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.logging;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * implementation to generate report records to the specified <code>PrintWriter</code> object.
 * 
 * @author Barry Feigenbaum
 */
public class PrintWriterReporter extends AbstractReporter
{

	// The following two variables are used to add the Class name, method and line
	// number into the trace buffer. 
	// _addClassAndMethod turns on/off the addition of the this info to the trace.
	private boolean _addClassAndMethod = false;

	// _stackDepth determines how far into the stack to go to get the calling 
	// method. This is an approximation as to where the trace statement actually
	// is in the source code. Some paths through the system may be deeper. 
	private int _stackDepth = 6;

	/** {@inheritDoc} */
	public void reset () {
		super.reset();
		formatBuffer = new StringBuffer();
	}

	protected PrintWriter writer;

	/**
	 * get the PrintWriter being used by this Reporter
	 *
	 * @return underlying PrintWriter
	 */
	public PrintWriter getPrintWriter () {
		return writer;
	}

	protected int[] levelCount = new int[MAX_LEVEL - MIN_LEVEL + 1];

	/**
	 * return the number of errors reported thus far by this reporter.
	 * 
	 * @return number of errors reported
	 */
	public int getErrorCount () {
		return levelCount[IReporter.ERROR];
	}

	/**
	 * return the number of warnings reported thus far by this reporter.
	 * 
	 * @return number of warnings reported
	 */
	public int getWarningCount () {
		return levelCount[IReporter.WARNING];
	}

	/**
	 * return the number of messages at the given level reported thus far by this reporter.
	 *
	 * @param level - level of messages to be counted  
	 * @return number of messages at the given level reported
	 */
	public int getLevelCount (int level) {
		return level >= MIN_LEVEL && level <= MAX_LEVEL ? levelCount[level] : 0;
	}

	/**
	 * reset the counts to 0.
	 */
	public void resetCount () {
		for (int l = 0; l < (MAX_LEVEL - MIN_LEVEL + 1); ++l) {
			levelCount[l] = 0;
		}
	}

	/**
	 * create a new reporter using the given PrintWriter
	 *
	 * @param writer -- underlying PrintWriter
	 */
	public PrintWriterReporter (PrintWriter writer) {
		super();
		this.writer = writer;
		this.defaultOutputLevel = UNKNOWN;
	}

	/**
	 * create a new reporter using the given PrintWriter
	 *
	 * @param writer -- underlying PrintWriter
	 * @param format -- message formatter
	 * @param bundle
	 */
	public PrintWriterReporter (PrintWriter writer, ResourceBundle bundle) {
		this(writer);
		this.bundle = bundle;
	}

	/** {@inheritDoc} */
	public String getLastReport () {
		return formatBuffer.toString();
	}

	public String toString () {
		StringBuffer sb = new StringBuffer(getClass().getName());
		sb.append('[');
		sb.append(sourceID == null ? "<None>" : sourceID);
		sb.append(isOpen() ? "open" : "closed");
		sb.append(",\"");
		sb.append(getLastReport());
		sb.append("\",\"");
		sb.append("\"]");
		return sb.toString();
	}

	protected boolean open;

	/** {@inheritDoc} */
	public void open () {
		open = true;
	}

	/** {@inheritDoc} */
	public boolean isOpen () {
		return open;
	}

	/** {@inheritDoc} */
	public void close () {
		if (isOpen()) {
			flush();
			if (!categories.isEmpty()) { throw new IllegalStateException("categories active: "
					+ categories.size()); }
			open = false;
		}
	}

	protected StringBuffer formatBuffer = new StringBuffer();

	protected StringBuffer lastReportBuffer;

	protected boolean reportStarted;

	/** {@inheritDoc} */
	public void report (int level, String message, Object[] values) {
		synchronized (this) {
			if (!isOpen()) { throw new IllegalStateException("not open"); }
			if (defaultOutputLevel == UNKNOWN || level >= defaultOutputLevel) {
				formatBuffer.setLength(0);
				if (bundle != null) {
					try {
						message = bundle.getString(message);
					}catch (MissingResourceException mre) {
						// do nothing
					}
				}
				if (level != UNKNOWN) {
					if (level < MIN_LEVEL || level > MAX_LEVEL) { throw new IllegalArgumentException("invalid level: "
							+ level); }
					int lastDot = IReporter.KEYS[level].lastIndexOf('.');
					String prefix = sourceID == null ? IReporter.KEYS[level].substring(
						0, lastDot) : sourceID;
					String levelStr = bundle != null ? bundle.getString(IReporter.KEYS[level])
							: IReporter.KEYS[level].substring(lastDot + 1);
					formatBuffer.append(prefix + "." + levelStr);
					formatBuffer.append(": ");
				}
				if (!categories.isEmpty()) {
					formatBuffer.append("[" + categories.peek() + "]");
					formatBuffer.append(" ");
				}
				if (getSourceID().equals(IReporter.TRACE)) {
					formatBuffer.append("\t["
							+ new Date(System.currentTimeMillis()) + "], ");
					formatBuffer.append("\t" + getThreadInfo());
					if (_addClassAndMethod) {
						formatBuffer.append("\n\t" + getMethodNameFromStack()
								+ " \n\t");
					}
				}
				if (values != null && values.length > 0) {
					if (message != null && message.length() > 0) {
						formatBuffer.append(MessageFormat.format(message, values));
					}
				}else {
					if (message != null && message.length() > 0) {
						formatBuffer.append(message);
					}
				}
				writer.println(formatBuffer.toString());
				if (reportStarted) {
					lastReportBuffer.append(formatBuffer.toString());
					lastReportBuffer.append('\n');
				}
				++levelCount[level];
			}
		}
	} // report

	/** {@inheritDoc} */
	public void flush () {
		if (!isOpen()) { throw new IllegalStateException("not open"); }
		writer.flush();
	}

	public boolean isReportStarted () {
		return reportStarted;
	}

	/** {@inheritDoc} */
	public void startReport (Object comp) {
		if (!reportStarted) {
			reportStarted = true;
			lastReportBuffer = new StringBuffer();
		}
	}

	/** {@inheritDoc} */
	public void endReport () {
		if (!reportStarted) { throw new IllegalStateException("Report has not been started on this reporter"); }
		reportStarted = false;
	}

	/** {@inheritDoc} */
	public void setLastReport (String reportText) {
		lastReportBuffer = new StringBuffer(reportText);
	}

	/**
	 * Get info about the calling Thread.
	 *  
	 * @return String the id and name of the Thread.
	 */
	public String getThreadInfo () {
		Thread t = Thread.currentThread();
		//Use this code in JDK 1.5
		//return "[Thread id - " + t.getId() + ", name - " + t.getName() + "]";
		//Use this code in JDK 1.4
		return "[Thread name - " + t.getName() + "]";
	}

	/**
	 * Get the class and method name that called PrintUtils.println(). In order
	 * to get the caller to PrintUtils.println() we need to go 6 deep into the 
	 * stack.
	 *  
	 * @return String the class and method name.
	 */
	public String getMethodNameFromStack () {
		return getMethodNameFromStack(_stackDepth);
	}

	/**
	 * Returns the class and method name from a line in the stack.
	 * 
	 * @param numLinesToIgnore number of lines in the stack trace to ignore
	 * @return String the class and method name from the specific line in the stack trace.
	 */
	public String getMethodNameFromStack (int numLinesToIgnore) {
		// Create a Throwable to get the stack infomation.
		Throwable t = new Throwable();
		// Print the stack into a byte array.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		t.printStackTrace(new PrintWriter(baos, true));
		byte[] stackTraceBytes = baos.toByteArray();
		String line = null;
		// Read each line of the byte array until we hit the line we're interested in.
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(stackTraceBytes);
			BufferedReader in = new BufferedReader(new InputStreamReader(bais));
			// Skip the specified number of lines in the stack trace until we reach the
			// line that contains the info for our calling method.
			in.readLine(); // always ignore the first line
			for (int i = 0; i < numLinesToIgnore - 1; i++) {
				in.readLine(); // clear lines that we don't need
			}
			line = in.readLine();
			in.close();
		}catch (IOException e) {
			return "Unknown method";
		}
		// Trim the stack trace line to eliminate the "at " at the beginning.       
		String methodName = line.substring(4, line.length());
		return methodName;
	}

	/** {@inheritDoc} */
	public void addReportRecords (List records) {
		// TODO need to refactor this.
		// This method is necessary because the Reporter interface requires it.
		// However, it doesn't make sense here because there are no report 
		// records for this implementation. 
		// It may be better to refactor the AbstractReporter to have the list
		// of report records.
	}
}
