/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  barryf - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.core;

/**
 * @author barryf
 * 
 */
public interface ITracingService
{

	public void trace (String option, String message);

	public void trace (String message);

	public void trace (Object source, String message);

	public void trace (String message, Throwable t);

	public void trace (Throwable t);
}
