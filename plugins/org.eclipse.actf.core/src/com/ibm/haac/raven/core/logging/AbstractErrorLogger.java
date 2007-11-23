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


/**
 * a base from which error loggers can be built. Extending this class, clients need only
 * implement <code>logError(String, Throwable)</code>.
 * 
 * @author Mike Squillace
 */
public abstract class AbstractErrorLogger implements IErrorLogger
{

	/** {@inheritDoc} */
	public void logError (Throwable t) {
		logError(t.getMessage() != null ? t.getMessage() : "<no message>", t);
	}

	/** {@inheritDoc} */
	public void logError (String msg) {
		logError(msg, null);
	}
}
