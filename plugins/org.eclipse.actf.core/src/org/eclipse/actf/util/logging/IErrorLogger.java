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

/**
 * used to log errors to different destinations such as files, streams, or GUI components. The
 * <code>ErrorLoggerFactory</code> interface should be implemented to return instances of this interface appropriate to different
 * contexts.
 *
 * @see org.eclipse.actf.util.error.ErrorLoggerFactory 
 * @author Mike Squillace
 */
public interface IErrorLogger
{

	/**
	 * log a Throwable
	 * 
	 * @param t - Throwable to log
	 */
	public void logError (Throwable t);

	/**
	 * log an error message
	 * 
	 * @param msg - error message
	 */
	public void logError (String msg);

	/**
	 * log an error message along with its corresponding Throwable. If no message
	 * is specified, the class and <code>toString()</code> of the
	 * Throwable should be logged.
	 * 
	 * @param msg - error message
	 * @param t - Throwable being logged
	 */
	public void logError (String msg, Throwable t);
}
