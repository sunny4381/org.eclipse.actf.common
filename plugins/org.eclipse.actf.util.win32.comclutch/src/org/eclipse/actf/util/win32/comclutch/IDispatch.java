/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.win32.comclutch;

/**
 * Wrapper for IDispatch object see
 * http://msdn.microsoft.com/en-us/library/ms221608.aspx
 * 
 * IDispatch interface defines the methods to be used for method invocation and
 * property operations with instances of native IDispatch.
 */
public interface IDispatch extends IUnknown {
	/**
	 * cache IDs corresponding to the names
	 * 
	 * @param names
	 *            the names of properties and methods
	 */
	void cacheDispIDs(String[] names);

	/**
	 * @param method
	 *            the method name
	 * @param args
	 *            the array of arguments
	 * @return the result of the native invocation
	 */
	Object invoke(String method, Object[] args);

	/**
	 * @param method
	 *            the method name
	 * @return the result of the native invocation
	 */
	Object invoke0(String method);

	/**
	 * @param method
	 *            the method name
	 * @param arg1
	 *            the argument
	 * @return the result of the native invocation
	 */
	Object invoke1(String method, Object arg1);

	/**
	 * @param prop
	 *            the property name
	 * @return the value of the property
	 */
	Object get(String prop);

	/**
	 * @param prop
	 *            the property name
	 * @param val
	 *            the value to be set to the property
	 */
	void put(String prop, Object val);

	/**
	 * @param ptr
	 * @return It will be called from native code
	 */
	IDispatch newIDispatch(long ptr);

	/**
	 * @param prop
	 *            the property name
	 * @param args
	 *            the array of arguments
	 * @return the value of the property
	 */
	public Object get(String prop, Object[] args);
}
