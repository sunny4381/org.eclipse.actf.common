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

package com.ibm.haac.util.command;

/**
 * An ArgumentResolver is used to resolve arguments passed to switches on the
 * command line. A default resolver is packaged with ACTF that resolves
 * arguments to the primitive types, primitive wrappers, Strings, and Class types.
 * 
 * @author Mike Squillace
 */
public interface ArgumentResolver
{

	/**
	 * resolve the given argument or value to the given type
	 * 
	 * @param arg - argument or value from command line switch
	 * @param type - type to which argument is to be resolved
	 * @return result of resolving argument to class
	 * @throws Exception if argument cannot be resolved to the specified type
	 */
	public Object resolve (String arg, Class type) throws Exception;
} // ArgumentResolver
