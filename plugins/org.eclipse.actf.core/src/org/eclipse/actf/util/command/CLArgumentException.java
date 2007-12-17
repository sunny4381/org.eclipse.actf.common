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

package org.eclipse.actf.util.command;

public class CLArgumentException extends Exception
{

	/**
	 * 
	 */
	public CLArgumentException () {
		super();
	}

	/**
	 * @param message
	 */
	public CLArgumentException (String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CLArgumentException (String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public CLArgumentException (Throwable cause) {
		super(cause);
	}
} // CLArgumentException
