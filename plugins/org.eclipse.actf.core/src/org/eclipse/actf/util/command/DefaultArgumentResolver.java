/*******************************************************************************
* Copyright (c) 2004, 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.command;

import java.lang.reflect.Array;
import java.util.StringTokenizer;

/**
 * This default implementation resolves arguments to primitive values, instances of wrappers of
 * primitive types, <code>String</code> instances, and <code>Class</code> instances. It
 * will also resolve single-dimensional arrays of primitives, strings, and Class objects.
 * 
 * @author Mike Squillace
 */
public class DefaultArgumentResolver implements IArgumentResolver
{

	/**
	 * create a default implementation for resolving arguments to switches on the
	 * command line. 
	 */
	public DefaultArgumentResolver () {
	}

	/** {@inheritDoc} */
	public Object resolve (String arg, Class type) throws Exception {
		Object result = null;
		if (arg != null) {
			if (type.isArray()) {
				result = resolveArray(arg, type.getComponentType());
			}else if (type.equals(String.class)) {
				result = arg;
			}else if (type.equals(Class.class)) {
				try {
					result = Class.forName(arg);
				}catch (Exception e) {
					throw new IllegalArgumentException("Illegal class name for switch:"
							+ arg + " - " + e);
				}
			}else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
				result = Short.valueOf(arg);
			}else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
				result = Integer.valueOf(arg);
			}else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
				result = Long.valueOf(arg);
			}else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
				result = Float.valueOf(arg);
			}else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
				result = Double.valueOf(arg);
			}else if (type.equals(Character.TYPE)
					|| type.equals(Character.class)) {
				result = new Character(arg.charAt(0));
			}
		}
		return result;
	} // resolve

	protected Object resolveArray (String arg, Class compType) throws Exception {
		if (compType.isArray()) { throw new IllegalArgumentException("Default impl of argument resolver does not support multi-dimensional arrays"); }
		StringTokenizer st = new StringTokenizer(arg);
		Object array = Array.newInstance(compType, st.countTokens());
		int index = 0;
		while (st.hasMoreTokens()) {
			Array.set(array, index++, resolve(st.nextToken(), compType));
		}
		return array;
	}
} // DefaultArgumentResolver
