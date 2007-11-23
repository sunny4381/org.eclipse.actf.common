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

package com.ibm.haac.raven.core.adapt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.haac.raven.core.resources.ClassLoaderCache;

/**
 * default implementation for creating adaptors in ACTF. Clients should subclass this implementation rather than 
 * implementing <code>IAdaptorFactory</code>.
 * 
 * @author Mike Squillace
 *
 */
public class DefaultAdaptorFactory implements IAdaptorFactory
{

	protected static final ClassLoaderCache _clCache = ClassLoaderCache.getDefault();

	private Map _adaptorMap = new HashMap();

	/**
	 * create a default adaptor factory
	 *
	 */
	public DefaultAdaptorFactory () {
	}

	/** {@inheritDoc} */
	public void registerAdaptor (Class type, IAdaptor adaptor) {
		String typeName = type.getName();
		List adaptors = (List) _adaptorMap.get(typeName);
		if (adaptors == null) {
			adaptors = new LinkedList();
			_adaptorMap.put(typeName, adaptors);
		}
		adaptors.add(adaptor);
	}

	/** {@inheritDoc} */
	public IAdaptor[] getAdaptors (Class type) {
		return getAdaptors(type.getName());
	}

	/** {@inheritDoc} */
	public IAdaptor[] getAdaptors (String className) {
		List l = (List) _adaptorMap.get(className);
		return l == null ? new IAdaptor[0]
				: (IAdaptor[]) l.toArray(new IAdaptor[l.size()]);
	}

	/** {@inheritDoc} */
	public IAdaptor[] getAllAdaptors (Class targetType) {
		Class[] ints = targetType.getInterfaces();
		List adaptorList = new ArrayList();
		// check original target type and all of its supertypes
		do {
			List adaptors = (List) _adaptorMap.get(targetType.getName());
			if (adaptors != null && !adaptors.isEmpty()) {
				adaptorList.addAll(adaptors);
			}
		}while ((targetType = targetType.getSuperclass()) != null);
		// now check all of the target type's interfaces, checking each interface's
		// supertypes along the way
		for (int i = 0; i < ints.length; ++i) {
			List adaptors = (List) _adaptorMap.get(ints[i].getName());
			if (adaptors != null && !adaptors.isEmpty()) {
				adaptorList.addAll(adaptors);
			}
			List adaptors2 = Arrays.asList(getAllAdaptors(ints[i]));
			if (adaptors2 != null && !adaptors2.isEmpty()) {
				adaptorList.addAll(adaptors2);
			}
		}
		return adaptorList.isEmpty() ? new IAdaptor[0]
				: (IAdaptor[]) adaptorList.toArray(new IAdaptor[adaptorList.size()]);
	}
}
