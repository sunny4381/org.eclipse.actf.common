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
package org.eclipse.actf.util.httpproxy.core;

/**
 * This exception is thrown when an operation (e.g. read and write) did not
 * completes within a specific time.
 */
public class TimeoutException extends Exception {
	private static final long serialVersionUID = 2697832049495326392L;

	/**
	 * Constructor of the exception.
	 */
	public TimeoutException() {
		super();
	}

	/**
	 * COnstuctor of the exception.
	 * 
	 * @param msg
	 *            exception message
	 */
	public TimeoutException(String msg) {
		super(msg);
	}
}
