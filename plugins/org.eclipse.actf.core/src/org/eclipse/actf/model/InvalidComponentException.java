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

package org.eclipse.actf.model;

public class InvalidComponentException extends Exception
{

	public InvalidComponentException () {
		super();
	}

	public InvalidComponentException (String message) {
		super(message);
	}

	public InvalidComponentException (String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidComponentException (Throwable cause) {
		super(cause);
	}
}
