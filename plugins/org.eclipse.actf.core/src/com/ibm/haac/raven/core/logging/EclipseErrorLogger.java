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

import com.ibm.haac.raven.core.CorePlugin;

/**
 * @author Mike Squillace
 */
public class EclipseErrorLogger extends AbstractErrorLogger
{

	/** {@inheritDoc} */
	public void logError (String msg, Throwable t) {
		CorePlugin.getDefault().logException(msg, t);
	}
	
}
