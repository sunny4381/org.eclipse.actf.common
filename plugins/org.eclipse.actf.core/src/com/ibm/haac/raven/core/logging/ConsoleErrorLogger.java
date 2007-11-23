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

package com.ibm.haac.raven.core.logging;

import java.io.PrintStream;


/**
 * general logger for writing to the console
 * 
 * @author Mike Squillace
 */
public class ConsoleErrorLogger extends AbstractErrorLogger
{

	private PrintStream _stream;

	/**
	 * create a console error logger that writes to <code>System.err</code>
	 * 
	 *
	 */
	public ConsoleErrorLogger () {
		this(System.err);
	}

	/**
	 * create an error logger for writing to the console
	 * 
	 * @param stream either <code>System.out</code> or <code>System.err</code>
	 */
	public ConsoleErrorLogger (PrintStream stream) {
		_stream = stream;
	}

	/** {@inheritDoc} */
	public void logError (String msg, Throwable t) {
		if (msg != null) {
			_stream.println(msg);
		}
		if (t != null) {
			_stream.println(t.getClass().getName() + " - " + t.toString());
			t.printStackTrace(_stream);
			while ((t = t.getCause()) != null) {
				_stream.println(t.getClass().getName() + " - " + t.toString());
				t.printStackTrace(_stream);
			}
		}
	}

}
